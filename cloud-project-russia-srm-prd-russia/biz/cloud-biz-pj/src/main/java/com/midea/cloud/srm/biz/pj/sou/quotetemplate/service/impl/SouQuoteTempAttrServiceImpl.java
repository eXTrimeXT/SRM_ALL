package com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.constants.RedisKey;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.aop.lock.SyncLock;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrTableRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempFieldRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempFormulaRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.judge.SouQuoteTempAttrJudge;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempAttrDataService;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempAttrService;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator.attr.SouQuoteTempAttrEditPO;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator.attr.SouQuoteTempAttrValidator;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempAttrEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttrTable;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempFormula;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempAttrStatusEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempFieldTypeEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrTableColumnVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrVO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.quotetemplate.dto.antlr.QuoteFunction;
import com.midea.cloud.srm.model.sou.quotetemplate.dto.antlr.QuoteFunctionType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源 - 模型报价模板 - 报价属性
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/27
 */
@Service
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
public class SouQuoteTempAttrServiceImpl implements ISouQuoteTempAttrService {

    @Autowired
    private SouQuoteTempAttrJudge souQuoteTempAttrJudge;
    @Autowired
    private SouQuoteTempAttrRepositoryImpl souQuoteTempAttrRepository;
    @Autowired
    private SouQuoteTempAttrValidator souQuoteTempAttrValidator;
    @Autowired
    private SouQuoteTempFieldRepositoryImpl souQuoteTempFieldRepository;
    @Autowired
    private SouQuoteTempFormulaRepositoryImpl souQuoteTempFormulaRepository;
    @Autowired
    private SouQuoteTempAttrTableRepositoryImpl souQuoteTempAttrTableRepository;
    @Autowired
    private ISouQuoteTempAttrDataService souQuoteTempAttrDataService;
    @Autowired
    private BaseClient baseClient;

    /**
     * 采购商端: 寻源模型报价模板-报价属性分页查询
     */
    @Override
    public List<SouQuoteTempAttr> listAttrsById(@Nullable LambdaQueryWrapper<SouQuoteTempAttr> queryWrapper,
                                                @Nullable Integer pageNum, @Nullable Integer pageSize) {
        // 1: 查询数据
        if (pageNum != null && pageSize != null) {
            PageMethod.startPage(pageNum, pageSize);
        }
        return queryWrapper != null ? souQuoteTempAttrRepository.list(queryWrapper) : souQuoteTempAttrRepository.list();
    }

    /**
     * 采购商端: 查询报价属性详情信息
     * @param attrId {@link SouQuoteTempAttr#getAttrId}
     */
    @Override
    public SouQuoteTempAttrVO getAttr(long attrId) {
        // 1: 校验操作条件/权限
        SouQuoteTempAttr attr = souQuoteTempAttrJudge.judgeGetAttrAuth(attrId);
        // 2: 查询数据
        List<SouQuoteTempField> fieldList = souQuoteTempFieldRepository.lambdaQuery()
                .eq(SouQuoteTempField::getAttrId, attrId)
                .orderByAsc(SouQuoteTempField::getSortIndex)
                .list();
        List<SouQuoteTempFormula> formulaList = souQuoteTempFormulaRepository.lambdaQuery()
                .eq(SouQuoteTempFormula::getAttrId, attrId)
                .orderByAsc(SouQuoteTempFormula::getSortIndex)
                .list();
        SouQuoteTempAttrTable tableInfo = souQuoteTempAttrTableRepository.lambdaQuery()
                .eq(SouQuoteTempAttrTable::getAttrId, attrId)
                .one();
        // 3: 组装数据返回
        return SouQuoteTempAttrVO.builder()
                .attr(attr)
                .fieldList(fieldList)
                .formulaList(formulaList)
                .tableInfo(tableInfo)
                .build();
    }

