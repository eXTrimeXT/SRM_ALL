package com.midea.cloud.srm.sou.inq.ext.plugin.event.init.editrequire;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempLine;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempFieldTypeEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempRelateTypeEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempStatusEnum;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouRequireInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.quotetemplate.dao.SouQuoteTempFieldRepository;
import com.midea.cloud.srm.sou.quotetemplate.dao.SouQuoteTempLineRepository;
import com.midea.cloud.srm.sou.quotetemplate.dao.SouQuoteTempRepository;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editrequrie.ApiSouRequireEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editrequrie.SouRequireEditContext;
import com.midea.cloud.srm.sou.sourcing.spi.init.editrequrie.SouRequireEditPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 物料需求保存校验转换处理
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/29
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouRequireEditHandler extends ApiSouRequireEditHandler {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouQuoteTempRepository souQuoteTempRepository;
    @Autowired
    private SouQuoteTempLineRepository souQuoteTempLineRepository;
    @Autowired
    private SouQuoteTempFieldRepository souQuoteTempFieldRepository;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private BaseExtClient baseExtClient;
    @Override
    public SouRequireEditPO formatValidateAndConvert(ApiSouRequireInfoDTO param, boolean isCopy, @Nullable Long userId, String souType) {
        AssertUtils.notNull(param.getProjectId(), "缺少projectId参数");
        if (param.getItemList() == null) {
            param.setItemList(new ArrayList<>());
        }

        // 1: 构造业务所需的上下文数据，并保存到上下文中
        SouRequireEditContext.setContextHolder(this.buildContextData(param, userId));
        try {
            // 2: 修改报价类型、报价模板id、报价模板名称
            if (param.getOrderType() != null) {
                SouProject project = SouRequireEditContext.getContextHolder().getProject();
                project.setOrderType(param.getOrderType());
                if (SouOrderTypeEnum.MATERIAL_COST_SEPARATION.equals(param.getOrderType())) {
                    project.setQuoteTempId(param.getQuoteTempId());
                    project.setQuoteTempName(param.getQuoteTempName());
                    if (project.getQuoteTempId() == null) {
                        AssertUtils.isTrue(param.isTempSave(), "请选择报价模板");
                    } else {
                        SouQuoteTemp quoteTemp = SouRequireEditContext.getContextHolder().getQuoteTempMap().get(project.getQuoteTempId());
                        AssertUtils.notNull(quoteTemp, LocaleHandler.getLocaleMsg("报价模板[{0} - {1}]不存在"), project.getQuoteTempId(), project.getQuoteTempName());
                        AssertUtils.isTrue(SouQuoteTempStatusEnum.VALID.equals(quoteTemp.getTempStatus()), "所选的报价模板不是生效状态");
                        AssertUtils.isTrue(SouQuoteTempRelateTypeEnum.ALL_DIM.equals(quoteTemp.getTempRelateType()), "所选择的报价模板不是全局维度的");
                        project.setQuoteTempName(quoteTemp.getTempName());

                        // 所选择的报价模板，其中作为标记总价的报价属性，只能全是公式类型字段(这样报价导入的时候，将汇总列表给屏蔽掉)
                        List<SouQuoteTempLine> tempLineList = souQuoteTempLineRepository.lambdaQuery()
                                .eq(SouQuoteTempLine::getTempId, quoteTemp.getTempId())
                                .list();
                        if (tempLineList.size() > 1) {
                            SouQuoteTempLine tempLine = tempLineList.stream().filter(e -> Enable.Y.equals(e.getIsTotal())).findFirst().orElse(null);
                            AssertUtils.notNull(tempLine, "找不到总价标识的字段");
                            List<SouQuoteTempField> fieldList = souQuoteTempFieldRepository.list(SouQuoteTempField::getAttrId, tempLine.getAttrId());
                            AssertUtils.isTrue(fieldList.stream().allMatch(f -> SouQuoteTempFieldTypeEnum.FORMULA.equals(f.getFieldType())),
                                    "所选择的报价模板不合适(报价模板中的总价属性里所有字段必须是公式类型)");
                        }
                    }
                } else {
                    project.setQuoteTempId(null);
                    project.setQuoteTempName(null);
                }
            }
            // 3: 数据格式化及校验
            this.formatAndValidate(param.getItemList(), param.isTempSave(), isCopy);
            // 4: 数据转换
            return this.convert(param.getProjectId(), param.getItemList(), isCopy);
        } finally {
            // 5: 清除业务上下文
            SouRequireEditContext.remove();
        }
    }

    @Override
    protected void formatAndValidate(List<ApiSouItemDTO> params, boolean isTempSave, boolean isCopy) {
        // 1: 格式化及校验物料行
        this.formatAndValidateItems(params, isTempSave, isCopy);
        // 2: 格式化及校验阶梯价
        this.formatAndValidateLadders(params, isTempSave, isCopy);
        // 3: 数据唯一性校验
        this.validateUniqueItems(params, isTempSave, isCopy);
    }

    @Override
    protected SouRequireEditContext buildContextData(ApiSouRequireInfoDTO param, @Nullable Long userId) {
        // 1: 查询寻源单
        SouProject project = souProjectDAO.getById(param.getProjectId());
        // 2: 查询物料信息
        Map<Long/* itemId */, MaterialItem> itemMap = Collections.emptyMap();
        {
            Set<Long> itemIds = param.getItemList().stream().map(ApiSouItemDTO::getItemId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!itemIds.isEmpty()) {
                itemMap = baseClient.listMaterialItemsByIds(new ArrayList<>(itemIds))
                        .stream().collect(Collectors.toMap(MaterialItem::getMaterialId, Function.identity()));
            }
        }
        // 3: 查询品类信息(用于校验无编码物料所选的品类)
        Map<String/* categoryCode */, PurchaseCategory> categoryMap = Collections.emptyMap();
        {
            Set<String> categoryCodes = param.getItemList().stream()
                    .filter(e -> Enable.Y.equals(e.getNoCodeItem()))
                    .map(ApiSouItemDTO::getCategoryCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (!categoryCodes.isEmpty()) {
                categoryMap = baseClient.getCategoryByCodes(categoryCodes);
            }
        }
        // 4: 查询当前用户有权限的OU信息
        Map<String/* orgCode */, Organization> authOrgMap;
        {
            if (userId != null) {
                authOrgMap = baseClient.getFullPathIdByTypeCode(userId)
                        .stream().collect(Collectors.toMap(Organization::getOrganizationCode, Function.identity(), (a, b) -> a));
                AssertUtils.notEmpty(authOrgMap, "当前用户无任何OU权限");
            } else {
                List<String> orgList = new ArrayList<>();
                param.getItemList().forEach(itemDTO -> {
                    orgList.add(itemDTO.getOrgInvCode());
                    orgList.add(itemDTO.getOrgOuCode());
                });
                authOrgMap = CollectionUtils.isEmpty(orgList) ? new HashMap<>(15) : baseExtClient.getOrganizationByOrgCodes(orgList)
                        .stream().collect(Collectors.toMap(Organization::getOrganizationCode, Function.identity(), (a, b) -> a));
            }
        }
        // 5: 查询现有的物料需求信息
        Map<Long/* souItemId */, SouItem> existSouItemMap = souItemDAO.list(SouItem::getProjectId, param.getProjectId())
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        // 6: 查询报价模板信息
        Map<Long/* tempId */, SouQuoteTemp> quoteTempMap = Collections.emptyMap();
        {
            if (SouOrderTypeEnum.MATERIAL_COST_SEPARATION.equals(param.getOrderType())) {
                quoteTempMap = souQuoteTempRepository.list()
                        .stream().collect(Collectors.toMap(SouQuoteTemp::getTempId, Function.identity()));
            }
        }
        SouRequireEditContext context = new SouRequireEditContext();
        context.setProject(project);
        context.setItemMap(itemMap);
        context.setCategoryMap(categoryMap);
        context.setAuthOrgMap(authOrgMap);
        context.setQuoteTempMap(quoteTempMap);
        context.setExistSouItemMap(existSouItemMap);
        return context;
       /* // 1: 查询品类信息(用于校验无编码物料所选的品类)
        Map<String*//* categoryCode *//*, PurchaseCategory> categoryMap = Collections.emptyMap(); {
            Set<String> categoryCodes = param.getItemList().stream()
                    .map(ApiSouItemDTO::getCategoryCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (!categoryCodes.isEmpty()) {
                categoryMap = baseClient.getCategoryByCodes(categoryCodes);
            }
        }
        context.setCategoryMap(categoryMap);

        return context;*/
    }

    @Override
    protected void formatAndValidateItems(@Nullable List<ApiSouItemDTO> params, boolean isTempSave, boolean isCopy) {
        if (CollectionUtils.isEmpty(params)) {
            AssertUtils.isTrue(isTempSave || isCopy, "缺少物料需求信息");
            return;
        }
        SouProject project = SouRequireEditContext.getContextHolder().getProject();
        Map<String/* orgCode */, Organization> authOrgMap = SouRequireEditContext.getContextHolder().getAuthOrgMap();
        Map<Long/* itemId */, MaterialItem> itemMap = SouRequireEditContext.getContextHolder().getItemMap();
        Map<String/* categoryCode */, PurchaseCategory> categoryMap = SouRequireEditContext.getContextHolder().getCategoryMap();

        int index = 0;
        for (ApiSouItemDTO param : params) {
            index++;
            // 1: ID(略)
            // 2: 业务实体编码
            param.setOrgOuCode(StringUtils.trimToNull(param.getOrgOuCode()));
            AssertUtils.isTrue(param.getOrgOuCode() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择业务实体"), index);
            // 3: 库存组织编码
            param.setOrgInvCode(null);
            // 9: 物料组合
            if (SouOrderWayEnum.COMBINED.equals(project.getOrderWay())) {
                param.setItemGroup(StringUtils.trimToNull(param.getItemGroup()));
                AssertUtils.isTrue(param.getItemGroup() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入物料组合"), index);
                if (param.getItemGroup() != null) {
                    AssertUtils.isTrue(param.getItemGroup().length() <= 30, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行物料组合的长度不能超过30"), index);
                }
            } else {
                param.setItemGroup(null);
            }
            extracted(isTempSave, project, itemMap, categoryMap, index, param);
        }
    }

    /**
     * 组装数据
     * @param isTempSave 参数
     * @param project 参数
     * @param itemMap 参数
     * @param categoryMap 参数
     * @param index 参数
     * @param param 参数
     */
    private static void extracted(boolean isTempSave, SouProject project, Map<Long, MaterialItem> itemMap, Map<String, PurchaseCategory> categoryMap, int index, ApiSouItemDTO param) {
        // 10: 是否无料号物料
        if (param.getNoCodeItem() == null) {
            param.setNoCodeItem(Enable.N);
        }
        // 11: 物料ID
        boolean isNoCodeItem = Enable.Y.equals(param.getNoCodeItem());
        if (isNoCodeItem) {
            param.setItemId(null);
        } else {
            if (param.getItemId() == null) {
                AssertUtils.isTrue(isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择物料"), index);
            } else {
                MaterialItem item = itemMap.get(param.getItemId());
                AssertUtils.notNull(item, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行的物料")+"[{1}]"+LocaleHandler.getLocaleMsg("不存在"), index, param.getItemId());
            }
        }
        // 13: 物料名称
        if (isNoCodeItem) {
            param.setItemDesc(StringUtils.trimToNull(param.getItemDesc()));
            AssertUtils.isTrue(param.getItemDesc() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入物料名称"), index);
            if (param.getItemDesc() != null) {
                AssertUtils.isTrue(param.getItemDesc().length() <= 200, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行物料名称的长度不能超过200"), index);
            }
        } else {
            param.setItemDesc(null);
        }
        // 14: 品类 -- 长城这边需求池的品类是可以自由选择的，不一定完全匹配物料
        if (param.getCategoryCode() != null) {
            PurchaseCategory purchaseCategory = categoryMap.get(param.getCategoryCode());
            if (purchaseCategory != null) {
                param.setCategoryId(purchaseCategory.getCategoryId());
                param.setCategoryCode(purchaseCategory.getCategoryCode());
                param.setCategoryName(purchaseCategory.getCategoryName());
            }
        }
        // 17: 需求数量
        AssertUtils.isTrue(param.getRequireQuantity() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入需求数量"), index);
        if (param.getRequireQuantity() != null) {
            AssertUtils.isTrue(param.getRequireQuantity().compareTo(BigDecimal.ZERO) > 0, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行需求数量必须大于0"), index);
        }
        // 18: 需求时间(略 - 无限制)
        // 19: 是否阶梯价
        if (SouOrderTypeEnum.SIMPLE.equals(project.getOrderType())) {
            // 普通报价
            if (param.getIsLadder() == null) {
                param.setIsLadder(Enable.N);
            }
        } else {
            param.setIsLadder(Enable.N);
        }
        // 20: 预计采购金额
        if (param.getBuyAmount() != null) {
            AssertUtils.isTrue(param.getBuyAmount().compareTo(BigDecimal.ZERO) >= 0, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行预计采购金额不能小于0"), index);
        }
        // 21: 价格有效期范围
        param.setPriceStartTime(null);
        param.setPriceEndTime(null);
        // 25: 备注
        param.setRemark(StringUtils.trimToNull(param.getRemark()));
        if (param.getRemark() != null) {
            AssertUtils.isTrue(param.getRemark().length() <= 300, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行备注长度不能超过300"), index);
        }
        // 26: 来源类型
        param.setSourceFromType(project.getSourceFromType());
        // 27: 来源单据ID/号
        if (!SouSourceFromTypeEnum.PURCHASE_REQ.name().equals(param.getSourceFromType())) {
            param.setSourceFromId(project.getSourceFromId());
            param.setSourceFromNo(project.getSourceFromNo());
        }
        // 28: 来源单据行ID/号
        if (SouSourceFromTypeEnum.HAND_MAKE.name().equals(param.getSourceFromType())) {
            param.setSourceFromLineId(null);
            param.setSourceFromLineNo(null);
        } else {
            SouItem existSouItem = SouRequireEditContext.getContextHolder().getExistSouItemMap().get(param.getSouItemId());
            if (existSouItem != null) {
                param.setSourceFromLineId(existSouItem.getSourceFromLineId());
                param.setSourceFromLineNo(existSouItem.getSourceFromLineNo());
            }
        }
        // 29: 排序
        param.setSortIndex(index);
    }

    @Override
    protected List<SouItem> doConvertItems(long projectId, List<ApiSouItemDTO> params, boolean isCopy) {
        if (params.isEmpty()) { return new ArrayList<>(); }
        List<SouItem> entityList = new ArrayList<>(params.size());

        for (ApiSouItemDTO param : params) {
            SouItem entity = SouObjectXUtil.convertTargetObj(param, SouItem.class);
            entityList.add(entity);

            // ID
            if (entity.getSouItemId() == null) {
                entity.setSouItemId(IdGenrator.generate());
            }
            // 寻源单ID
            entity.setProjectId(projectId);
            // 业务实体
            if (entity.getOrgOuCode() != null) {
                Organization org = SouRequireEditContext.getContextHolder().getAuthOrgMap().get(entity.getOrgOuCode());
                if (org != null) {
                    entity.setOrgOuId(org.getOrganizationId());
                    entity.setOrgOuName(org.getOrganizationName());
                }
            }
            // 库存组织
            entity.setOrgInvId(null);
            entity.setOrgInvCode(null);
            entity.setOrgInvName(null);
            // 物料
            if (entity.getItemId() != null) {
                MaterialItem item = SouRequireEditContext.getContextHolder().getItemMap().get(entity.getItemId());
                if (item != null) {
                    entity.setItemCode(item.getMaterialCode());
                    entity.setItemDesc(item.getMaterialName());
                }
            }
            // 品类
            entity.setCategoryId(param.getCategoryId());
            entity.setCategoryCode(param.getCategoryCode());
            entity.setCategoryName(param.getCategoryName());

            SouObjectXUtil.mergeProperties(entity, param);
        }
        return entityList;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
