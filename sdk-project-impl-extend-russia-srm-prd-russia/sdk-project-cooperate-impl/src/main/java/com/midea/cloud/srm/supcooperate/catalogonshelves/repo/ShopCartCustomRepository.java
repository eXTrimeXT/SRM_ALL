package com.midea.cloud.srm.supcooperate.catalogonshelves.repo;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.DateChangeUtil;
import com.midea.cloud.meiql.api.enums.QlQueryFeature;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.OpenApiUtil;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.inq.price.entity.PriceLibrary;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApplyStatus;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.pm.pr.shopcart.entity.ShopCart;
import com.midea.cloud.srm.model.pm.pr.shopcart.enums.ShopCartStatus;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.supcooperate.catalogonshelves.utils.CatalogCommonUtil;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.mtmapping.service.ExternalMaterialService;
import com.midea.cloud.srm.supcooperate.spi.meiql.ShopCartSpiService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/12/4 10:17
 *  修改内容:
 * </pre>
 */
@Component
public class ShopCartCustomRepository extends ProxyRepository {
    @Autowired
    private CatalogCommonUtil catalogCommonUtil;
    @Autowired
    protected QlService qlService;
    @Autowired
    private ShopCartSpiService shopCartSpiService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private BaseExtClient baseExtClient;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private ExternalMaterialService externalMaterialService;
    public ShopCartCustomRepository() {
        //注册action
        this.register("add", this::add, true, "购物车-新增");
        this.register("approved", this::approved, true, "购物车-审批");
        this.register("createRequirements", this::createRequirements, true, "创建采购申请");
        this.register("submit", this::submit, true, "提交需求");
        this.register("withdraw", this::withdraw, true, "撤回");
        this.register("reject", this::reject, true, "驳回");
        this.register("close", this::close, true, "关闭");
    }

    private QlResult submit(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        AssertUtils.notEmpty(recs, "参数不存在");
        recs.forEach(record -> {
            record.put(ShopCart::getStatus, ShopCartStatus.SUBMITTED.getCode());
        });
        return this.add(queryAction);
    }

