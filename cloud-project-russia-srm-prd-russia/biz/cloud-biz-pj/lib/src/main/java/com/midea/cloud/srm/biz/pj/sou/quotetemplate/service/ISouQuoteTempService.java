package com.midea.cloud.srm.biz.pj.sou.quotetemplate.service;

import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempDataBatchQueryDto;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempQueryDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.*;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 寻源 - 模型报价模板
 *
 * @author zhangwk12@midea.com
 * @since 2022/08/02
 */
public interface ISouQuoteTempService {

    /**
     * 采购商端: 列表查询报价模板
     * @param queryParam
     * @return
     */
    List<SouQuoteTemp> listTemps(SouQuoteTempQueryDTO queryParam);

    /**
     * 采购商端: 查询报价模板详情
     * @param tempId {@link SouQuoteTemp#getTempId}
     * @return
     */
    SouQuoteTempVO getTemp(long tempId);

    /**
     * 采购商端: 校验添加的报价属性是否是完整的
     * PS: 假设报价属性有如下的引用链 "[A] -> [B] -> [C]"
     *     如果仅选择了"[A]"，那么就会报错，引用链上的所有有效的报价属性都必须被选择
     * @param attrIds {@link SouQuoteTempAttr#getAttrId}
     * @return
     */
    SouQuoteTempAttrRelateVO checkTempAttrs(Set<Long> attrIds);

    /**
     * 采购商端: 编辑/提交报价模板
     * @param param 报价模板信息
     * @param isTempSave true-暂存/false-提交
     * @return
     */
    long/* tempId */ editTemp(SouQuoteTempEditDTO param, boolean isTempSave);

    /**
     * 采购商端: 删除报价模板
     * @param tempId {@link SouQuoteTemp#getTempId}
     */
    void removeTemp(long tempId);

    /**
     * 采购商端: 生效报价模板
     * @param tempId {@link SouQuoteTemp#getTempId}
     */
    void validTemp(long tempId);

    /**
     * 采购商端: 失效报价模板
     * @param tempId {@link SouQuoteTemp#getTempId}
     */
    void invalidTemp(long tempId);

    /**
     * 采购商端: 复制报价模板
     * @param tempId {@link SouQuoteTemp#getTempId}
     * @return
     */
    SouQuoteTemp copyTemp(long tempId);

    /**
     * 采购商端: 查询报价模板数据
     * @param tempId {@link SouQuoteTemp#getTempId}
     * @param businessId 业务单据ID
     * @param queryTableData 是否需要查询实际表数据
     * @return
     */
    SouQuoteTempDataVO queryTempData(long tempId, String businessId, boolean queryTableData);

    /**
     * 查询报价模板数据(批量)
     * @param souQuoteTempDataBatchQueryDto 批量查询报价模板的报价数据的DTO
     * @return
     */
    SouQuoteTempBatchDataVO batchQueryTempData(SouQuoteTempDataBatchQueryDto souQuoteTempDataBatchQueryDto);

    /**
     * 采购商端: 报价计算
     * @param tempId {@link SouQuoteTemp#getTempId}
     * @param businessId 业务单据ID
     * @param tempData 报价数据(如果为空，则从表中获取)
     * @param needRemoveOld 是否需要删除同business下的旧数据
     * @param needRemove 是否在计算完后，删除相关数据
     * @return
     */
    SouQuoteTempDataDetailVO computeTempData(long tempId, @Nullable String businessId,
                                             @Nullable Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> tempData,
                                             boolean needRemoveOld, boolean needRemove);

}
