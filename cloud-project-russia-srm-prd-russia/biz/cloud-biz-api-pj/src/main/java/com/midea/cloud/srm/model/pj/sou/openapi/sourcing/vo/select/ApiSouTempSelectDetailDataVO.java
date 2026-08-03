package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select;

import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempDataBatchQueryDto;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrTableColumnVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempBatchDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 *  模板报价的比价数据模型－分项明细维度
 * </pre>
 *
 * @author haibo1.huang@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录修改后版本:
 * 修改人:
 * 修改日期: 2023/8/11 18:12、
 * 修改内容:
 * </pre>
 */
@Data
@Accessors(chain = true)
@ApiModel("模板报价的比价数据模型－分项明细维度")
public class ApiSouTempSelectDetailDataVO extends BaseObjectX {

    @ApiModelProperty("供应商id")
    private Long vendorId;
    @ApiModelProperty("供应商名称")
    private String vendorName;
    @ApiModelProperty("供应商编码")
    private String vendorCode;
    @ApiModelProperty("动态列数据")
    private Map<String, Object> dynamicColDataMap;

    /**
     * 设置模板报价的比价数据（分项明细维度）
     * @param souTempSelectDataVOList
     * @param souQuoteTempBatchDataVO
     * @param souVendorList
     */
    public static void covertSouTempSelectDetailDataList(List<ApiSouTempSelectDataVO> souTempSelectDataVOList,
                                                     SouQuoteTempBatchDataVO souQuoteTempBatchDataVO,
                                                     List<SouVendor> souVendorList,
                                                     Map<Long/* fieldId */, List<Long/* attrId */>> refAttrIdsByFieldIds,
                                                     Map<String /* dictCode */, Map<String /* dictItemCode */, DictItemDTO /* dictItemName */>> dictItemMap) {

        // 1.报价模板相关数据
        Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> fieldQuoteDataMap = souQuoteTempBatchDataVO.getFieldQuoteDataMap();
        Map<Long, SouQuoteTempAttrVO> attrMap = souQuoteTempBatchDataVO.getAttrMap();

        for (ApiSouTempSelectDataVO selectDataVO : souTempSelectDataVOList) {
            // 2.获取出分项明细的属性数据
            List<Long> attrIdList = refAttrIdsByFieldIds.get(selectDataVO.getQuoteFieldId());
            if (CollectionUtils.isEmpty(attrIdList)) {
                selectDataVO.setErrorMessage("该分项没有分项明细数据");
                continue;
            }
            if (attrIdList.size() > 1) {
                selectDataVO.setErrorMessage("该分项公式存在多个分项明细属性,该报表形式无法满足显示");
                continue;
            }
            // 3.获取分项明细属性下的属性字段明细、属性字段的报价数据
            Long attrId = attrIdList.get(0);
            SouQuoteTempAttrVO souQuoteTempAttrVO = attrMap.get(attrId);
            List<SouQuoteTempField> fieldList = souQuoteTempAttrVO.getFieldList();
            List<Map<String/* fieldId */, Object>> quoteDataList = fieldQuoteDataMap.get(attrId);
            // 根据businessId分组
            Map<String /* businessId */, List<Map<String /* fieldId */, Object>>> quoteDataListMap = quoteDataList.stream()
                    .collect(Collectors.groupingBy(item -> item.get(SouQuoteTempAttrTableColumnVO.BUSINESS_ID).toString()));

            // 4.分项明细的报价数据, 按供应商维度展开
            List<ApiSouTempSelectDetailDataVO> souTempSelectDetailDataVOList = new ArrayList<>();
            for (SouVendor souVendor : souVendorList) {
                String businessId = SouQuoteTempDataBatchQueryDto.covertSouQuoteTempDataBusinessId(selectDataVO.getProjectId(),
                        selectDataVO.getRound(), souVendor.getVendorId(), selectDataVO.getSouItemId());
                List<Map<String /* fieldId */, Object>> quoteDataTempList = quoteDataListMap.get(businessId);
                // 4.1 收集分项明细的报价数据（即动态列数据）
                if (CollectionUtils.isNotEmpty(quoteDataTempList)) {
                    for (Map<String /* fieldId */, Object> attrFieldQuoteData : quoteDataTempList) {
                        Map<String, Object> dynamicColData = new HashMap<>(50);
                        for (SouQuoteTempField field : fieldList) {
                            Object value = attrFieldQuoteData.get(field.getFieldId().toString());
                            // 字段转换
                            value = field.convertDictFileValue(value, dictItemMap);
                            dynamicColData.put(ApiSouTempSelectVO.DynamicCol.PROP_COMMON_PART_FIELD_QUOTE_VALUE + field.getFieldId(), value);
                        }
                        ApiSouTempSelectDetailDataVO souTempSelectDetailDataVO = new ApiSouTempSelectDetailDataVO();
                        souTempSelectDetailDataVO.setVendorId(souVendor.getVendorId());
                        souTempSelectDetailDataVO.setVendorName(souVendor.getVendorName());
                        souTempSelectDetailDataVO.setVendorCode(souVendor.getVendorCode());
                        souTempSelectDetailDataVO.setDynamicColDataMap(dynamicColData);
                        souTempSelectDetailDataVOList.add(souTempSelectDetailDataVO);
                    }
                }
            }
            // 设置分项明细的报价数据
            selectDataVO.setSouTempSelectDetailDataVOList(souTempSelectDetailDataVOList);

            // 设置分项明细的动态列
            List<ApiSouTempSelectVO.DynamicCol> dynamicColList = new ArrayList<>();
            for (SouQuoteTempField field : fieldList) {
                dynamicColList.add(new ApiSouTempSelectVO.DynamicCol(ApiSouTempSelectVO.DynamicCol.PROP_COMMON_PART_FIELD_QUOTE_VALUE + field.getFieldId(), field.getFieldName()));
            }
            selectDataVO.setSouTempSelectDetailDynamicColList(dynamicColList);
        }

    }


}