    private QlResult createRequirements(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        AssertUtils.notEmpty(recs, "参数不存在");
        List<Long> ids = recs.stream().map(r -> r.get(ShopCart::getShopCartId)).collect(Collectors.toList());
        AssertUtils.notEmpty(ids, "请选择购物车行数据");
        List<Record> shopCarts = qlService.queryByWrapper(QlWrappers.query(queryAction.getType())
                .in(ShopCart::getShopCartId, ids), Record.class);
        // 获取当前登录用户
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        // 按 采购类型+业务实体+库存组织+物料大类+是否目录化物料 分组
        Map<String, List<Record>> shopCartMap = new HashMap<>(16);
        //优化下面代码
        List<Long> categoryIds = shopCarts.stream()
                .map(e -> e.get(ShopCart::getCategoryId))
                .distinct()
                .collect(Collectors.toList());
        //批量查询大类
        List<PurchaseCategory> bigPurchaseCategorys = baseExtClient.queryMaxLevelCategoryList(categoryIds);
        //转换map，CategoryId等于传过去的CategoryId，因为在上面方法里进行过重新赋值
        Map<Long, PurchaseCategory> bigPurchaseCategoryMap = bigPurchaseCategorys.stream().collect(Collectors.toMap(PurchaseCategory::getCategoryId, e -> e));
        //遍历赋值
        shopCarts.forEach(shopCart -> {
            shopCart.put(ShopCart::getBigPurchaseCategory, bigPurchaseCategoryMap.get(shopCart.get(ShopCart::getCategoryId)));
            shopCartMap.computeIfAbsent(shopCart.get(ShopCart::getOrgId).toString(), k -> new ArrayList<>()).add(shopCart);
        });
       /* for (Record shopCart : shopCarts) {
            PurchaseCategory bigPurchaseCategory = baseExtClient.queryMaxLevelCategory(new PurchaseCategory().setCategoryId(shopCart.get(ShopCart::getCategoryId)));
            Assert.notNull(bigPurchaseCategory, "获取大类异常");
            bigPurchaseCategory.setCategoryId(shopCart.get(ShopCart::getCategoryId));
            shopCart.put(ShopCart::getBigPurchaseCategory, bigPurchaseCategory);

            String key = shopCart.get(ShopCart::getOrgId).toString();
            shopCartMap.computeIfAbsent(key, k -> new ArrayList<>()).add(shopCart);
        }*/
        //更新数据
        List<Record> shopCartUpdateList = new ArrayList<>();
        List<Record> requirementHeadUpdateList = new ArrayList<>();
        HrUserOrgnizationDto hrUserOrgnizationByUsername = pjProjectExtClient.getHrUserOrgnizationByUsername(loginAppUser.getUsername());
        //创建采购申请
        shopCartMap.forEach((key, shopCartList) -> {
            //物料大类
            Record shopCart = shopCartList.get(0);
            PurchaseCategory bigPurchaseCategory = shopCart.get(ShopCart::getBigPurchaseCategory);
            //构建采购申请头
            Record requirementHead = new Record();
            requirementHead.put(RequirementHead::getRequirementHeadNum, baseClient.seqGen(SequenceCodeConstant.SEQ_PMP_PR_APPLY_NUM));
            requirementHead.put(RequirementHead::getAuditStatus, RequirementApproveStatus.DRAFT);
            requirementHead.put(RequirementHead::getApplyDate, DateChangeUtil.asLocalDate(new Date()));
            requirementHead.put(RequirementHead::getCeeaDepartmentName, loginAppUser.getDepartment());// 部门
            requirementHead.put(RequirementHead::getCeeaPurchaseType, shopCart.get(ShopCart::getPurchaseType));
            requirementHead.put(RequirementHead::getOrgId, shopCart.get(ShopCart::getOrgId));
            requirementHead.put(RequirementHead::getOrgCode, shopCart.get(ShopCart::getOrgCode));
            requirementHead.put(RequirementHead::getOrgName, shopCart.get(ShopCart::getOrgName));
            requirementHead.put(RequirementHead::getOrganizationId, shopCart.get(ShopCart::getOrganizationId));
            requirementHead.put(RequirementHead::getOrganizationCode, shopCart.get(ShopCart::getOrganizationCode));
            requirementHead.put(RequirementHead::getOrganizationName, shopCart.get(ShopCart::getOrganizationName));
            requirementHead.put(RequirementHead::getCategoryId, bigPurchaseCategory.getCategoryId());
            requirementHead.put(RequirementHead::getCategoryCode, bigPurchaseCategory.getCategoryCode());
            requirementHead.put(RequirementHead::getCategoryName, bigPurchaseCategory.getCategoryName());
            //二开扩展
            requirementHead.put(RequirementHead::getDemandType, "Material_category");//固定物资类型
            requirementHead.put("extBidFlag", Enable.N.name());//是否招标默认为否

            if (ObjectUtil.isNotEmpty(hrUserOrgnizationByUsername)) {
                //板块
                requirementHead.put(PurchaseRequirementHeadDTO::getExtOrgBuId, hrUserOrgnizationByUsername.getBuOrganization().getOrganizationId());
                requirementHead.put(PurchaseRequirementHeadDTO::getExtOrgBuCode, hrUserOrgnizationByUsername.getBuOrganization().getOrganizationCode());
                requirementHead.put(PurchaseRequirementHeadDTO::getExtOrgBuName, hrUserOrgnizationByUsername.getBuOrganization().getOrganizationName());
                //业务实体
                requirementHead.put(PurchaseRequirementHeadDTO::getOrgId, hrUserOrgnizationByUsername.getOuOrganization().getOrganizationId());
                requirementHead.put(PurchaseRequirementHeadDTO::getOrgCode, hrUserOrgnizationByUsername.getOuOrganization().getOrganizationCode());
                requirementHead.put(PurchaseRequirementHeadDTO::getOrgName, hrUserOrgnizationByUsername.getOuOrganization().getOrganizationName());
                //申请人
                requirementHead.put("applyById", loginAppUser.getUserId());
                requirementHead.put(PurchaseRequirementHeadDTO::getApplyCode, loginAppUser.getUsername());
                requirementHead.put(PurchaseRequirementHeadDTO::getApplyBy, loginAppUser.getUsername());
                requirementHead.put(PurchaseRequirementHeadDTO::getApplyByNickname, loginAppUser.getNickname());
                //申请人部门
                requirementHead.put(PurchaseRequirementHeadDTO::getCeeaDepartmentId, hrUserOrgnizationByUsername.getDepartmentOrganization().getOrganizationId());
                requirementHead.put(PurchaseRequirementHeadDTO::getCeeaDepartmentName, hrUserOrgnizationByUsername.getDepartmentOrganization().getOrganizationName());
            }

            List<Record> lineList = new ArrayList<>();
            //生成采购申请明细
            List<Long> extCatalogOnShelvesIds = shopCartList.stream().map(e -> e.getLong("extCatalogOnShelvesId")).collect(Collectors.toList());
            Map<Long, Record> extCatalogOnShelvesIdMap = null;
            if (CollectionUtils.isNotEmpty(extCatalogOnShelvesIds)) {
                List<Record> catalogOnShelvesList = qlService.queryByWrapper(QlWrappers.query("CatalogOnShelves").in(CatalogOnShelves::getCatalogOnShelvesId, extCatalogOnShelvesIds), Record.class);
                extCatalogOnShelvesIdMap = catalogOnShelvesList.stream().collect(Collectors.toMap(r -> r.getLong("extCatalogOnShelvesId"), r -> r, (a, b) -> b));
            }
            for (Record cart : shopCartList) {
                //京东商品判断，如果时京东商品，则需要判断商品状态和库存
                externalMaterialService.checkStateByMaterialType(requirementHead, cart);
                //构建采购申请行
                Record requirementLine = new Record();
                requirementLine.put(RequirementLine::getRequirementHeadNum, requirementHead.get(RequirementHead::getRequirementHeadNum));
                requirementLine.put(RequirementLine::getApplyStatus, RequirementApplyStatus.UNASSIGNED);
                requirementLine.put(RequirementLine::getOrgId, cart.get(ShopCart::getOrgId));
                requirementLine.put(RequirementLine::getOrgCode, cart.get(ShopCart::getOrgCode));
                requirementLine.put(RequirementLine::getOrgName, cart.get(ShopCart::getOrgName));
                requirementLine.put(RequirementLine::getOrganizationId, cart.get(ShopCart::getOrganizationId));
                requirementLine.put(RequirementLine::getOrganizationCode, cart.get(ShopCart::getOrganizationCode));
                requirementLine.put(RequirementLine::getOrganizationName, cart.get(ShopCart::getOrganizationName));
                requirementLine.put(RequirementLine::getCategoryId, cart.get(ShopCart::getCategoryId));
                requirementLine.put(RequirementLine::getCategoryCode, cart.get(ShopCart::getCategoryCode));
                requirementLine.put(RequirementLine::getCategoryName, cart.get(ShopCart::getCategoryName));
                requirementLine.put(RequirementLine::getCeeaIfDirectory, cart.get(ShopCart::getIfCatalog));
                requirementLine.put(RequirementLine::getMaterialId, cart.get(ShopCart::getMaterialId));
                requirementLine.put(RequirementLine::getMaterialCode, cart.get(ShopCart::getMaterialCode));
                requirementLine.put(RequirementLine::getMaterialName, cart.get(ShopCart::getMaterialName));
                requirementLine.put(RequirementLine::getUnitCode, cart.get(ShopCart::getUnit));
                requirementLine.put(RequirementLine::getUnit, cart.get(ShopCart::getUnitName));
                requirementLine.put(RequirementLine::getCurrencyId, cart.get(ShopCart::getCurrencyId));
                requirementLine.put(RequirementLine::getCurrencyCode, cart.get(ShopCart::getCurrencyCode));
                requirementLine.put(RequirementLine::getCurrencyName, cart.get(ShopCart::getCurrencyName));
                requirementLine.put(RequirementLine::getRequirementQuantity, cart.get(ShopCart::getRequirementNum));
                requirementLine.put(RequirementLine::getRequirementDate, cart.get(ShopCart::getRequirementDate));
                requirementLine.put(RequirementLine::getShopCartId, cart.get(ShopCart::getShopCartId));
                PriceLibrary pl = new PriceLibrary();
                pl.setItemId(cart.get(ShopCart::getMaterialId));
                pl.setIfQuote(YesOrNo.YES.getValue());
                pl.setCeeaOrgId(cart.get(ShopCart::getOrgId));
                pl.setCeeaOrganizationId(cart.get(ShopCart::getOrganizationId));
                if (ObjectUtil.isNotEmpty(cart.getLong("extCatalogOnShelvesId")) && extCatalogOnShelvesIdMap != null && extCatalogOnShelvesIdMap.get(cart.getLong("extCatalogOnShelvesId")) != null) {
                    Record catalogOnShelves = extCatalogOnShelvesIdMap.get(cart.getLong("extCatalogOnShelvesId"));
                    requirementLine.put(RequirementLine::getVendorId, catalogOnShelves.get(CatalogOnShelves::getVendorId));
                    requirementLine.put(RequirementLine::getVendorCode, catalogOnShelves.get(CatalogOnShelves::getVendorCode));
                    requirementLine.put(RequirementLine::getVendorName, catalogOnShelves.get(CatalogOnShelves::getVendorName));
                }
                // 预算价格=价格库中含税单价，指定供应商为价格库中供应商
                requirementLine.put(RequirementLine::getNotaxPrice, cart.get(ShopCart::getUnitPrice));
                requirementLine.put(RequirementLine::getTotalAmount, cart.get(ShopCart::getUnitPrice).multiply(requirementLine.get(RequirementLine::getRequirementQuantity)).setScale(8, RoundingMode.UP));
                //添加二开扩展字段
                requirementLine.put("extMaterialModel", cart.get(ShopCart::getSpecification));
                requirementLine.put("extUseDepartmentId", cart.get("extCeeaDeptid"));
                requirementLine.put("extUseDepartmentCode", cart.get("extDepartmentcode"));
                requirementLine.put("extUseDepartmentName", cart.get("extDepartment"));
                requirementLine.put("extPredictPrice", cart.get(ShopCart::getUnitPrice));
                requirementLine.put("extPredictAmount", cart.get(ShopCart::getUnitPrice).multiply(requirementLine.get(RequirementLine::getRequirementQuantity)).setScale(8, RoundingMode.UP));
                requirementLine.put("receiveAddress", cart.get("extAddressName"));
                requirementLine.put("extReceiver", cart.get("extReceiver"));
                requirementLine.put("receiveTelephone", cart.get("extReceiverContact"));
                requirementLine.put("extAreaCode", cart.get("extAreaCode"));
                requirementLine.put("extUserName", cart.get(ShopCart::getCreatedFullName));
                requirementLine.put("extUserCode", cart.get(ShopCart::getCreatedBy));
                requirementLine.put("extUseTo", cart.get("extUseTo"));
                requirementLine.put("brand", cart.get("brand"));
                requirementLine.put("comments", cart.get("extBuyTypeComment"));
                requirementLine.put("extAttachId", cart.get("extAttachId"));
                requirementLine.put("extAttachName", cart.get("extAttachName"));
                requirementLine.put("extProductFlag", cart.get("extIsGoods"));
                requirementLine.put("extUserPhone", cart.get("extUserPhone"));
                //更新数据
                lineList.add(requirementLine);
                Record sc = new Record();
                sc.put(ShopCart::getStatus, ShopCartStatus.APPLIED.getCode());// 已生成申请单
                sc.put(ShopCart::getRequirementHeadNum, requirementHead.get(RequirementHead::getRequirementHeadNum));// 采购申请单号
                sc.put(ShopCart::getShopCartId, cart.get(ShopCart::getShopCartId));
                shopCartUpdateList.add(sc);
            }
            requirementHead.put("reqLineList", lineList);
            //采购申请单-预算总金额
            BigDecimal totalBudget = lineList.stream().map(e -> e.get(RequirementLine::getTotalAmount))
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(8, RoundingMode.UP);
            requirementHead.put(RequirementHead::getTotalBudget, totalBudget);
            requirementHeadUpdateList.add(requirementHead);
        });
        super.save(OpenApiUtil.convertSaveRequest("PurchaseRequirementHead", "save", requirementHeadUpdateList));
        return super.update(OpenApiUtil.convertSaveRequest(queryAction.getType(), "update", shopCartUpdateList));
    }