    /**
     * 采购商/供应商端: 查询多个报价属性详情信息
     */
    @Override
    public List<SouQuoteTempAttrVO> listAttrsById(Set<Long> attrIds) {
        AssertUtils.notEmpty(attrIds, "缺少attrId数据");
        // 1: 查询报价属性信息
        List<SouQuoteTempAttr> attrList = souQuoteTempAttrRepository.listByIds(attrIds);
        if (attrList.isEmpty()) { return Collections.emptyList(); }
        // 2: 查询报价属性字段
        Map<Long/* attrId */, List<SouQuoteTempField>> fieldMap = souQuoteTempFieldRepository.lambdaQuery()
                .in(SouQuoteTempField::getAttrId, attrIds)
                .orderByAsc(SouQuoteTempField::getSortIndex)
                .list()
                .stream().collect(Collectors.groupingBy(SouQuoteTempField::getAttrId));
        Map<Long/* attrId */, SouQuoteTempAttrTable> tableInfoMap = souQuoteTempAttrTableRepository.lambdaQuery()
                .in(SouQuoteTempAttrTable::getAttrId, attrIds)
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempAttrTable::getAttrId, Function.identity()));
        // 3: 查询报价属性公式
        Map<Long/* attrId */, List<SouQuoteTempFormula>> formulaMap = souQuoteTempFormulaRepository.lambdaQuery()
                .in(SouQuoteTempFormula::getAttrId, attrIds)
                .orderByAsc(SouQuoteTempFormula::getSortIndex)
                .list()
                .stream().collect(Collectors.groupingBy(SouQuoteTempFormula::getAttrId));
        // 4: 组装数据返回
        return attrList.stream()
                .map(attr -> SouQuoteTempAttrVO.builder()
                            .attr(attr)
                            .fieldList(fieldMap.get(attr.getAttrId()))
                            .formulaList(formulaMap.get(attr.getAttrId()))
                            .tableInfo(tableInfoMap.get(attr.getAttrId()))
                            .build())
                .collect(Collectors.toList());
    }

    /**
     * 采购商/供应商端: 查询多个报价属性详情信息
     */
    @Override
    public List<SouQuoteTempAttrVO> listAttrsByName(Set<String> attrNames) {
        AssertUtils.notEmpty(attrNames, "缺少attrId数据");
        // 1: 查询报价属性信息
        List<SouQuoteTempAttr> attrList = souQuoteTempAttrRepository.lambdaQuery()
                .in(SouQuoteTempAttr::getAttrName, attrNames)
                .list();
        if (attrList.isEmpty()) { return Collections.emptyList(); }
        Set<Long> attrIds = attrList.stream().map(SouQuoteTempAttr::getAttrId).collect(Collectors.toSet());
        // 2: 查询报价属性字段
        Map<Long/* attrId */, List<SouQuoteTempField>> fieldMap = souQuoteTempFieldRepository.lambdaQuery()
                .in(SouQuoteTempField::getAttrId, attrIds)
                .orderByAsc(SouQuoteTempField::getSortIndex)
                .list()
                .stream().collect(Collectors.groupingBy(SouQuoteTempField::getAttrId));
        Map<Long/* attrId */, SouQuoteTempAttrTable> tableInfoMap = souQuoteTempAttrTableRepository.lambdaQuery()
                .in(SouQuoteTempAttrTable::getAttrId, attrIds)
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempAttrTable::getAttrId, Function.identity()));
        // 3: 查询报价属性公式
        Map<Long/* attrId */, List<SouQuoteTempFormula>> formulaMap = souQuoteTempFormulaRepository.lambdaQuery()
                .in(SouQuoteTempFormula::getAttrId, attrIds)
                .orderByAsc(SouQuoteTempFormula::getSortIndex)
                .list()
                .stream().collect(Collectors.groupingBy(SouQuoteTempFormula::getAttrId));
        // 4: 组装数据返回
        return attrList.stream()
                .map(attr -> SouQuoteTempAttrVO.builder()
                        .attr(attr)
                        .fieldList(fieldMap.get(attr.getAttrId()))
                        .formulaList(formulaMap.get(attr.getAttrId()))
                        .tableInfo(tableInfoMap.get(attr.getAttrId()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 采购商端: 编辑/提交报价属性
     * PS: 提交的同时需要生效该报价属性的其他版本数据(非拟定)
     * @param param 报价属性信息
     * @param isTempSave true-暂存/false-提交
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.QuoteTemp.SYNC_LOCK_BUYER,
            keyBySpel = "#param.attr.attrId",
            condition = "(#param.attr != null and #param.attr.attrId != null) ? true : false")
    public long/* attrId */ editAttr(SouQuoteTempAttrEditDTO param, boolean isTempSave) {
        // 1: 校验操作条件/权限
        souQuoteTempAttrJudge.judgeEditAttrAuth(param.getAttr() != null ? param.getAttr().getAttrId() : null);
        // 2: 入参校验及数据转换
        SouQuoteTempAttrEditPO po = souQuoteTempAttrValidator.formatValidateAndConvert(param, isTempSave);
        // 3: 保存数据
        souQuoteTempAttrRepository.saveOrUpdate(po.getAttr());
        souQuoteTempFieldRepository.saveOrUpdateForceNull(po.getAttr().getAttrId(), po.getFieldList(), SouQuoteTempField::getAttrId);
        souQuoteTempFormulaRepository.saveOrUpdateForceNull(po.getAttr().getAttrId(), po.getFormulaList(), SouQuoteTempFormula::getAttrId);
        // 4: 创建动态表
        if (!isTempSave) {
            SouQuoteTempAttrTable tableInfo = souQuoteTempAttrValidator.createDynamicTable(po);
            souQuoteTempAttrTableRepository.save(tableInfo);
        }

        return po.getAttr().getAttrId();
    }

    /**
     * 采购商端: 删除报价属性
     * @param attrId {@link SouQuoteTempAttr#getAttrId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.QuoteTemp.SYNC_LOCK_BUYER,
            keyBySpel = "#attrId")
    public void removeAttr(long attrId) {
        // 1: 校验操作条件/权限
        souQuoteTempAttrJudge.judgeRemoveAttrAuth(attrId);
        // 2: 删除数据
        souQuoteTempAttrRepository.removeById(attrId);
        souQuoteTempFieldRepository.lambdaUpdate().eq(SouQuoteTempField::getAttrId, attrId).remove();
        souQuoteTempFormulaRepository.lambdaUpdate().eq(SouQuoteTempFormula::getAttrId, attrId).remove();
        souQuoteTempAttrTableRepository.lambdaUpdate().eq(SouQuoteTempAttrTable::getAttrId, attrId).remove();
    }

    /**
     * 采购商端: 生效报价属性
     * PS: 同时需要生效该报价属性的其他版本数据(非拟定)
     * @param attrId {@link SouQuoteTempAttr#getAttrId}
     * @param loginAppUser 用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.QuoteTemp.SYNC_LOCK_BUYER,
            keyBySpel = "#attrId")
    public void validAttr(long attrId, @Nullable LoginAppUser loginAppUser) {
        // 1: 校验操作条件/权限
        SouQuoteTempAttr attr = souQuoteTempAttrJudge.judgeValidAttrAuth(attrId);
        // 2: 查询数据，走'提交报价属性'接口
        if (SouQuoteTempAttrStatusEnum.DRAFT.equals(attr.getAttrStatus())) {
            SouQuoteTempAttrEditDTO dto = new SouQuoteTempAttrEditDTO(); {
                SouQuoteTempAttrVO vo = this.getAttr(attrId);
                BeanUtils.copyProperties(vo, dto);
            }
            this.editAttr(dto, false);
        }
        // 3: 更新状态
        souQuoteTempAttrRepository.lambdaUpdate()
                .set(SouQuoteTempAttr::getAttrStatus, SouQuoteTempAttrStatusEnum.VALID)
                .set(loginAppUser != null, SouQuoteTempAttr::getLastUpdatedId, loginAppUser != null ? loginAppUser.getUserId() : null)
                .set(loginAppUser != null, SouQuoteTempAttr::getLastUpdatedBy, loginAppUser != null ? loginAppUser.getUsername() : null)
                .set(loginAppUser != null, SouQuoteTempAttr::getLastUpdatedFullName, loginAppUser != null ? loginAppUser.getNickname() : null)
                .set(SouQuoteTempAttr::getLastUpdateDate, new Date())
                .eq(SouQuoteTempAttr::getAttrId, attrId)
                .update();
    }

    /**
     * 采购商端: 失效报价属性
     * @param attrId {@link SouQuoteTempAttr#getAttrId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.QuoteTemp.SYNC_LOCK_BUYER,
            keyBySpel = "#attrId")
    public void invalidAttr(long attrId) {
        // 1: 校验操作条件/权限
        souQuoteTempAttrJudge.judgeInvalidAttrAuth(attrId);
        // 2: 更新状态
        souQuoteTempAttrRepository.lambdaUpdate()
                .set(SouQuoteTempAttr::getAttrStatus, SouQuoteTempAttrStatusEnum.INVALID)
                .set(AppUserUtil.getLoginAppUser() != null, SouQuoteTempAttr::getLastUpdatedId,
                        AppUserUtil.getLoginAppUser() != null ? AppUserUtil.getLoginAppUser().getUserId() : null)
                .set(AppUserUtil.getLoginAppUser() != null, SouQuoteTempAttr::getLastUpdatedBy,
                        AppUserUtil.getLoginAppUser() != null ? AppUserUtil.getLoginAppUser().getUsername() : null)
                .set(AppUserUtil.getLoginAppUser() != null, SouQuoteTempAttr::getLastUpdatedFullName,
                        AppUserUtil.getLoginAppUser() != null ? AppUserUtil.getLoginAppUser().getNickname() : null)
                .set(SouQuoteTempAttr::getLastUpdateDate, new Date())
                .eq(SouQuoteTempAttr::getAttrId, attrId)
                .eq(SouQuoteTempAttr::getAttrStatus, SouQuoteTempAttrStatusEnum.VALID)
                .update();
    }

    /**
     * 采购商端: 复制报价属性
     * @param attrId {@link SouQuoteTempAttr#getAttrId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.QuoteTemp.SYNC_LOCK_BUYER,
            keyBySpel = "#attrId")
    public SouQuoteTempAttr copyAttr(long attrId) {
        // 1: 校验操作条件/权限
        souQuoteTempAttrJudge.judgeCopyAttrAuth(attrId);
        // 2: 查询数据，走'暂存报价属性'接口
        SouQuoteTempAttrEditDTO dto = new SouQuoteTempAttrEditDTO(); {
            SouQuoteTempAttrVO vo = this.getAttr(attrId);
            vo.getAttr().setAttrId(null);
            vo.getFieldList().forEach(field -> field.setFieldId(null));
            vo.getFormulaList().forEach(formula -> formula.setFormulaId(null));
            // 设置一个唯一的名称
            String key = vo.getAttr().getAttrName() + "_复制";
            int index = 0;
            while (true) {
                long count = souQuoteTempAttrRepository.lambdaQuery().eq(SouQuoteTempAttr::getAttrName, key + (++index)).count();
                if (count <= 0) {
                    break;
                }
            }
            vo.getAttr().setAttrName(key + index);
            BeanUtils.copyProperties(vo, dto);
        }
        long newAttrId = this.editAttr(dto, true);
        // 3: 返回数据
        return souQuoteTempAttrRepository.getById(newAttrId);
    }

    /**
     * 计算表格数据
     * @param attrId 报价属性ID
     * @param businessId 业务单据ID
     * @param dataList 表格数据
     * @param needValid 是否需要校验入参数据情况
     */
    @Override
    public void executeCompute(final long attrId, @Nullable String businessId, @Nullable List<Map<String/* fieldId */, Object>> dataList,
                               boolean needValid) {
        if (CollectionUtils.isEmpty(dataList)) { return; }
        SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价属性[{0}]不存在"), attrId);
        Map<Long/* fieldId */, String/* fieldName */> fieldIdNameMap = souQuoteTempFieldRepository.lambdaQuery()
                .eq(SouQuoteTempField::getAttrId, attrId)
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempField::getFieldId, SouQuoteTempField::getFieldName));
        List<SouQuoteTempFormula> formulaList = souQuoteTempFormulaRepository.list(SouQuoteTempFormula::getAttrId, attrId);
        formulaList.sort(Comparator.comparing(SouQuoteTempFormula::getExecuteIndex));
        List<SouQuoteTempField> fieldList = souQuoteTempFieldRepository.list(SouQuoteTempField::getAttrId, attrId);
        Map<String/* dictCode */, Map<String/* dictItemName */, String/* dictItemCode */>> dictMap; {
            Set<String> dictCodes = fieldList.stream().filter(e -> SouQuoteTempFieldTypeEnum.DICT.equals(e.getFieldType()))
                    .map(SouQuoteTempField::getFieldValue).collect(Collectors.toSet());
            if (dictCodes.isEmpty()) {
                dictMap = Collections.emptyMap();
            } else {
                dictMap = baseClient.listByDictCode(new ArrayList<>(dictCodes)).stream()
                        .collect(Collectors.groupingBy(DictItemDTO::getDictCode, Collectors.toMap(DictItemDTO::getDictItemName, DictItemDTO::getDictItemCode, (a, b) -> a)));
            }
        }
        for (Map<String/* fieldId */, Object> rowData : dataList) {
            // 先将可能存在的字典中文转成英文
            {
                fieldList.forEach(field -> {
                    if (SouQuoteTempFieldTypeEnum.DICT.equals(field.getFieldType())) {
                        Map<String/* dictItemName */, String/* dictItemCode */> dictItemMap = dictMap.get(field.getFieldValue());
                        if (dictItemMap != null) {
                            String dictItemName = dictItemMap.get(rowData.get(field.getFieldId().toString()));
                            if (dictItemName != null) {
                                rowData.put(field.getFieldId().toString(), dictItemName);
                            }
                        }
                    }
                });
            }
        }

        // 校验入参数据
        if (needValid) {
            souQuoteTempAttrDataService.analysisAttrDataList(attrId, businessId, dataList, needValid);
        }

        int rowIndex = 0;
        for (Map<String/* fieldId */, Object> rowData : dataList) {
            rowIndex++;
            Set<Long> computedFieldIds = new HashSet<>();
            Map<String/* fieldName */, Object> rowDataByName = new HashMap<>(32); {
                rowData.forEach((fieldId, value) -> {
                    try {
                        rowDataByName.put(fieldIdNameMap.get(Long.valueOf(fieldId)), value);
                    } catch (NumberFormatException e) {
                        // 忽略
                    }
                });
                rowDataByName.put(SouQuoteTempAttrTableColumnVO.TABLE_ID, rowData.get(SouQuoteTempAttrTableColumnVO.TABLE_ID));
                if (!Enable.Y.equals(attr.getIsGlobal())) {
                    rowDataByName.put(SouQuoteTempAttrTableColumnVO.BUSINESS_ID, rowData.get(SouQuoteTempAttrTableColumnVO.BUSINESS_ID));
                }
            }
            for (SouQuoteTempFormula formula : formulaList) {
                if (computedFieldIds.contains(formula.getFieldId())) { continue; }

                BigDecimal price = souQuoteTempAttrDataService.executeFormula(formula, rowDataByName, rowIndex);
                if (price != null) {
                    rowData.put(formula.getFieldId().toString(), price);
                    rowDataByName.put(formula.getFieldName(), price);
                    computedFieldIds.add(formula.getFieldId());
                }
            }
            // 确保每个公式类型的字段都有计算结果
            for (SouQuoteTempFormula formula : formulaList) {
                AssertUtils.isTrue(computedFieldIds.contains(formula.getFieldId()),
                        "[{0}]列表第{1}行字段{2}没有满足的应用条件，无法进行计算", attr.getAttrName(), rowIndex, formula.getFieldName());
            }
        }
    }

    /**
     * 专门用于查询动态表数据
     */
    @Override
    public List<Map<String/* fieldId */, Object>> queryAttrData(final long attrId, Collection<String> businessIds,
                                                                @Nullable Integer pageNum, @Nullable Integer pageSize) {
        SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价模型[{0}]不存在"), attrId);
        SouQuoteTempAttrTable tableInfo = souQuoteTempAttrTableRepository.lambdaQuery()
                .eq(SouQuoteTempAttrTable::getAttrId, attrId)
                .one();
        Map<String/* fieldName */, Long/* fieldId */> fieldNameIdMap = souQuoteTempFieldRepository.lambdaQuery()
                .eq(SouQuoteTempField::getAttrId, attrId)
                .list().stream().collect(Collectors.toMap(SouQuoteTempField::getFieldName, SouQuoteTempField::getFieldId));
        // 1: 查询数据
        String querySql = MessageFormat.format("select * from {0} where 1 = 1 {1}",
                tableInfo.getTableName(), businessIds == null || businessIds.isEmpty() ? "" : "and " + SouQuoteTempAttrTableColumnVO.BUSINESS_ID + " in (" + getStrings(businessIds) + ")");
        List<MetadataDataVO> originDataList = souQuoteTempAttrDataService.readByExecutableSql(querySql, pageNum, pageSize);
        // 2: 数据转换
        List<Map<String/* fieldId */, Object>> dataList = new ArrayList<>(originDataList.size());
        for (MetadataDataVO metaData : originDataList) {
            Map<String/* fieldId */, Object> data = new HashMap<>(metaData.size());
            dataList.add(data);

            tableInfo.getTableColumns().forEach((fieldName, columnInfo) ->
                    data.put(fieldNameIdMap.get(fieldName).toString(), metaData.get(columnInfo.getFieldName())));
            // 写入额外信息:id/businessid
            data.put(SouQuoteTempAttrTableColumnVO.TABLE_ID, metaData.get(SouQuoteTempAttrTableColumnVO.TABLE_ID));
            if (Enable.N.equals(attr.getIsGlobal())) {
                data.put(SouQuoteTempAttrTableColumnVO.BUSINESS_ID, metaData.get(SouQuoteTempAttrTableColumnVO.BUSINESS_ID));
            }
        }

        return dataList;
    }

    private static String getStrings(Collection<String> businessIds) {
        Set<String> ids = new HashSet<>(businessIds);
        StringBuilder sb = new StringBuilder(100);
        for (String businessId : ids) {
            sb.append("'");
            sb.append(businessId);
            sb.append("',");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 根据报价属性的字段IDs（公式类型字段），查找其引用的其他报价属性的 attrIds 集合
     * @param fieldIds {@link SouQuoteTempField#getFieldId}
     * @return {@link SouQuoteTempAttr#getAttrId}
     */
    @Override
    public Map<Long/* fieldId */, List<Long/* attrId */>> getRefAttrIdsByFieldIds(Set<Long> fieldIds) {
        List<SouQuoteTempField> tempFields = souQuoteTempFieldRepository.listByIds(fieldIds).stream()
                .filter(e -> SouQuoteTempFieldTypeEnum.FORMULA.equals(e.getFieldType()))
                .collect(Collectors.toList());
        if (tempFields.isEmpty()) { return Collections.emptyMap(); }
        long attrId = tempFields.get(0).getAttrId();
        boolean isOneAttr = tempFields.stream().allMatch(e -> attrId == e.getAttrId());
        AssertUtils.isTrue(isOneAttr, "不支持同时查询多个不同报价属性的字段信息");
        Map<Long/* fieldId */, List<SouQuoteTempFormula>> formulaMap = souQuoteTempFormulaRepository.lambdaQuery()
                .eq(SouQuoteTempFormula::getAttrId, attrId)
                .in(SouQuoteTempFormula::getFieldId, tempFields.stream().map(SouQuoteTempField::getFieldId).collect(Collectors.toSet()))
                .list().stream().collect(Collectors.groupingBy(SouQuoteTempFormula::getFieldId));
        if (formulaMap.isEmpty()) { return Collections.emptyMap(); }

        Map<Long/* fieldId */, Set<String/* attrName */>> fieldAttrNameMap = new HashMap<>(fieldIds.size());
        Set<String> tempAttrNameMap = new HashSet<>();
        formulaMap.forEach((fieldId, formulaList) -> formulaList.forEach(formula -> {
            List<QuoteFunction> functionList = formula.getFormulaVars().values().stream()
                    .filter(e -> e.getType().equals(QuoteFunctionType.REF))
                    .collect(Collectors.toList());
            functionList.forEach(function -> {
                fieldAttrNameMap.computeIfAbsent(fieldId, k -> new HashSet<>())
                        .add(function.getName());
                tempAttrNameMap.add(function.getName());
            });
        }));
        if (tempAttrNameMap.isEmpty()) { return Collections.emptyMap(); }

        Map<String/* attrName */, Long/* attrId */> attrMap = souQuoteTempAttrRepository.lambdaQuery()
                .in(SouQuoteTempAttr::getAttrName, tempAttrNameMap)
                .list().stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrName, SouQuoteTempAttr::getAttrId));

        Map<Long/* fieldId */, List<Long/* attrId */>> resultMap = new HashMap<>(fieldIds.size()); {
            fieldAttrNameMap.forEach((fieldId, attrNameList) -> attrNameList.forEach(attrName -> {
                Long tempAttrId = attrMap.get(attrName);
                if (tempAttrId != null) {
                    resultMap.computeIfAbsent(fieldId, k -> new ArrayList<>()).add(tempAttrId);
                }
            }));
        }

        return resultMap;
    }

}
