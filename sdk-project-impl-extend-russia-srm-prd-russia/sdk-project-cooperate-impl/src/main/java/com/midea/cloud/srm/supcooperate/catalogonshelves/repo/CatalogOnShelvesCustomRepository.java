package com.midea.cloud.srm.supcooperate.catalogonshelves.repo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.QlContext;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.mall.request.jd.goods.QuerySkuDetailRequestDTO;
import com.midea.cloud.srm.mall.result.jd.goods.SkuDetailResultDTO;
import com.midea.cloud.srm.mall.service.jd.MallService;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.base.organization.entity.Site;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCurrency;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.dto.CatalogOnShelvesDTO;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelvesAttach;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.enums.CatalogOnShelvesStatusEnum;
import com.midea.cloud.srm.model.pm.pr.shopcart.entity.ShopCart;
import com.midea.cloud.srm.model.pm.pr.shopcart.enums.ShopCartStatus;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.agreement.enums.AgreementStatusEnums;
import com.midea.cloud.srm.model.supcooperate.enums.MallTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.ExtCatalogOnShelvesStatusEnum;
import com.midea.cloud.srm.supcooperate.catalogonshelves.utils.CatalogCommonUtil;
import com.midea.cloud.srm.supcooperate.mtmapping.service.ExternalMaterialService;
import com.midea.cloud.srm.supcooperate.spi.meiql.ShopCartSpiService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
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
 *  修改日期: 2023/10/27 10:22
 *  修改内容:
 * </pre>
 */
@Component
public class CatalogOnShelvesCustomRepository extends ProxyRepository {
    @Autowired
    protected QlService qlService;
    @Autowired
    private ShopCartSpiService shopCartSpiService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private CatalogCommonUtil catalogCommonUtil;
    private static final String LIST_PAGE_FOR_PURCHASE_CATALOG = "listPageForPurchaseCatalog";
    private static final String QUERY = "query";
    @Autowired
    private ExternalMaterialService externalMaterialService;
    @Autowired
    private MallService goodsService;

    public CatalogOnShelvesCustomRepository() {
        //注册action
        this.register("batchOnShelves", this::batchOnShelves, true, "批量上架");
        this.register("batchScheduledShelves", this::batchScheduledShelves, true, "批量定时上架");
        this.register("batchUntimedShelves", this::batchUntimedShelves, true, "批量取消定时上架");
        this.register("addToShoppingCart", this::addToShoppingCart, true, "采购目录-加入购物车");
        this.register("add", this::add, true, "新增商品-价格协议审批完成后，自动回写至商品上下架列表");
        this.register("onShelves", this::onShelves, true, "采购目录-上架");
        this.register(LIST_PAGE_FOR_PURCHASE_CATALOG, this::listPageForPurchaseCatalog, false, "采购目录-查询");
    }

    @Override
    public QlResult save(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        AssertUtils.notEmpty(recs, "参数不存在");
        if (UserType.VENDOR.name().equals(AppUserUtil.getLoginAppUser().getUserType())) {
            return this.saveOrUpdate(queryAction, recs.get(0), CatalogOnShelvesStatusEnum.TO_BE_SUBMIT.name());
        } else {
            String status = recs.get(0).get("extPriceLibraryStatus").equals(AgreementStatusEnums.EXECUTE.getCode())
                    ? ExtCatalogOnShelvesStatusEnum.UNTIMED_SHELVES.toString()
                    : ExtCatalogOnShelvesStatusEnum.TO_BE_ON_SHELVES.toString();
            return this.saveOrUpdate(queryAction, recs.get(0), status);
        }
    }

