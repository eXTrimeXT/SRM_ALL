package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 寻源模块 - 智能评选服务的入参对象定义
 *
 * @author zhangwk12@midea.com
 * @since 2022/03/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SouScoreDimensionContextData extends BaseObjectX {

    /** @see SouOrderItem#getProjectId */
    @ApiModelProperty("寻源单ID")
    protected Long projectId;

    /** @see SouItem#getSouItemId */
    @ApiModelProperty("物料需求ID")
    protected Long souItemId;

    @ApiModelProperty("报价行ID")
    protected Long orderItemId;

    /**
     * 用于标记供应商
     * PS: 可以存放vendorId、vendorCode、vendorName
     *     为何不直接指定 vendorId或者vendorCode，防止有的供应商未建档，还需要走寻源流程，导致这里的智能评选无法正常执行下去
     */
    @ApiModelProperty("用于标记供应商")
    protected Object vendorTab;

    /**
     * 报价(可能是本币未税单价、本币含税单价、本币未税折息价格等)
     * PS: 产品核心默认使用本币未税单价
     */
    @ApiModelProperty("报价")
    protected BigDecimal price;

    @ApiModelProperty("物料组合")
    protected String itemGroup;

    @ApiModelProperty("物料需求数量")
    protected BigDecimal requireQuantity;

    @ApiModelProperty("排名")
    protected Integer ranking;

    @ApiModelProperty("每个维度的得分")
    protected Map<SouScoreRuleDimensionEnum, BigDecimal> dimensionScores = new HashMap<>(8);

    /**
     * 转换为智能评分所需的参数对象
     */
    public static List<SouScoreDimensionContextData> convert2ContextData(List<SouOrderItem> orderItemList) {
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new IllegalArgumentException("缺少评分信息");
        }
        List<SouScoreDimensionContextData> dataList = new ArrayList<>(orderItemList.size());

        SouScoreDimensionContextData data;
        for (SouOrderItem orderItem : orderItemList) {
            data = new SouScoreDimensionContextData();
            dataList.add(data);
            // 唯一标识
            data.setOrderItemId(orderItem.getOrderItemId());
            // 寻源单ID
            data.setProjectId(orderItem.getProjectId());
            // 物料需求ID
            data.setSouItemId(orderItem.getSouItemId());
            // 供应商标识
            data.setVendorTab(orderItem.getVendorId());
            // 报价
            data.setPrice(orderItem.getStandardNotaxPrice());
            // 组合
            data.setItemGroup(orderItem.getItemGroup());
            // 物料需求数量
            data.setRequireQuantity(orderItem.getRequireQuantity());
        }

        return dataList;
    }

    /**
     * 将算分和排名的结果回写
     */
    public static List<SouOrderItem> convertFromContextData(List<SouScoreDimensionContextData> dataList) {
        List<SouOrderItem> orderItemList = new ArrayList<>(dataList.size());
        for (SouScoreDimensionContextData data : dataList) {
            SouOrderItem orderItem = new SouOrderItem();
            orderItemList.add(orderItem);

            orderItem.setOrderItemId(data.getOrderItemId());
            // 价格得分
            orderItem.setPriceScore(data.getDimensionScores().get(SouScoreRuleDimensionEnum.PRICE));
            // 技术得分
            orderItem.setTechScore(data.getDimensionScores().get(SouScoreRuleDimensionEnum.TECHNOLOGY));
            // 综合得分
            orderItem.setCompositeScore(data.getDimensionScores().get(SouScoreRuleDimensionEnum.COMPOSITE));
            // 排名
            orderItem.setRanking(data.getRanking());
        }
        return orderItemList;
    }

}
