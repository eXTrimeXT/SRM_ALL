package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.itemrecord;

import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemRecordDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.ApiSouItemRecordDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemRecord;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
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
 * 寻源openAPI - 物料变更记录处理
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouItemRecordHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouItemRecordDAOImpl souItemRecordDao;
    @Autowired
    private BaseClient baseClient;

    public SouItemRecordPO formatValidateAndConvert(ApiSouItemRecordDTO param, String souType) {
        // 1: 构造业务所需的上下文数据，并保存到上下文中
        SouItemRecordContext.setContextHolder(this.buildContextData(param));
        try {
            // 3: 数据格式化及校验
            this.formatAndValidate(param.getItemList());
            // 4: 数据转换
            return this.convert(param.getProjectId(), param.getItemList());
        } finally {
            // 5: 清除业务上下文
            SouItemRecordContext.remove();
        }
    }

    protected SouItemRecordContext buildContextData(ApiSouItemRecordDTO param) {
        // 1: 查询寻源单
        SouProject project = souProjectDao.getById(param.getProjectId());
        // 2: 查询现有生效的物料物料
        List<SouItem> existItemList = SouActiveBeanUtils.getActiveBean(project.getSouType(), ApiSouOrderQueryHandler.class)
                .getValidItemsInSpecifiedRound(project.getProjectId(), null);
        // 3: 查询物料信息
        Map<Long/* itemId */, MaterialItem> itemMap = Collections.emptyMap(); {
            Set<Long> itemIds = param.getItemList().stream().map(ApiSouItemDTO::getItemId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!itemIds.isEmpty()) {
                itemMap = baseClient.listMaterialItemsByIds(new ArrayList<>(itemIds))
                        .stream().collect(Collectors.toMap(MaterialItem::getMaterialId, Function.identity()));
            }
        }
        // 4: 查询品类信息(用于校验无编码物料所选的品类)
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
        // 5: 查询当前用户有权限的OU信息
        Map<String/* orgCode */, Organization> authOrgMap = baseClient.listAllOrganization()
                .stream().collect(Collectors.toMap(Organization::getOrganizationCode, Function.identity()));
        // 6: 批次号
        String newBatchNo = baseClient.seqGen(SequenceCodeConstant.SOU.SEQ_SOU_ITEM_REFRESH_NO);

        SouItemRecordContext context = new SouItemRecordContext();
        context.setProject(project);
        context.setExistItemList(existItemList);
        context.setItemMap(itemMap);
        context.setCategoryMap(categoryMap);
        context.setAuthOrgMap(authOrgMap);
        context.setNewBatchNo(newBatchNo);
        return context;
    }

    protected void formatAndValidate(List<ApiSouItemDTO> dtoList) {
        if (dtoList.isEmpty()) { return; }
        // 1: 格式化及校验物料行
        this.formatAndValidateItems(dtoList);
        // 3: 校验物料唯一性
        this.validateUniqueItems(dtoList);
    }

    /**
     * 校验物料需求行
     * @param dtoList
     */
    protected void formatAndValidateItems(List<ApiSouItemDTO> dtoList) {
        SouProject project = SouItemRecordContext.getContextHolder().getProject();
        Map<String/* orgCode */, Organization> authOrgMap = SouItemRecordContext.getContextHolder().getAuthOrgMap();
        Map<Long/* itemId */, MaterialItem> itemMap = SouItemRecordContext.getContextHolder().getItemMap();
        Map<String/* categoryCode */, PurchaseCategory> categoryMap = SouItemRecordContext.getContextHolder().getCategoryMap();

        int index = 0;
        for (ApiSouItemDTO param : dtoList) {
            index++;
            // 1: ID(略)
            // 2: 业务实体编码
            param.setOrgOuCode(StringUtils.trimToNull(param.getOrgOuCode()));
            AssertUtils.isTrue(param.getOrgOuCode() != null, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择业务实体"), index);
            if (param.getOrgOuCode() != null) {
                AssertUtils.isTrue(authOrgMap.containsKey(param.getOrgOuCode()), LocaleHandler.getLocaleMsg("没有")+"[{0}]"+LocaleHandler.getLocaleMsg("组织的权限"), param.getOrgOuCode(), index);
            }
            // 3: 库存组织编码
            param.setOrgInvCode(StringUtils.trimToNull(param.getOrgInvCode()));
            AssertUtils.isTrue(param.getOrgInvCode() != null, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择业务实体"), index);
            if (param.getOrgInvCode() != null) {
                Organization inv = authOrgMap.get(param.getOrgInvCode());
                AssertUtils.notNull(inv, LocaleHandler.getLocaleMsg("没有")+"[{0}]"+LocaleHandler.getLocaleMsg("组织的权限"), param.getOrgInvCode());
                if (param.getOrgOuCode() != null) {
                    AssertUtils.isTrue(StringUtils.equals(param.getOrgOuCode(), inv.getParentOrganizationCodes()),
                            "组织[{0} - {1}]没有上下级关系，请重新选择", param.getOrgOuCode(), param.getOrgInvCode());
                }
            }
            // 4: 物料组合
            if (SouOrderWayEnum.COMBINED.equals(project.getOrderWay())) {
                param.setItemGroup(StringUtils.trimToNull(param.getItemGroup()));
                AssertUtils.isTrue(param.getItemGroup() != null, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入物料组合"), index);
                if (param.getItemGroup() != null) {
                    AssertUtils.isTrue(param.getItemGroup().length() <= 30, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行物料组合的长度不能超过30"), index);
                }
            } else {
                param.setItemGroup(null);
            }
            // 4: 是否无料号物料
            if (param.getNoCodeItem() == null) {
                param.setNoCodeItem(Enable.N);
            }
            // 5: 物料ID
            boolean isNoCodeItem = Enable.Y.equals(param.getNoCodeItem());
            if (isNoCodeItem) {
                param.setItemId(null);
            } else {
                AssertUtils.notNull(param.getItemId(), LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择物料"), index);
                MaterialItem item = itemMap.get(param.getItemId());
                AssertUtils.notNull(item, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行的物料")+"[{1}]"+LocaleHandler.getLocaleMsg("不存在"), index, param.getItemId());
            }
            // 6: 物料名称
            if (isNoCodeItem) {
                param.setItemDesc(StringUtils.trimToNull(param.getItemDesc()));
                AssertUtils.isTrue(param.getItemDesc() != null, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入物料名称"));
                if (param.getItemDesc() != null) {
                    AssertUtils.isTrue(param.getItemDesc().length() <= 200, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行物料名称的长度不能超过200"));
                }
            } else {
                param.setItemDesc(null);
            }
            // 7: 品类ID(置空)
            param.setCategoryId(null);
            // 8: 品类编码
            if (isNoCodeItem) {
                param.setCategoryCode(StringUtils.trimToNull(param.getCategoryCode()));
                AssertUtils.isTrue(param.getCategoryCode() != null, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择品类"));
                if (param.getCategoryCode() != null) {
                    AssertUtils.isTrue(categoryMap.containsKey(param.getCategoryCode()), LocaleHandler.getLocaleMsg("品类")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getCategoryCode());
                }
            }
            // 9: 品类名称(置空)
            param.setCategoryName(null);
            // 10: 需求数量
            AssertUtils.isTrue(param.getRequireQuantity() != null, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入需求数量"), index);
            if (param.getRequireQuantity() != null) {
                AssertUtils.isTrue(param.getRequireQuantity().compareTo(BigDecimal.ZERO) > 0, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行需求数量必须大于0"), index);
            }
            // 11: 需求时间(略 - 无限制)
            // 12: 是否阶梯价
            if (SouOrderTypeEnum.SIMPLE.equals(project.getOrderType())) {
                // 普通报价
                if (param.getIsLadder() == null) {
                    param.setIsLadder(Enable.N);
                }
            } else {
                param.setIsLadder(Enable.N);
            }
            // 13: 预计采购金额
            if (param.getBuyAmount() != null) {
                AssertUtils.isTrue(param.getBuyAmount().compareTo(BigDecimal.ZERO) > 0, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行预计采购金额必须大于0"), index);
            }
            // 14: 价格有效期范围
            AssertUtils.isTrue(param.getPriceStartTime() != null, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择价格开始日期"), index);
            if (param.getPriceStartTime() != null) {
                param.setPriceStartTime(ApiSouProjectQueryDTO.getStartTimeOfDate(param.getPriceStartTime()));
            }
            if (param.getPriceEndTime() != null) {
                param.setPriceEndTime(ApiSouProjectQueryDTO.getEndTimeOfDay(param.getPriceEndTime()));
                if (param.getPriceStartTime() != null) {
                    AssertUtils.isTrue(param.getPriceStartTime().before(param.getPriceEndTime()), "价格有效期开始时间必须遭遇价格有效期截止时间");
                }
            }
            // 15: 备注
            param.setRemark(StringUtils.trimToNull(param.getRemark()));
            if (param.getRemark() != null) {
                AssertUtils.isTrue(param.getRemark().length() <= 100, LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行备注长度不能超过100"), index);
            }
        }
    }

    protected void formatAndValidateLadders(List<ApiSouItemDTO> dtoList) {
        int index = 0;
        int ladderIndex;
        for (ApiSouItemDTO param : dtoList) {
            index++;
            boolean isLadder = Enable.Y.equals(param.getIsLadder());
            if (!isLadder) {
                continue;
            }

            AssertUtils.isTrue(CollectionUtils.isNotEmpty(param.getLadderList()), LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行情维护阶梯价"), index);
            ladderIndex = 0;
            for (SouItemLadder ladder : param.getLadderList()) {
                ladderIndex++;
                // ID(略)
                // 寻源单ID(置空 - 后端处理)
                ladder.setProjectId(null);
                // 物料需求ID(置空 - 后端处理)
                ladder.setSouItemId(null);
                // 阶梯区间从
                AssertUtils.notNull(ladder.getBeginQuantity(), LocaleHandler.getLocaleMsg("物料变更数据第")+"{0}"+LocaleHandler.getLocaleMsg("行的阶梯价第")+"{1}"+LocaleHandler.getLocaleMsg("行请维护起始数量"), index, ladderIndex);
                AssertUtils.isTrue(ladder.getBeginQuantity().compareTo(BigDecimal.ZERO) >= 0,
                        "物料变更数据第{0}行的阶梯价第{1}行起始数量不能小于0", index, ladderIndex);
                // 阶梯区间到
                if (ladder.getEndQuantity() != null) {
                    AssertUtils.isTrue(ladder.getBeginQuantity().compareTo(BigDecimal.ZERO) > 0,
                            "物料变更数据第{0}行的阶梯价第{0}行截止数量必须大于0", index, ladderIndex);
                    AssertUtils.isTrue(ladder.getBeginQuantity().compareTo(ladder.getEndQuantity()) < 0,
                            "物料变更数据第{0}行的阶梯价第{1}行起始数量必须小于截止数量");
                }
            }
        }
    }

    /**
     * 物料唯一性判断
     * @param dtoList
     */
    protected void validateUniqueItems(List<ApiSouItemDTO> dtoList) {
        // 产品核心略，允许随意添加，由各寻源模块独立实现
    }

    /**
     * 数据转换
     * PS: 如果物料信息相同，则更新，否则就新增
     */
    @SuppressWarnings("unchecked")
    protected SouItemRecordPO convert(long projectId, List<ApiSouItemDTO> dtoList) {
        SouProject project = SouItemRecordContext.getContextHolder().getProject();
        SouItemRecordPO po = new SouItemRecordPO();

        // 1: 将入参数据进行填充
        List<SouItem> newItemList = this.doConvertItems(projectId, dtoList);
        // 2: 与现有数据进行对比
        List<SouItem> addSouItemList = new ArrayList<>(32);
        List<SouItem> keepSouItemList = new ArrayList<>(32);
        for (SouItem newItem : newItemList) {
            SouItem existItem = this.findExistMatchItem(projectId, SouItemRecordContext.getContextHolder().getExistItemList(), newItem);
            if (existItem == null) {
                // 说明是新数据
                addSouItemList.add(newItem);
                // 处理阶梯价 TODO
            } else {
                // 说明是现有的数据进行更新
                keepSouItemList.add(existItem);
                SouObjectXUtil.mergePropertiesIgnoreFields(newItem, existItem, SouItem::getSouItemId);
                // 处理阶梯价 TODO
            }
        }

        this.doConvertRecords(projectId, po, dtoList, addSouItemList, keepSouItemList);

        return po;
    }

    protected List<SouItem> doConvertItems(long projectId, List<ApiSouItemDTO> dtoList) {
        List<SouItem> entityList = new ArrayList<>(dtoList.size());

        if (dtoList.isEmpty()) {
            // 从现有的记录中查找
            List<SouItemRecord> recordList = souItemRecordDao.lambdaQuery()
                    .eq(SouItemRecord::getProjectId, projectId)
                    .orderByDesc(SouItemRecord::getBatchNo)
                    .list();
            if (!recordList.isEmpty()) {
                String batchNo = recordList.get(0).getBatchNo();
                entityList.addAll(recordList.stream()
                        .filter(r -> r.getBatchNo().equals(batchNo))
                        .filter(r -> r.getRefreshStatus().equals(SouItemRefreshStatusEnum.DRAFT)
                                || r.getRefreshStatus().equals(SouItemRefreshStatusEnum.FAIL))
                        .map(SouItemRecord::getItemInfo)
                        .collect(Collectors.toList()));
            }
        } else {
            // 新的记录
            int index = 0;
            if (!SouItemRecordContext.getContextHolder().getExistItemList().isEmpty()) {
                SouItemRecordContext.getContextHolder().getExistItemList().sort(Comparator.comparing(SouItem::getSortIndex).reversed());
                index = SouItemRecordContext.getContextHolder().getExistItemList().get(0).getSortIndex();
            }
            for (ApiSouItemDTO dto : dtoList) {
                index++;
                SouItem entity = SouObjectXUtil.convertTargetObj(dto, SouItem.class);
                entityList.add(entity);

                // ID
                entity.setSouItemId(IdGenrator.generate());
                // 寻源单ID
                entity.setProjectId(projectId);
                // 业务实体
                if (entity.getOrgOuCode() != null) {
                    Organization org = SouItemRecordContext.getContextHolder().getAuthOrgMap().get(entity.getOrgOuCode());
                    if (org != null) {
                        entity.setOrgOuId(org.getOrganizationId());
                        entity.setOrgOuName(org.getOrganizationName());
                    }
                }
                // 库存组织
                if (entity.getOrgInvCode() != null) {
                    Organization org = SouItemRecordContext.getContextHolder().getAuthOrgMap().get(entity.getOrgInvCode());
                    if (org != null) {
                        entity.setOrgInvId(org.getOrganizationId());
                        entity.setOrgInvName(org.getOrganizationName());
                    }
                }
                // 物料
                if (entity.getItemId() != null) {
                    MaterialItem item = SouItemRecordContext.getContextHolder().getItemMap().get(entity.getItemId());
                    if (item != null) {
                        entity.setItemCode(item.getMaterialCode());
                        entity.setItemDesc(item.getMaterialName());
                        if (item.getCategoryCode() != null) {
                            entity.setCategoryId(item.getCategoryId());
                            entity.setCategoryCode(item.getCategoryCode());
                            entity.setCategoryName(item.getCategoryName());
                        }
                        entity.setUnit(item.getUnit());
                    }
                }
                // 品类
                if (entity.getCategoryCode() != null) {
                    PurchaseCategory category = SouItemRecordContext.getContextHolder().getCategoryMap().get(entity.getCategoryCode());
                    if (category != null) {
                        entity.setCategoryId(category.getCategoryId());
                        entity.setCategoryName(category.getCategoryName());
                    }
                }
                // 来源信息
                AssertUtils.notNull(entity.getSourceFromType(), "缺少来源类型");
                AssertUtils.notNull(entity.getSourceFromLineId(), "缺少来源行ID");
                // 排序
                entity.setSortIndex(index);

                SouObjectXUtil.mergeProperties(entity, dto);
            }
        }

        return entityList;
    }

    protected List<SouItemLadder> doConvertLadders(long projectId, List<ApiSouItemDTO> params) {
        boolean isSimple = SouOrderTypeEnum.SIMPLE.equals(SouItemRecordContext.getContextHolder().getProject().getOrderType());
        if (CollectionUtils.isEmpty(params) || !isSimple) { return new ArrayList<>(); }

        List<SouItemLadder> entityList = new ArrayList<>(params.size() << 2);

        int ladderIndex;
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

    protected void doConvertRecords(long projectId, SouItemRecordPO po, List<ApiSouItemDTO> dtoList,
                                    List<SouItem> addSouItemList, List<SouItem> keepSouItemList) {
        SouProject project = souProjectDao.getById(projectId);
        // 把历史记录中[未执行/失败]的记录状态设置为作废
        po.setUpdateRecordList(souItemRecordDao.lambdaQuery()
                .eq(SouItemRecord::getProjectId, projectId)
                .in(SouItemRecord::getRefreshStatus, SouItemRefreshStatusEnum.DRAFT, SouItemRefreshStatusEnum.FAIL)
                .list());
        po.getUpdateRecordList().forEach(e -> e.setRefreshStatus(SouItemRefreshStatusEnum.CANCEL));
        //...

        po.setSaveRecordList(new ArrayList<>(dtoList.size()));
        String batchNo = SouItemRecordContext.getContextHolder().getNewBatchNo();

        Set<Long> addSouItemIds = addSouItemList.stream().map(SouItem::getSouItemId).collect(Collectors.toSet());
        Set<Long> keepSouItemIds = keepSouItemList.stream().map(SouItem::getSouItemId).collect(Collectors.toSet());

        List<SouItem> tempItemList = new ArrayList<>(dtoList.size() << 2); {
            // 待新增的
            tempItemList.addAll(addSouItemList);
            // 待删除的
            List<SouItem> validSouItems = SouActiveBeanUtils.getActiveBean(project.getSouType(), ApiSouOrderQueryHandler.class)
                    .getValidItemsInSpecifiedRound(projectId, null);
            validSouItems.forEach(item -> {
                if (!(addSouItemIds.contains(item.getSouItemId()) || keepSouItemIds.contains(item.getSouItemId()))) {
                    tempItemList.add(item);
                }
            });
            // 现有的
            tempItemList.addAll(keepSouItemList);
        }
        for (SouItem souItem : tempItemList) {
            SouItemRecord record = new SouItemRecord();
            po.getSaveRecordList().add(record);

            // ID
            record.setRecordId(IdGenrator.generate());
            // 寻源单ID
            record.setProjectId(souItem.getProjectId());
            // 批次号
            record.setBatchNo(batchNo);
            // 物料需求ID
            record.setSouItemId(souItem.getSouItemId());
            // 刷新类型
            if (addSouItemIds.contains(souItem.getSouItemId())) {
                record.setRefreshType(SouItemRefreshTypeEnum.NEW);
            } else if (keepSouItemIds.contains(souItem.getSouItemId())) {
                record.setRefreshType(SouItemRefreshTypeEnum.EXIST);
            } else {
                record.setRefreshType(SouItemRefreshTypeEnum.DELETE);
            }
            // 刷新状态/轮次
            record.setRefreshStatus(SouItemRefreshStatusEnum.DRAFT);
            record.setRefreshRound(null);
            // 执行物料刷新的时间
            record.setRefreshTime(null);

            BeanUtils.copyProperties(souItem, record);
            record.setItemInfo(souItem);
        }
    }

    @Nullable
    protected SouItem findExistMatchItem(long projectId, List<SouItem> existItemList, SouItem newItem) {
        // 产品上默认是不存在重复数据，可以随意添加，因此这里就返回null
        return null;
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