    private QlResult listPageForPurchaseCatalog(QlQueryAction queryAction) {
        return query(ProxyQlQueryAction.proxy(queryAction, "query"));
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = MeiQl.newCondition();
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        if (LIST_PAGE_FOR_PURCHASE_CATALOG.equals(QlContext.getRequestAction().getAction())) {
            QlCondition subCondition = MeiQl.newCondition();
            //依据区域+业务实体（公司）过滤对应的商品
            Record addr = catalogCommonUtil.getAddr(loginAppUser);
            //业务实体
            subCondition.contains("extOrgIdList", addr.get(Site::getOrganizationId));
            subCondition.eq(CatalogOnShelvesDTO::getStatus, CatalogOnShelvesStatusEnum.ON_SHELVES.name());
            qlCondition = qlCondition.and(subCondition);
        } else if (QUERY.equals(QlContext.getRequestAction().getAction())) {
            QlCondition subCondition = MeiQl.newCondition();
            if (UserType.VENDOR.name().equals(loginAppUser.getUserType())) {
                subCondition.eq(CatalogOnShelvesDTO::getVendorId, loginAppUser.getCompanyId());
                subCondition.in(CatalogOnShelvesDTO::getStatus, new String[]{
                        ExtCatalogOnShelvesStatusEnum.SCHEDULED_SHELVES.name(),
                        ExtCatalogOnShelvesStatusEnum.TO_BE_ON_SHELVES.name()
                });
            }
            qlCondition = qlCondition.and(subCondition);
        }
        return qlCondition;
    }

