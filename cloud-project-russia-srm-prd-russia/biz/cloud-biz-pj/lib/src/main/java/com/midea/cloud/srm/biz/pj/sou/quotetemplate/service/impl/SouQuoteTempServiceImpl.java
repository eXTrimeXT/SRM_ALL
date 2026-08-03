package com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.constants.RedisKey;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.aop.lock.SyncLock;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.ConditionType;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDetailDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.service.MetadataDataService;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.*;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.judge.SouQuoteTempJudge;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempAttrDataService;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempAttrService;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempService;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator.SouQuoteTempEditPO;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator.SouQuoteTempValidator;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempDataBatchQueryDto;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempQueryDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.*;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempAttrStatusEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempStatusEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.*;
import com.midea.cloud.srm.model.common.enums.Enable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangwk12@midea.com
 * @since 2022/08/02
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouQuoteTempServiceImpl implements ISouQuoteTempService {

    @Autowired
    private SouQuoteTempJudge souQuoteTempJudge;
    @Autowired
    private SouQuoteTempRepositoryImpl souQuoteTempRepository;
    @Autowired
    private SouQuoteTempLineRepositoryImpl souQuoteTempLineRepository;
    @Autowired
    private SouQuoteTempAttrRepositoryImpl souQuoteTempAttrRepository;
    @Autowired
    private SouQuoteTempValidator souQuoteTempValidator;
    @Autowired
    private ISouQuoteTempAttrService souQuoteTempAttrService;
    @Autowired
    private ISouQuoteTempAttrDataService souQuoteTempAttrDataService;
    @Autowired
    private SouQuoteTempAttrTableRepositoryImpl souQuoteTempAttrTableRepository;
    @Autowired
    private MetadataDataService metadataDataService;
    @Autowired
    private SouQuoteTempFieldRepositoryImpl souQuoteTempFieldRepository;

    /**
     * 采购商端: 列表查询报价模板
     */
    @Override
    public List<SouQuoteTemp> listTemps(SouQuoteTempQueryDTO queryParam) {
        /* 1: 校验操作条件/权限 */
        souQuoteTempJudge.judgeListTempsAuth();
        /* 2: 入参格式化 */
        queryParam.formatParams();
        /* 3: 查询数据 */
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        return souQuoteTempRepository.lambdaQuery()
                /* 模板编码 */
                .like(queryParam.getTempNo() != null, SouQuoteTemp::getTempNo, queryParam.getTempNo())
                /* 模板名称 */
                .like(queryParam.getTempName() != null, SouQuoteTemp::getTempName, queryParam.getTempName())
                /* 模板状态 */
                .eq(queryParam.getTempStatus() != null, SouQuoteTemp::getTempStatus, queryParam.getTempStatus())
                /* 创建时间范围 */
                .ge(queryParam.getLastUpdateDateFrom() != null, SouQuoteTemp::getCreationDate, queryParam.getLastUpdateDateFrom())
                .le(queryParam.getLastUpdateDateTo() != null, SouQuoteTemp::getCreationDate, queryParam.getLastUpdateDateTo())
                .orderByDesc(SouQuoteTemp::getTempNo)
                .list();
    }

    /**
     * 采购商端: 查询报价模板详情
     * @param tempId {@link SouQuoteTemp#getTempId}
     */
    @Override
    public SouQuoteTempVO getTemp(long tempId) {
        /* 1: 校验操作条件/权限 */
        SouQuoteTemp temp = souQuoteTempJudge.judgeGetTempAuth(tempId);
        /* 2: 查询报价模板行信息 */
        List<SouQuoteTempLine> tempLineList = souQuoteTempLineRepository.lambdaQuery()
                .eq(SouQuoteTempLine::getTempId, tempId)
                .orderByAsc(SouQuoteTempLine::getSortIndex)
                .list();
        /* 3: 组装数据返回 */
        return SouQuoteTempVO.builder()
                .temp(temp)
                .tempLineList(tempLineList)
                .build();
    }

    /**
     * 采购商端: 校验添加的报价属性是否是完整的
     * PS: 假设报价属性有如下的引用链 "[A] -> [B] -> [C]"
     *     如果仅选择了"[A]"，那么就会报错，引用链上的所有有效的报价属性都必须被选择
     * @param attrIds {@link SouQuoteTempAttr#getAttrId}
     */
    @Override
    public SouQuoteTempAttrRelateVO checkTempAttrs(Set<Long> attrIds) {
        /* 1: 校验操作条件/权限 */
        Map<String/* attrName */, SouQuoteTempAttr> attrMap = souQuoteTempJudge.judgeCheckTempAttrsAuth(attrIds)
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrName, Function.identity()));
        /* 2: 拿到所有的依赖报价属性 */
        Map<String/* attrName */, SouQuoteTempAttr> allAttrMap; {
            Set<String> relateAttrNames = new HashSet<>(32); {
                for (SouQuoteTempAttr attr : attrMap.values()) {
                    relateAttrNames.addAll(attr.getAttrRelate().getRelateNodes());
                }
            }
            if (!relateAttrNames.isEmpty()) {
                List<SouQuoteTempAttr> attrs = souQuoteTempAttrRepository.lambdaQuery()
                        .in(SouQuoteTempAttr::getAttrName, relateAttrNames)
                        .list();
                attrs.addAll(attrMap.values());
                allAttrMap = attrs.stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrName, Function.identity(), (a, b) -> b));
            } else {
                allAttrMap = Collections.emptyMap();
            }
        }
        if (allAttrMap.isEmpty()) { return new SouQuoteTempAttrRelateVO(); }
        /* 3: 确保被依赖的报价属性存在且生效 */
        List<String> treeList = new ArrayList<>(attrMap.size());
        for (SouQuoteTempAttr attr : attrMap.values()) {
            treeList.addAll(attr.getAttrRelate().getRelatePath());

            attr.getAttrRelate().getRelateNodes().forEach(node -> {
                SouQuoteTempAttr targetAttr = allAttrMap.get(node);
                AssertUtils.notNull(targetAttr, LocaleHandler.getLocaleMsg("报价属性[{0}]所依赖的[{1}]不存在"), attr.getAttrName(), node);
                AssertUtils.isTrue(SouQuoteTempAttrStatusEnum.VALID.equals(targetAttr.getAttrStatus()),
                        "报价属性[{0}]所依赖的[{1}]不是生效状态", attr.getAttrName(), node);
            });
        }

        return new SouQuoteTempAttrRelateVO(treeList);
    }

    /**
     * 采购商端: 编辑/提交报价模板
     * @param param 报价模板信息
     * @param isTempSave true-暂存/false-提交
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.SYNC_LOCK_BUYER,
            keyBySpel = "#param.temp.tempId",
            condition = "(#param.temp != null and #param.temp.tempId != null) ? true : false")
    public long/* tempId */ editTemp(SouQuoteTempEditDTO param, boolean isTempSave) {
        /* 1: 校验操作条件/权限 */
        souQuoteTempJudge.judgeEditTempAuth(param.getTemp() != null ? param.getTemp().getTempId() : null);
        /* 2: 入参校验及数据转换 */
        SouQuoteTempEditPO po = souQuoteTempValidator.formatValidateAndConvert(param, isTempSave);
        /* 3: 保存数据 */
        souQuoteTempRepository.saveOrUpdate(po.getTemp());
        souQuoteTempLineRepository.saveOrUpdate(po.getTemp().getTempId(), po.getTempLineList(), SouQuoteTempLine::getTempId);

        return po.getTemp().getTempId();
    }

    /**
     * 采购商端: 删除报价模板
     * @param tempId {@link SouQuoteTemp#getTempId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.SYNC_LOCK_BUYER,
            keyBySpel = "#tempId")
    public void removeTemp(long tempId) {
        /* 1: 校验操作条件/权限 */
        souQuoteTempJudge.judgeRemoveTempAuth(tempId);
        /* 2: 删除数据 */
        souQuoteTempRepository.removeById(tempId);
        souQuoteTempLineRepository.lambdaUpdate().eq(SouQuoteTempLine::getTempId, tempId).remove();
    }

    /**
     * 采购商端: 生效报价模板
     * @param tempId {@link SouQuoteTemp#getTempId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.SYNC_LOCK_BUYER,
            keyBySpel = "#tempId")
    public void validTemp(long tempId) {
        /* 1: 校验操作条件/权限 */
        SouQuoteTemp temp = souQuoteTempJudge.judgeValidTempAuth(tempId);
        /* 2: 查询数据，走'提交报价模板'接口 */
        if (SouQuoteTempStatusEnum.DRAFT.equals(temp.getTempStatus())) {
            SouQuoteTempEditDTO dto = new SouQuoteTempEditDTO(); {
                SouQuoteTempVO vo = this.getTemp(tempId);
                BeanUtils.copyProperties(vo, dto);
            }
            this.editTemp(dto, false);
        }
        /* 3: 更新状态 */
        souQuoteTempRepository.lambdaUpdate()
                .set(SouQuoteTemp::getTempStatus, SouQuoteTempStatusEnum.VALID)
                .eq(SouQuoteTemp::getTempId, tempId)
                .update();
    }

    /**
     * 采购商端: 失效报价模板
     * @param tempId {@link SouQuoteTemp#getTempId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.SYNC_LOCK_BUYER,
            keyBySpel = "#tempId")
    public void invalidTemp(long tempId) {
        /* 1: 校验操作条件/权限 */
        souQuoteTempJudge.judgeInvalidTempAuth(tempId);
        /* 2: 更新状态 */
        souQuoteTempRepository.lambdaUpdate()
                .set(SouQuoteTemp::getTempStatus, SouQuoteTempStatusEnum.INVALID)
                .eq(SouQuoteTemp::getTempId, tempId)
                .eq(SouQuoteTemp::getTempStatus, SouQuoteTempStatusEnum.VALID)
                .update();
    }

    /**
     * 采购商端: 复制报价模板
     * @param tempId {@link SouQuoteTemp#getTempId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.SYNC_LOCK_BUYER,
            keyBySpel = "#tempId")
    public SouQuoteTemp copyTemp(long tempId) {
        /* 1: 校验操作条件/权限 */
        souQuoteTempJudge.judgeCopyTempAuth(tempId);
        /* 2: 查询数据，走'暂存报价模板'接口 */
        SouQuoteTempEditDTO dto = new SouQuoteTempEditDTO(); {
            SouQuoteTempVO vo = this.getTemp(tempId);
            vo.getTemp().setTempId(null);
            vo.getTempLineList().forEach(line -> line.setTempLineId(null));
            /* 设置一个唯一的名称 */
            String key = vo.getTemp().getTempName() + "_复制";
            int index = 0;
            while (true) {
                long count = souQuoteTempRepository.lambdaQuery().eq(SouQuoteTemp::getTempName, key + (++index)).count();
                if (count <= 0) {
                    break;
                }
            }
            BeanUtils.copyProperties(vo, dto);
        }
        long newTempId = this.editTemp(dto, true);
        /* 3: 返回数据 */
        return souQuoteTempRepository.getById(newTempId);
    }

    /**
     * 采购商端: 查询报价模板数据
     * @param tempId {@link SouQuoteTemp#getTempId}
     * @param businessId 业务单据ID
     * @param queryTableData 是否需要查询实际表数据
     */
    @Override
    public SouQuoteTempDataVO queryTempData(long tempId, String businessId, boolean queryTableData) {
        businessId = StringUtils.trimToNull(businessId);
        AssertUtils.notNull(businessId, "缺少businessId参数");
        /* 1: 查询数据 */
        SouQuoteTempVO tempVO = this.getTemp(tempId);
        Map<Long/* attrId */, SouQuoteTempAttrVO> attrMap = souQuoteTempAttrService
                .listAttrsById(tempVO.getTempLineList().stream().map(SouQuoteTempLine::getAttrId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(attr -> attr.getAttr().getAttrId(), Function.identity()));
        /* 2: 查询报价属性数据 */
        SouQuoteTempDataDetailVO dataInfo; {
            Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> attrDataList = new HashMap<>(attrMap.size());
            if (queryTableData && !attrMap.isEmpty()) {
                for (Long attrId : attrMap.keySet()) {
                    attrDataList.put(attrId, souQuoteTempAttrService.queryAttrData(attrId, Collections.singletonList(businessId), null, null));
                }
            }
            dataInfo = new SouQuoteTempDataDetailVO();
            dataInfo.setData(attrDataList);
        }
        /* 2: 组装数据返回 */
        return new SouQuoteTempDataVO(tempVO, attrMap, dataInfo);
    }

    /**
     * 查询报价模板数据(批量)
     * @param souQuoteTempDataBatchQueryDto　批量查询报价模板的报价数据的DTO
     */
    @Override
    public SouQuoteTempBatchDataVO batchQueryTempData(SouQuoteTempDataBatchQueryDto souQuoteTempDataBatchQueryDto) {
        Set<String> businessIds = souQuoteTempDataBatchQueryDto.getBusinessIds().stream().map(StringUtils::trimToNull).filter(Objects::nonNull).collect(Collectors.toSet());
        AssertUtils.notEmpty(businessIds, "缺少businessIds参数");
        /* 1: 查询数据 */
        SouQuoteTempVO tempVO = this.getTemp(souQuoteTempDataBatchQueryDto.getTempId());
        Map<Long/* attrId */, SouQuoteTempAttrVO> attrMap = souQuoteTempAttrService
                .listAttrsById(tempVO.getTempLineList().stream().map(SouQuoteTempLine::getAttrId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(attr -> attr.getAttr().getAttrId(), Function.identity()));
        /* 2: 查询报价属性数据 */
        Map<String/* businessId */, Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>>> attrDataBusinessGroupMap = new HashMap<>(attrMap.size());
        Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> fieldQuoteDataMap = new HashMap<>(50);
        if (souQuoteTempDataBatchQueryDto.getQueryTableData() && !attrMap.isEmpty()) {
            for (Long attrId : attrMap.keySet()) {
                List<Map<String, Object>> tempList = souQuoteTempAttrService.queryAttrData(attrId, businessIds, null, null);
                fieldQuoteDataMap.put(attrId, tempList);
                /* 根据businessId分组 */
                Map<String /* businessId */, List<Map<String, Object>>> tempGroupMap = tempList.stream().collect(Collectors.groupingBy(item -> item.get(SouQuoteTempAttrTableColumnVO.BUSINESS_ID).toString()));
                for (String businessId : tempGroupMap.keySet()) {
                    Map<Long, List<Map<String, Object>>> attrIdDataTempData;
                    if ((attrIdDataTempData = attrDataBusinessGroupMap.get(businessId)) != null) {
                        attrIdDataTempData.put(attrId, tempGroupMap.get(businessId));
                    } else {
                        attrIdDataTempData = new HashMap<>(50);
                        attrIdDataTempData.put(attrId, tempGroupMap.get(businessId));
                        attrDataBusinessGroupMap.put(businessId, attrIdDataTempData);
                    }
                }
            }
        }

        /* 3: 组装数据返回 */
        Map<String /* businessId */, SouQuoteTempDataDetailVO> priceDataMap = new HashMap<>(50);
        for (String businessId : attrDataBusinessGroupMap.keySet()) {
            SouQuoteTempDataDetailVO souQuoteTempDataDetailVO = new SouQuoteTempDataDetailVO();
            souQuoteTempDataDetailVO.setData(attrDataBusinessGroupMap.get(businessId));
            priceDataMap.put(businessId, souQuoteTempDataDetailVO);
        }
        return new SouQuoteTempBatchDataVO(tempVO, attrMap, priceDataMap, fieldQuoteDataMap);
    }

    /**
     * 采购商端: 报价计算
     * @param tempId {@link SouQuoteTemp#getTempId}
     * @param businessId 业务单据ID
     * @param tempData 报价数据(如果为空，则从表中获取)
     * @param needRemoveOld 是否需要删除同business下的旧数据
     * @param needRemove 是否在计算完后，删除相关数据
     */
    @Override
    public SouQuoteTempDataDetailVO computeTempData(long tempId, @Nullable String businessId,
                                                    @Nullable Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> tempData,
                                                    boolean needRemoveOld, boolean needRemove) {
        /* 1: 查询数据 */
        SouQuoteTempVO tempVO = this.getTemp(tempId);
        /* 2: 校验+计算 */
        tempVO.getTempLineList().sort(Comparator.comparing(SouQuoteTempLine::getExecuteIndex));
        BigDecimal totalPrice = null;
        RuntimeException ex = null;
        try {
            for (SouQuoteTempLine tempLine : tempVO.getTempLineList()) {
                List<Map<String/* fieldId */, Object>> dataList;
                if (tempData != null) {
                    dataList = tempData.get(tempLine.getAttrId());
                } else {
                    dataList = souQuoteTempAttrService.queryAttrData(tempLine.getAttrId(), Collections.singletonList(businessId), null, null);
                }
                /* 计算 */
                souQuoteTempAttrService.executeCompute(tempLine.getAttrId(), businessId, dataList, true);
                /* 保存 */
                souQuoteTempAttrDataService.writeAttrs(tempLine.getAttrId(), businessId, dataList, false, needRemoveOld);

                if (Enable.Y.equals(tempLine.getIsTotal())) {
                    /* 标记总价了 */
                    if (CollectionUtils.isNotEmpty(dataList)) {
                        SouQuoteTempField field = souQuoteTempFieldRepository.lambdaQuery()
                                .eq(SouQuoteTempField::getAttrId, tempLine.getAttrId())
                                .eq(SouQuoteTempField::getIsTotal, Enable.Y)
                                .one();
                        if (field != null) {
                            totalPrice = new BigDecimal(dataList.get(0).get(field.getFieldId().toString()).toString());
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            ex = e;
        }
        /* 3: 删除数据 */
        Set<Long> attrIds = tempVO.getTempLineList().stream().map(SouQuoteTempLine::getAttrId).collect(Collectors.toSet());
        Map<Long/* attrId */, SouQuoteTempAttr> attrMap = souQuoteTempAttrRepository.lambdaQuery()
                .in(SouQuoteTempAttr::getAttrId, attrIds)
                .list().stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrId, Function.identity()));
        for (SouQuoteTempLine tempLine : tempVO.getTempLineList()) {
            SouQuoteTempAttrTable tableInfo = souQuoteTempAttrTableRepository.lambdaQuery().eq(SouQuoteTempAttrTable::getAttrId, tempLine.getAttrId()).one();
            SouQuoteTempAttr attr = attrMap.get(tempLine.getAttrId());

            /* 删除数据 */
            if (needRemove) {
                MetadataDataDTO deleteParam = new MetadataDataDTO();
                deleteParam.setTableName(tableInfo.getTableName());
                deleteParam.setConditions(Collections.singletonList(new MetadataQueryDetailDTO()));
                if (Enable.N.equals(attr.getIsGlobal())) {
                    MetadataQueryDetailDTO condition = deleteParam.getConditions().get(0);
                    condition.setFieldName(SouQuoteTempAttrTableColumnVO.BUSINESS_ID);
                    condition.setConditionType(ConditionType.EQ);
                    condition.setFieldValue(businessId);
                }
                try {
                    metadataDataService.delete(deleteParam);
                } catch (Exception e) {
                    throw new IllegalArgumentException("清理旧数据失败", e);
                }
            }
        }
        if (ex != null) {
            /* 这样就算计算过程出现报错，也确保可以清除相关数据 */
            throw ex;
        }
        return new SouQuoteTempDataDetailVO(totalPrice, tempData);
    }

}