    private QlResult approved(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        List<Record> records = new ArrayList<>();
        recs.forEach(record -> {
            Assert.isTrue(record.getLong("deptLeaderUserId").equals(AppUserUtil.getLoginAppUser().getUserId()), "审批失败：存在审批人不属于当前操作人的商品");
            Record record1 = new Record();
            record1.set(ShopCart::getShopCartId, record.get(ShopCart::getShopCartId));
            record1.set(ShopCart::getStatus, "APPROVED");
            records.add(record1);
        });
        qlService.update("ShopCart", records, QlQueryFeature.EXCLUDE_NULL);
        return new QlResult();
    }

    private QlResult add(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //获取二级品类
        recs.forEach(record -> {
            //参考价赋值到预算单价，用于后续生成申请单
            record.put(ShopCart::getUnitPrice, record.getBigDecimal("extReferencePrice"));
            if (ObjectUtil.isNotEmpty(record.get(CatalogOnShelves::getCategoryId))) {
                //二级品类
                PurchaseCategory purchaseCategory = catalogCommonUtil.getSecondCategory(record);
                record.put("extSecondCategoryId", purchaseCategory.getCategoryId());
                record.put("extSecondCategoryCode", purchaseCategory.getCategoryCode());
                record.put("extSecondCategoryName", purchaseCategory.getCategoryName());
            }
            //外部商品判断
            record.put("extMaterialType", externalMaterialService.getMaterialType(record.get(CatalogOnShelves::getMaterialCode)));
        });
        return doSave(noProxy(queryAction), recs);
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        return MeiQl.newCondition();
    }

    private QlResult withdraw(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        recs.forEach(r -> r.put(ShopCart::getStatus, "WITHDRAW"));
        return super.update(OpenApiUtil.convertSaveRequest(queryAction.getType(), "update", recs));
    }

    private QlResult reject(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        recs.forEach(r -> r.put(ShopCart::getStatus, "REJECTED"));
        return super.update(OpenApiUtil.convertSaveRequest(queryAction.getType(), "update", recs));
    }

    private QlResult close(QlQueryAction queryAction) {

        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        recs.forEach(r -> {
            r.put(ShopCart::getStatus, "CLOSE");
        });
        return super.update(OpenApiUtil.convertSaveRequest(queryAction.getType(), "update", recs));
    }

}
