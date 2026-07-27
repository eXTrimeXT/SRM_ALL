package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select;

import com.google.common.collect.Lists;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempDataBatchQueryDto;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempFieldTypeEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectDetailDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author haibo1.huang@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录修改后版本:
 * 修改人:
 * 修改日期: 2023/8/11 17:30、
 * 修改内容:
 * </pre>
 */
@Data
@Accessors(chain = true)
@ApiModel("模板报价的比价数据模型－分项维度")
public class ApiSouTempSelectDataVO extends BaseObjectX {
    @ApiModelProperty("寻源核心 - 寻源单id")
    private Long projectId;
    @ApiModelProperty("寻源核心 - 物料需求行ID")
    private Long souItemId;
    @ApiModelProperty("物料ID")
    private Long itemId;
    @ApiModelProperty("物料编码")
    private String itemCode;
    @ApiModelProperty("物料名称")
    private String itemDesc;
    @ApiModelProperty("需求数量")
    private BigDecimal requireQuantity;
    @ApiModelProperty("轮次")
    private Integer round;
    @ApiModelProperty("属性id")
    private Long attrId;
    @ApiModelProperty("分项名称（报价属性字段的名称）")
    private String quoteFieldName;
    @ApiModelProperty("分项ID（报价属性字段的id）")
    private Long quoteFieldId;
    @ApiModelProperty("分项动态列数据")
    private Map<String, Object> dynamicColMap;
    @ApiModelProperty("分项明细数据")
    private List<ApiSouTempSelectDetailDataVO> souTempSelectDetailDataVOList;
    @ApiModelProperty("分项明细动态列")
    private List<com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectVO.DynamicCol> souTempSelectDetailDynamicColList;
    @ApiModelProperty("异常提示")
    private String errorMessage;

    /**
     * 组装数据
     * @param souItem 寻源核心 - 物料需求行
     * @param round 轮次
     * @param field 寻源模型报价模板字段
     * @param dynamicColMap 动态列数据
     * @return 模板报价的比价数据模型
     */
    public static ApiSouTempSelectDataVO convertSouTempSelectDataVO(SouItem souItem, Integer round, SouQuoteTempField field, Map<String, Object> dynamicColMap) {
        ApiSouTempSelectDataVO selectDataVO = new ApiSouTempSelectDataVO();
        selectDataVO.setProjectId(souItem.getProjectId());
        selectDataVO.setSouItemId(souItem.getSouItemId());
        selectDataVO.setItemId(souItem.getItemId());
        selectDataVO.setItemCode(souItem.getItemCode());
        selectDataVO.setItemDesc(souItem.getItemDesc());
        selectDataVO.setRequireQuantity(souItem.getRequireQuantity());
        selectDataVO.setRound(round);
        selectDataVO.setAttrId(field.getAttrId());
        selectDataVO.setQuoteFieldId(field.getFieldId());
        selectDataVO.setQuoteFieldName(field.getFieldName());
        selectDataVO.setDynamicColMap(dynamicColMap);
        return selectDataVO;
    }

    /**
     * 在寻源物料和、轮次和分项（报价模板中总价类型的属性）的维度，组装各个供应商的分项报价数据
     *
     * @param souItemList 寻源物料
     * @param souRoundList 轮次
     * @param fieldList 报价模板：报价属性：属性字段
     * @param souVendorList 寻源供应商
     * @param projectId 寻源id
     * @param priceDataMap 报价模板：报价属性：属性字段：数据值
     * @return 模板报价的比价数据模型－分项维度
     */
    public static List<ApiSouTempSelectDataVO> convertSouTempSelectDataListVO(List<SouItem> souItemList,
                                                                              List<Integer> souRoundList,
                                                                              List<SouQuoteTempField> fieldList,
                                                                              List<SouVendor> souVendorList,
                                                                              Long projectId,
                                                                              Map<String /* businessId */, SouQuoteTempDataDetailVO> priceDataMap,
                                                                              Map<String /* dictCode */, Map<String /* dictItemCode */, DictItemDTO /* dictItemName */>> dictItemMap
    ) {
        List<ApiSouTempSelectDataVO> souTempSelectDataVOList = Lists.newArrayList();
        for (SouItem souItem : souItemList) {
            for (Integer round : souRoundList) {
                for (SouQuoteTempField field : fieldList) {
                    // 动态列数据
                    Map<String, Object> dynamicColMap = new HashMap<>(50);
                    for (SouVendor souVendor : souVendorList) {
                        String businessId = SouQuoteTempDataBatchQueryDto.covertSouQuoteTempDataBusinessId(projectId, round, souVendor.getVendorId(), souItem.getSouItemId());
                        SouQuoteTempDataDetailVO souQuoteTempDataDetailVO = priceDataMap.get(businessId);
                        if (souQuoteTempDataDetailVO == null) {
                            continue;
                        }
                        Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> attrFieldDataMap = souQuoteTempDataDetailVO.getData();
                        if (attrFieldDataMap == null || attrFieldDataMap.isEmpty()) {
                            continue;
                        }
                        List<Map<String, Object>> fieldDataList = attrFieldDataMap.get(field.getAttrId());
                        if (CollectionUtils.isEmpty(fieldDataList)) {
                            continue;
                        }
                        Object value = fieldDataList.get(0).get(field.getFieldId().toString());
                        // 字段转换
                        value = field.convertDictFileValue(value, dictItemMap);
                        dynamicColMap.put(ApiSouTempSelectVO.DynamicCol.PROP_COMMON_PART_FIELD_QUOTE_VALUE + souVendor.getVendorCode(), value);
                    }
                    ApiSouTempSelectDataVO selectDataVO = ApiSouTempSelectDataVO.convertSouTempSelectDataVO(souItem, round, field, dynamicColMap);
                    souTempSelectDataVOList.add(selectDataVO);
                }
            }
        }
        return souTempSelectDataVOList;
    }



}