    @Override
    public void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        if (LIST_PAGE_FOR_PURCHASE_CATALOG.equals(queryAction.getAction())) {
            Map<Long, Long> fileMap = new HashMap<>(16);
            List<Long> catalogOnShelvesIds = records.stream().map(e -> e.get(CatalogOnShelvesDTO::getCatalogOnShelvesId)).collect(Collectors.toList());
            //主图ID
            if (CollectionUtils.isNotEmpty(catalogOnShelvesIds)) {
                List<Record> list = qlService.queryByWrapper(QlWrappers.query("CatalogOnShelvesAttach")
                        .in(CatalogOnShelvesAttach::getCatalogOnShelvesId, catalogOnShelvesIds)
                        .eq(CatalogOnShelvesAttach::getIfDefaultPicture, YesOrNo.Y)
                        .select(CatalogOnShelvesAttach::getCatalogOnShelvesId, CatalogOnShelvesAttach::getFileuploadId), Record.class);
                list.forEach(r -> {
                    if (!fileMap.containsKey(r.get(CatalogOnShelvesAttach::getCatalogOnShelvesId))) {
                        fileMap.put(r.get(CatalogOnShelvesAttach::getCatalogOnShelvesId), r.get(CatalogOnShelvesAttach::getFileuploadId));
                    }
                });
            }
            for (Record r : records) {
                //计算出距离下架日期（精确到小时数）
                countRemainingTime(r);
                if (fileMap.containsKey(r.get(CatalogOnShelvesDTO::getCatalogOnShelvesId))
                        && null != fileMap.get(r.get(CatalogOnShelvesDTO::getCatalogOnShelvesId))) {
                    r.put(CatalogOnShelvesDTO::getFileuploadId, fileMap.get(r.get(CatalogOnShelvesDTO::getCatalogOnShelvesId)));
                }
            }
        }
    }

    private QlResult onShelves(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Record record = recs.get(0);
        //上架人信息
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        record.put(CatalogOnShelvesDTO::getOnShelvesId, loginAppUser.getUserId());
        record.put(CatalogOnShelvesDTO::getOnShelvesBy, loginAppUser.getUsername());
        record.put(CatalogOnShelvesDTO::getOnShelvesDate, new Date());
        record.put(CatalogOnShelvesDTO::getOnShelvesFullName, loginAppUser.getNickname());
        //检查是否jd商品
        this.checkExternalMaterial(record);
        return this.saveOrUpdate(queryAction, record, CatalogOnShelvesStatusEnum.ON_SHELVES.name());
    }

    private void checkExternalMaterial(Record record) {
        //判断是否是关联外部商品
        ExternalMaterial externalMaterial = externalMaterialService.getOne(new LambdaQueryWrapper<ExternalMaterial>()
                .eq(ExternalMaterial::getMaterialCode, record.get(CatalogOnShelvesDTO::getMaterialCode)), false);
        record.put("extMaterialType", externalMaterial == null ? MallTypeEnum.CC.getCode() : externalMaterial.getMaterialType());
        if (externalMaterial != null) {
            //校验该物料对应的京东商品的上下架状态，若是下架状态则上架不成功，给出提示：该商品在京东平台已下架
            QuerySkuDetailRequestDTO querySkuDetailRequestDTO = QuerySkuDetailRequestDTO.builder()
                    .sku(externalMaterial.getSkuId())
                    .build();
            querySkuDetailRequestDTO.setMallType(MallTypeEnum.JD.getCode());
            //查询商品详情（京东接口限流，查询商品详情  每秒110次）
            SkuDetailResultDTO skuDetailResultDTO = goodsService.querySkuDetail(querySkuDetailRequestDTO);
            Assert.isTrue(skuDetailResultDTO.isSuccess(), "获取京东商品详情异常：" + skuDetailResultDTO.getResultMessage());
            Assert.isTrue("1".equals(skuDetailResultDTO.getResult().getState()), "该商品在京东平台已下架");
        }
    }

    public QlResult saveOrUpdateBatch(QlQueryAction queryAction, List<Record> records, String catalogOnShelvesStatus) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(records), "参数不存在");

        List<PurchaseCategory> purchaseCategories = baseClient.listCategoryByIds(records.stream().map(record -> record.get(CatalogOnShelvesDTO::getCategoryId)).distinct().collect(Collectors.toList()));
        Map<Long, PurchaseCategory> purchaseCategoryMap = purchaseCategories.stream().collect(Collectors.toMap(k -> k.getCategoryId(), Function.identity(), (k1, k2) -> k2));

        records.stream().forEach(record -> {
            record.put(CatalogOnShelvesDTO::getUserType, AppUserUtil.getLoginAppUser().getUserType());
            record.put(CatalogOnShelvesDTO::getStatus, catalogOnShelvesStatus);
            Long categoryId = record.getLong("categoryId");
            if (categoryId != null) {

                if (purchaseCategoryMap.containsKey(categoryId)) {
                    record.put(CatalogOnShelvesDTO::getStruct, purchaseCategoryMap.get(categoryId).getStruct());
                }
            }
        });

        return doSave(noProxy(queryAction), records);
    }

    public QlResult saveOrUpdate(QlQueryAction queryAction, Record record, String catalogOnShelvesStatus) {
        AssertUtils.notNull(record, "参数不存在");
        record.put(CatalogOnShelvesDTO::getUserType, AppUserUtil.getLoginAppUser().getUserType());
        record.put(CatalogOnShelvesDTO::getStatus, catalogOnShelvesStatus);
        Long categoryId = record.getLong("categoryId");
        if (categoryId != null) {
            List<PurchaseCategory> purchaseCategories = baseClient.listCategoryByIds(new ArrayList<>(Collections.singletonList(categoryId)));
            if (CollectionUtils.isNotEmpty(purchaseCategories)) {
                record.put(CatalogOnShelvesDTO::getStruct, purchaseCategories.get(0).getStruct());
            }
        }
        return doSave(noProxy(queryAction), Collections.singletonList(record));
    }

    private QlResult add(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        AssertUtils.notEmpty(records, "数据不能为空。");
        for (Record record : records) {
            record.set(CatalogOnShelves::getStatus, record.get("extPriceLibraryStatus").equals(AgreementStatusEnums.EXECUTE.getCode())
                    ? ExtCatalogOnShelvesStatusEnum.UNTIMED_SHELVES.toString()
                    : ExtCatalogOnShelvesStatusEnum.TO_BE_ON_SHELVES.toString());
            record.set(CatalogOnShelves::getOffShelvesDate, ObjectUtil.isEmpty(record.get(CatalogOnShelves::getOffShelvesDate)) && ObjectUtil.isNotEmpty(record.get(CatalogOnShelves::getExpirationDate))
                    ? record.get(CatalogOnShelves::getExpirationDate)
                    : record.get(CatalogOnShelves::getOffShelvesDate));
        }
        return super.save(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value(), records));
    }

    private QlResult addToShoppingCart(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        AssertUtils.notEmpty(recs, "参数不存在");
        List<Record> shopCarts = new ArrayList<>();
        List<Record> list = new ArrayList<>();
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        for (Record rec : recs) {
            Record record = qlService.readByKey(queryAction.getType(), rec.get(CatalogOnShelves::getCatalogOnShelvesId), Record.class);
            if (ObjectUtil.isNotEmpty(record.get(CatalogOnShelves::getOrderQuantityMinimum))) {
                Assert.isTrue(ObjectUtil.isNotEmpty(rec.get("requirementNum")) && Convert.toBigDecimal(rec.get("requirementNum")).compareTo(record.get(CatalogOnShelves::getOrderQuantityMinimum)) >= 0, "订购数量要大于等于最小起订量");
            }
            list.add(record);
            Record sc = new Record();
            sc.put(ShopCart::getIfCatalog, com.midea.cloud.common.enums.YesOrNo.YES.getValue());
            // 单价（含税）= 预算单价
            sc.put(ShopCart::getUnitPrice, record.get("extReferencePrice"));
            sc.put(ShopCart::getStatus, ShopCartStatus.DRAFT.getCode());
            // 供应商
            sc.put(ShopCart::getSupplierId, record.get(CatalogOnShelvesDTO::getVendorId));
            sc.put(ShopCart::getSupplierCode, record.get(CatalogOnShelvesDTO::getVendorCode));
            sc.put(ShopCart::getSupplierName, record.get(CatalogOnShelvesDTO::getVendorName));
            // 用户
            sc.put(ShopCart::getAddToUserId, loginAppUser.getUserId());
            sc.put(ShopCart::getAddToEmpNo, loginAppUser.getUsername());
            sc.put(ShopCart::getAddToNickname, loginAppUser.getNickname());
            // 库存组织
            sc.put(ShopCart::getOrganizationId, record.get(CatalogOnShelvesDTO::getOrganizationId));
            sc.put(ShopCart::getOrganizationCode, record.get(CatalogOnShelvesDTO::getOrganizationCode));
            sc.put(ShopCart::getOrganizationName, record.get(CatalogOnShelvesDTO::getOrganizationName));
            // 小类
            sc.put(ShopCart::getCategoryId, record.get(CatalogOnShelvesDTO::getCategoryId));
            sc.put(ShopCart::getCategoryName, record.get(CatalogOnShelvesDTO::getCategoryName));
            sc.put(ShopCart::getCategoryCode, record.get(CatalogOnShelvesDTO::getCategoryCode));
            // 物料
            sc.put(ShopCart::getMaterialId, record.get(CatalogOnShelvesDTO::getMaterialId));
            sc.put(ShopCart::getMaterialCode, record.get(CatalogOnShelvesDTO::getMaterialCode));
            sc.put(ShopCart::getMaterialName, record.get(CatalogOnShelvesDTO::getMaterialName));
            sc.put(ShopCart::getSpecification, record.get(CatalogOnShelvesDTO::getSpecification));
            sc.put(ShopCart::getUnit, record.get(CatalogOnShelvesDTO::getUnitCode));
            sc.put(ShopCart::getUnitName, record.get(CatalogOnShelvesDTO::getUnit));
            // 币种
            sc.put(ShopCart::getCurrencyId, record.get(CatalogOnShelvesDTO::getCurrencyId));
            sc.put(ShopCart::getCurrencyCode, record.get(CatalogOnShelvesDTO::getCurrencyCode));
            if (StringUtils.isNotEmpty(record.get(CatalogOnShelvesDTO::getCurrencyName))) {
                sc.put(ShopCart::getCurrencyName, record.get(CatalogOnShelvesDTO::getCurrencyName));
            } else {
                List<String> currencyCodes = Collections.singletonList(record.get(CatalogOnShelvesDTO::getCurrencyCode));
                if (CollectionUtils.isNotEmpty(currencyCodes)) {
                    List<PurchaseCurrency> purchaseCurrencyList = baseClient.listPurchaseCurrencyAnon(currencyCodes);
                    if (ObjectUtil.isNotEmpty(purchaseCurrencyList)) {
                        sc.put(ShopCart::getCurrencyName, purchaseCurrencyList.get(0).getCurrencyName());
                        sc.put(ShopCart::getCurrencyId, purchaseCurrencyList.get(0).getCurrencyId());
                    }
                }
            }
            //数量
            sc.put(ShopCart::getRequirementNum, rec.get("requirementNum"));
            //默认汇总人为当前创建人
            sc.put(ShopCart::getSummaryUserId, loginAppUser.getUserId());
            sc.put(ShopCart::getSummaryEmpNo, loginAppUser.getUsername());
            sc.put(ShopCart::getSummaryNickname, loginAppUser.getNickname());
            //赋值扩展字段
            List<Record> shelvesAttach = qlService.queryByWrapper(QlWrappers.query(CatalogOnShelvesAttach.class)
                    .eq(CatalogOnShelvesAttach::getCatalogOnShelvesId, rec.get(CatalogOnShelves::getCatalogOnShelvesId))
                    //查询主图
                    .eq(CatalogOnShelvesAttach::getIfDefaultPicture, Enable.Y.name()), Record.class);
            //获取二级品类
            PurchaseCategory purchaseCategory = catalogCommonUtil.getSecondCategory(record);
            sc.put("extShelvesAttachId", ObjectUtil.isNotEmpty(shelvesAttach) ? shelvesAttach.get(0).get(CatalogOnShelvesAttach::getAttachId) : null);
            sc.put("extOrderQuantityMinimum", record.get(CatalogOnShelves::getOrderQuantityMinimum));
            sc.put("extSecondCategoryId", purchaseCategory.getCategoryId());
            sc.put("extSecondCategoryCode", purchaseCategory.getCategoryCode());
            sc.put("extSecondCategoryName", purchaseCategory.getCategoryName());
            Record addr = catalogCommonUtil.getAddr(loginAppUser);
            //收货id
            sc.put("extAddressId", addr.get("siteId"));
            //收货地址
            sc.put("extAddressName", addr.get("siteName"));
            //收货人
            sc.put("extReceiver", addr.get("receiver"));
            //收货人联系方式
            sc.put("extReceiverContact", addr.get("receiverPhone"));
            //使用部门
            sc.put("extCeeaDeptid", addr.get("departmentOrganizationId"));
            sc.put("extDepartmentcode", addr.get("departmentOrganizationCode"));
            sc.put("extDepartment", addr.get("departmentOrganizationName"));
            // 业务实体
            sc.put(ShopCart::getOrgId, addr.get(Site::getOrganizationId));
            sc.put(ShopCart::getOrgCode, addr.get(Site::getOrganizationCode));
            sc.put(ShopCart::getOrgName, addr.get(Site::getOrganizationName));
            sc.put("extIsGoods", ObjectUtil.isNotEmpty(rec.get("extIsGoods")) ? rec.get("extIsGoods") : Enable.Y.name());
            sc.put("extAreaId", rec.get("extAreaId"));
            sc.put("extAreaCode", rec.get("extAreaCode"));
            sc.put("extAreaName", rec.get("extAreaName"));
            sc.put("extReferencePrice", record.get("extReferencePrice"));
            sc.put("extCatalogOnShelvesId", rec.get(CatalogOnShelves::getCatalogOnShelvesId));
            //外部商品判断
            sc.put("extMaterialType", externalMaterialService.getMaterialType(record.get(CatalogOnShelves::getMaterialCode)));
            shopCarts.add(sc);
        }
        //采购目录转换购物车SPI
        shopCartSpiService.catalogOnShelvesChangeShopCart(shopCarts, list);
        qlService.create("ShopCart", shopCarts);
        return new QlResult(queryAction.getType());
    }


    private QlResult batchUntimedShelves(QlQueryAction queryAction) {
        //查询商品
        List<Record> catalogOnShelvesList = this.getbyIds(queryAction);
        catalogOnShelvesList.forEach(record -> {
            Assert.isTrue((record.getString("status").equals(ExtCatalogOnShelvesStatusEnum.SCHEDULED_SHELVES.name())), "其所勾选行存在无需取消定时上架的商品，请检查");
            record.set(CatalogOnShelves::getStatus, ExtCatalogOnShelvesStatusEnum.UNTIMED_SHELVES.name());
        });
        return super.update(ProxyQlQueryAction.proxy(queryAction, DefaultAction.UPDATE.value(), catalogOnShelvesList));
    }

    private QlResult batchScheduledShelves(QlQueryAction queryAction) {
        //查询商品
        List<Record> catalogOnShelvesList = this.getbyIds(queryAction);
        catalogOnShelvesList.forEach(record1 -> Assert.isTrue((record1.getString("status").equals(ExtCatalogOnShelvesStatusEnum.UNTIMED_SHELVES.name())), "其所勾选行存在无需取消定时上架的商品，请检查"));
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //上架人信息
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        recs.stream().forEach(record -> {
            record.put(CatalogOnShelvesDTO::getOnShelvesId, loginAppUser.getUserId());
            record.put(CatalogOnShelvesDTO::getOnShelvesBy, loginAppUser.getUsername());
            record.put(CatalogOnShelvesDTO::getOnShelvesDate, new Date());
            record.put(CatalogOnShelvesDTO::getOnShelvesFullName, loginAppUser.getNickname());
        });


        return this.saveOrUpdateBatch(queryAction, recs, ExtCatalogOnShelvesStatusEnum.SCHEDULED_SHELVES.name());
    }

    private QlResult batchOnShelves(QlQueryAction queryAction) {
        //查询商品
        List<Record> catalogOnShelvesList = this.getbyIds(queryAction);
        catalogOnShelvesList.forEach(record -> {
            Assert.isTrue((record.getString("status").equals(ExtCatalogOnShelvesStatusEnum.TO_BE_ON_SHELVES.name())
                    || record.getString("status").equals(ExtCatalogOnShelvesStatusEnum.OFF_SHELVES.name()))
                    && (ObjectUtil.isNotEmpty(record.getString("expirationDate")) && DateUtil.parse(record.getString("expirationDate")).getTime() > System.currentTimeMillis()), "其所勾选行，存在商品状态为非待上架或者已下架的商品，不允许批量上架");
            record.set(CatalogOnShelves::getStatus, ExtCatalogOnShelvesStatusEnum.ON_SHELVES);
        });
        return super.update(ProxyQlQueryAction.proxy(queryAction, DefaultAction.UPDATE.value(), catalogOnShelvesList));
    }

    private List<Record> getbyIds(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //
        List<Long> ids = records.stream().map(record -> record.get(CatalogOnShelves::getCatalogOnShelvesId)).collect(Collectors.toList());
        //查询商品
        return qlService.readByKeys("CatalogOnShelves", ids, Record.class);
    }

    private void countRemainingTime(Record catalogOnShelves) {
        if (null != catalogOnShelves.get(CatalogOnShelvesDTO::getExpirationDate)) {
            Date expirationDate = Date.from(catalogOnShelves.get(CatalogOnShelvesDTO::getExpirationDate).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Calendar instance = Calendar.getInstance();
            instance.setTime(expirationDate);
            instance.set(Calendar.HOUR_OF_DAY, 23);
            instance.set(Calendar.MINUTE, 59);
            instance.set(Calendar.SECOND, 59);
            Date endDateTime = instance.getTime();
            // 一天的毫秒数
            long nd = 1000 * 24 * 60 * 60;
            // 一小时的毫秒数
            long nh = 1000 * 60 * 60;
            long diff;
            long day = 0;
            long hour = 0;
            diff = endDateTime.getTime() - System.currentTimeMillis();
            day = diff / nd;
            hour = diff % nd / nh;
            catalogOnShelves.put(CatalogOnShelvesDTO::getRemainingTime, String.format("%s天%s小时", day, hour));
        }
    }
}
