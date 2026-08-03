package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editrequrie;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempFieldRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempLineRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSourceFromTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouRequireEditHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouQuoteTempRepositoryImpl souQuoteTempRepository;
    @Autowired
    private SouQuoteTempLineRepositoryImpl souQuoteTempLineRepository;
    @Autowired
    private SouQuoteTempFieldRepositoryImpl souQuoteTempFieldRepository;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private BaseClient baseClient;

    public SouRequireEditPO formatValidateAndConvert(ApiSouRequireInfoDTO param, boolean isCopy, @Nullable Long userId, String souType) {
        AssertUtils.notNull(param.getProjectId(), "缺少projectId参数");
        if (param.getItemList() == null) { param.setItemList(new ArrayList<>()); }
        // 1: 构造业务所需的上下文数据，并保存到上下文中
        SouRequireEditContext.setContextHolder(this.buildContextData(param, userId));
        try {
            // 3: 数据格式化及校验
            this.formatAndValidate(param.getItemList(), param.isTempSave(), isCopy);
            // 4: 数据转换
            return this.convert(param.getProjectId(), param.getItemList(), isCopy);
        } finally {
            // 5: 清除业务上下文
            SouRequireEditContext.remove();
        }
    }

    protected SouRequireEditContext buildContextData(ApiSouRequireInfoDTO param, @Nullable Long userId) {
        // 1: 查询寻源单
        SouProject project = souProjectDao.getById(param.getProjectId());
        // 2: 查询物料信息
        Map<Long/* itemId */, MaterialItem> itemMap = Collections.emptyMap(); {
            Set<Long> itemIds = param.getItemList().stream().map(ApiSouItemDTO::getItemId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!itemIds.isEmpty()) {
                itemMap = baseClient.listMaterialItemsByIds(new ArrayList<>(itemIds))
                        .stream().collect(Collectors.toMap(MaterialItem::getMaterialId, Function.identity()));
            }
        }
        // 3: 查询品类信息(用于校验无编码物料所选的品类)
        Map<String/* categoryCode */, PurchaseCategory> categoryMap = Collections.emptyMap(); {
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
        Map<String/* orgCode */, Organization> authOrgMap; {
            if (userId != null) {
                authOrgMap = baseClient.getFullPathIdByTypeCode(userId)
                        .stream().collect(Collectors.toMap(Organization::getOrganizationCode, Function.identity(), (a, b) -> a));
                AssertUtils.notEmpty(authOrgMap, "当前用户无任何OU权限");
            } else {
                authOrgMap = baseClient.listAllOrganization()
                        .stream().collect(Collectors.toMap(Organization::getOrganizationCode, Function.identity(), (a, b) -> a));
            }
        }
        // 5: 查询现有的物料需求信息
        Map<Long/* souItemId */, SouItem> existSouItemMap = souItemDao.list(SouItem::getProjectId, param.getProjectId())
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        // 6: 查询报价模板信息
        Map<Long/* tempId */, SouQuoteTemp> quoteTempMap = Collections.emptyMap(); {
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
    }

    /**
     * 格式化及校验物料需求信息
     * @param params
     * @param isTempSave
     * @param isCopy
     */
    protected void formatAndValidate(List<ApiSouItemDTO> params, boolean isTempSave, boolean isCopy) {
        /* 1: 格式化及校验物料行 */
        this.formatAndValidateItems(params, isTempSave, isCopy);
        /* 2: 格式化及校验阶梯价 */
        this.formatAndValidateLadders(params, isTempSave, isCopy);
        /* 3: 数据唯一性校验 */
        this.validateUniqueItems(params, isTempSave, isCopy);
    }

    protected void validateUniqueItems(List<ApiSouItemDTO> params, boolean isTempSave, boolean isCopy) {
        /*产品上没有唯一性*/
    }

    /**
     * 数据转换
     * @param projectId
     * @param params
     * @param isCopy
     * @return
     */
    protected SouRequireEditPO convert(long projectId, List<ApiSouItemDTO> params, boolean isCopy) {
        SouRequireEditPO po = new SouRequireEditPO();
        po.setProject(SouRequireEditContext.getContextHolder().getProject());
        po.setSouItemList(this.doConvertItems(projectId, params, isCopy));
        po.setLadderList(this.doConvertLadders(projectId, params, isCopy));
        return po;
    }

    protected void formatAndValidateItems(@Nullable List<ApiSouItemDTO> params, boolean isTempSave, boolean isCopy) {
        if (CollectionUtils.isEmpty(params)) {
            AssertUtils.isTrue(isTempSave || isCopy, "缺少物料需求信息");
            return;
        }
        SouProject project = SouRequireEditContext.getContextHolder().getProject();

        int index = 0;
        for (ApiSouItemDTO param : params) {
            index++;
            // 9: 物料组合
            if (true) {
                param.setItemGroup(StringUtils.trimToNull(param.getItemGroup()));
                AssertUtils.isTrue(param.getItemGroup() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入物料组合"), index);
                if (param.getItemGroup() != null) {
                   AssertUtils.isTrue(param.getItemGroup().length() <= 30, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行物料组合的长度不能超过30"), index);
                }
            } else {
                param.setItemGroup(null);
            }
            // 13: 物料名称
            if (true) {
                param.setItemDesc(StringUtils.trimToNull(param.getItemDesc()));
                AssertUtils.isTrue(param.getItemDesc() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入物料名称"), index);
                if (param.getItemDesc() != null) {
                    AssertUtils.isTrue(param.getItemDesc().length() <= 200, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行物料名称的长度不能超过200"), index);
                }
            } else {
                param.setItemDesc(null);
            }

            // 17: 需求数量
            AssertUtils.isTrue(param.getRequireQuantity() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入需求数量"), index);
            if (param.getRequireQuantity() != null) {
                AssertUtils.isTrue(param.getRequireQuantity().compareTo(BigDecimal.ZERO) > 0, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行需求数量必须大于0"), index);
            }
            // 25: 备注
            param.setRemark(StringUtils.trimToNull(param.getRemark()));
            if (param.getRemark() != null) {
                AssertUtils.isTrue(param.getRemark().length() <= 150, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行备注长度不能超过150"), index);
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
    }

    protected void formatAndValidateLadders(@Nullable List<ApiSouItemDTO> params, boolean isTempSave, boolean isCopy) {
        if (CollectionUtils.isEmpty(params)) {
            AssertUtils.isTrue(isTempSave || isCopy, "缺少物料需求信息");
            return;
        }

        int index = 0;
        int ladderIndex;
        for (ApiSouItemDTO param : params) {
            index++;
            boolean isLadder = Enable.Y.equals(param.getIsLadder());
            if (!isLadder) {
                continue;
            }

            AssertUtils.isTrue(CollectionUtils.isNotEmpty(param.getLadderList()) || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行情维护阶梯价"), index);
            ladderIndex = 0;
            if (CollectionUtils.isNotEmpty(param.getLadderList())) {
                BigDecimal minQty = null;
                BigDecimal maxQty = null;
                for (SouItemLadder ladder : param.getLadderList()) {
                    ladderIndex++;
                    // ID(略)
                    // 寻源单ID(置空 - 后端处理)
                    ladder.setProjectId(null);
                    // 物料需求ID(置空 - 后端处理)
                    ladder.setSouItemId(null);
                    // 阶梯区间从
                    AssertUtils.notNull(ladder.getBeginQuantity(), LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行的阶梯价第")+"{1}"+LocaleHandler.getLocaleMsg("行请维护起始数量"), index, ladderIndex);
                    AssertUtils.isTrue(ladder.getBeginQuantity().compareTo(BigDecimal.ZERO) >= 0,
                            "物料需求列表第{0}行的阶梯价第{1}行起始数量不能小于0", index, ladderIndex);
                    if (minQty == null) {
                        minQty = ladder.getBeginQuantity();
                    }
                    // 阶梯区间到
                    if (ladder.getEndQuantity() != null) {
                        AssertUtils.isTrue(ladder.getBeginQuantity().compareTo(BigDecimal.ZERO) > 0,
                                "物料需求列表第{0}行的阶梯价第{0}行截止数量必须大于0", index, ladderIndex);
                        AssertUtils.isTrue(ladder.getBeginQuantity().compareTo(ladder.getEndQuantity()) < 0,
                                "物料需求列表第{0}行的阶梯价第{1}行起始数量必须小于截止数量");
                    }
                    maxQty = ladder.getEndQuantity();
                    // 排序
                    ladder.setSortIndex(ladderIndex);
                }

                // 确保物料需求数量在阶梯范围区间内
                if (param.getRequireQuantity() != null) {
                    AssertUtils.isTrue(param.getRequireQuantity().compareTo(minQty) >= 0, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行物料需求数量")+"[{1}]"+LocaleHandler.getLocaleMsg("不在阶梯数量范围内")+"[{2} ~ {3}]",
                            index, param.getRequireQuantity(), minQty, maxQty == null ? "" : maxQty);
                    if (maxQty != null) {
                        AssertUtils.isTrue(param.getRequireQuantity().compareTo(maxQty) < 0, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行物料需求数量")+"[{1}]"+LocaleHandler.getLocaleMsg("不在阶梯数量范围内")+"[{2} ~ {3}]",
                                index, param.getRequireQuantity(), minQty, maxQty);
                    }
                }
            }
        }
    }

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
            if (entity.getOrgInvCode() != null) {
                Organization org = SouRequireEditContext.getContextHolder().getAuthOrgMap().get(entity.getOrgInvCode());
                if (org != null) {
                    entity.setOrgInvId(org.getOrganizationId());
                    entity.setOrgInvName(org.getOrganizationName());
                }
            }
            // 物料
            if (entity.getItemId() != null) {
                MaterialItem item = SouRequireEditContext.getContextHolder().getItemMap().get(entity.getItemId());
                if (item != null) {
                    entity.setItemCode(item.getMaterialCode());
                    entity.setItemDesc(item.getMaterialName());
                    entity.setCategoryId(item.getCategoryId());
                    entity.setCategoryCode(item.getCategoryCode());
                    entity.setCategoryName(item.getCategoryName());
                }
            }
            // 品类
            if (Enable.Y.equals(entity.getNoCodeItem()) && entity.getCategoryCode() != null) {
                PurchaseCategory category = SouRequireEditContext.getContextHolder().getCategoryMap().get(entity.getCategoryCode());
                if (category != null) {
                    entity.setCategoryId(category.getCategoryId());
                    entity.setCategoryName(category.getCategoryName());
                }
            }

            SouObjectXUtil.mergeProperties(entity, param);
        }
        return entityList;
    }

    protected List<SouItemLadder> doConvertLadders(long projectId, List<ApiSouItemDTO> params, boolean isCopy) {
        boolean isSimple = SouOrderTypeEnum.SIMPLE.equals(SouRequireEditContext.getContextHolder().getProject().getOrderType());
        if (CollectionUtils.isEmpty(params) || !isSimple) { return new ArrayList<>(); }

        List<SouItemLadder> entityList = new ArrayList<>(params.size() << 2);

        for (ApiSouItemDTO param : params) {
            if (CollectionUtils.isEmpty(param.getLadderList())) { continue; }

            for (SouItemLadder ladder : param.getLadderList()) {
                // ID
                if (ladder.getSouItemLadderId() == null) {
                    ladder.setSouItemLadderId(IdGenrator.generate());
                }
                // 寻源单ID
                ladder.setProjectId(projectId);
                // 物料需求ID
                ladder.setSouItemId(param.getSouItemId());

                SouItemLadder entity = new SouItemLadder();
                BeanUtils.copyProperties(ladder, entity);
                entityList.add(entity);
            }
        }
        return entityList;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
