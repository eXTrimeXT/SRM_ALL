package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItem;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItemHis;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderFileVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 竞价 - 供应商报价
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Data
public class CompSouVendorViewOrderDetailsWebVO {

    /**
     * @see SouProject#getScoreRuleType
     */
    @ApiModelProperty("评分规则")
    private SouScoreRuleTypeEnum scoreRuleType;
    /**
     * @see SouProject#getOrderWay
     */
    @ApiModelProperty("当前最新轮次")
    private Integer currentRound;
    @ApiModelProperty("报价方式")
    private SouOrderWayEnum orderWay;
    @ApiModelProperty("供应商报价信息")
    private List<VendorViewOrderDetail> orderDetails;
    @ApiModelProperty("供应商报价附件")
    private List<ApiSouOrderFileVO> orderFileList;

    @Data
    private static class VendorViewOrderDetail {
        /**
         * @see SouOrderItem#getOrderItemId
         */
        @ApiModelProperty("报价行ID")
        private Long orderItemId;

        /**
         * @see SouOrderItem#getSouItemId
         */
        @ApiModelProperty("物料需求行ID")
        private Long souItemId;

        @ApiModelProperty("所属单位")
        @TableField("AFFILIATED_UNIT")
        private String affiliatedUnit;

        @ApiModelProperty("投标保证金")
        @TableField("BID_SECURITY")
        private BigDecimal bidSecurity;

        @ApiModelProperty("履约保证金")
        @TableField("PERFORMANCE_BOND")
        private BigDecimal performanceBond;

        @ApiModelProperty("预付款")
        @TableField("ADVANCE_CHARGE")
        private BigDecimal advanceCharge;

        @ApiModelProperty("月约产量")
        @TableField("MONTHLY_PRODUCTION")
        private BigDecimal monthlyProduction;

        @ApiModelProperty("计量单位")
        @TableField("METERING_UNIT")
        private String meteringUnit;

        @ApiModelProperty("起拍价格（元）")
        @TableField("START_PRICE")
        private BigDecimal startPrice;

        @ApiModelProperty("梯次价格（元）")
        @TableField("ECHELON_PRICE")
        private BigDecimal echelonPrice;

        /**
         * @see SouOrderItem#getRound
         */
        @ApiModelProperty("轮次")
        private Integer round;

        /**
         * @see SouItem#getOrgOuName
         */
        @ApiModelProperty("业务实体名称")
        private String orgOuName;

        /**
         * @see SouItem#getOrgInvName
         */
        @ApiModelProperty("库存组织名称")
        private String orgInvName;

        /**
         * @see SouItem#getItemCode
         */
        @ApiModelProperty("物料编码")
        private String itemCode;

        /**
         * @see SouItem#getItemDesc
         */
        @ApiModelProperty("物料名称")
        private String itemDesc;

        /**
         * @see SouItem#getItemGroup
         */
        @ApiModelProperty("组合")
        private String itemGroup;

        /**
         * @see SouItem#getRequireQuantity
         */
        @ApiModelProperty("需求数量")
        private BigDecimal requireQuantity;

        /**
         * @see SouItem#getUnit
         */
        @ApiModelProperty("单位")
        private String unit;

        /**
         * @see SouItem#getRemark
         */
        @ApiModelProperty("物料需求备注")
        private String remark;

        /**
         * @see SouOrderItem#getOrderCurrency
         */
        @ApiModelProperty("供应商的报价币种")
        private String orderCurrency;

        /**
         * @see SouOrderItem#getOrderNotaxPrice
         */
        @ApiModelProperty("原币未税单价")
        private BigDecimal orderNotaxPrice;

        @ApiModelProperty("原币含税单价")
        private BigDecimal orderTaxPrice;

        @ApiModelProperty("本次报价金额")
        private BigDecimal orderNowPrice;

        /**
         * @see SouOrderItem#getStandardNotaxPrice
         */
        @ApiModelProperty("本币未税单价")
        private BigDecimal standardNotaxPrice;

        /**
         * @see SouOrderItem#getOrderRemark
         */
        @ApiModelProperty("报价备注")
        private String orderRemark;

        /**
         * @see SouOrderItem#getPriceStartTime
         */
        @ApiModelProperty("定价开始时间")
        private Date priceStartTime;

        /**
         * @see SouOrderItem#getPriceEndTime
         */
        @ApiModelProperty("定价截止时间")
        private Date priceEndTime;

        /**
         * @see SouOrderItem#getRanking
         */
        @ApiModelProperty("排名")
        private Integer ranking;

        /**
         * @see SouOrderItem#getWinStatus
         */
        @ApiModelProperty("当前轮次是否入围")
        private SouWinStatusEnum winStatus;

        /**
         * @see SouOrderItem#getSelectStatus
         */
        @ApiModelProperty("评选情况")
        private SouSelectStatusEnum selectStatus;

        /**
         * @see CompSouItem#getFormulaValue
         */
        @ApiModelProperty("公式值")
        private String formulaValue;

        /**
         * @see CompSouOrderItem#getFormulaResult
         */
        @ApiModelProperty("供应商填写的公式json")
        private String formulaResult;

        /**
         * @see SouOrderItem#getIsProxy
         */
        @ApiModelProperty("是否代理报价")
        private Enable isProxy;

        /**
         * @see CompSouItemHis#getLatestOrderNotaxPrice
         */
        @ApiModelProperty("原币最新未税单价")
        private BigDecimal latestOrderNotaxPrice;

        /**
         * @see CompSouItemHis#getLatestOrderTaxPrice
         */
        @ApiModelProperty("原币最新含税单价")
        private BigDecimal latestOrderTaxPrice;

    }

    /**
     * 便捷方法
     */
    public static CompSouVendorViewOrderDetailsWebVO convert(SouProject souProject,
                                                             CompSouProject compProject,
                                                             SouRound currentRound,
                                                             List<SouItem> souItemList,
                                                             List<CompSouItem> compItemList,
                                                             Map<String/* souItemId_round */, CompSouItemHis> compSouItemHisMap,
                                                             List<SouOrderItem> souOrderItemList,
                                                             List<CompSouOrderItem> compOrderLineList,
                                                             List<SouFileConfig> fileConfigList,
                                                             List<SouOrderFile> orderFileList) {
        CompSouVendorViewOrderDetailsWebVO vo = new CompSouVendorViewOrderDetailsWebVO();
        /* 评分规则 */
        vo.scoreRuleType = souProject.getScoreRuleType();
        /* 当前轮次 */
        vo.currentRound = souProject.getCurrentRound();
        /* 决标方式 */
        vo.orderWay = souProject.getOrderWay();
        /* 供应商报价信息 */
        vo.orderDetails = doConvertOrderDetails(souProject, compProject, currentRound, souItemList, compItemList, compSouItemHisMap, souOrderItemList, compOrderLineList);
        /* 供应商报价附件 */
        vo.orderFileList = doConvertOrderHeadFiles(fileConfigList, orderFileList);

        return vo;
    }

    private static List<VendorViewOrderDetail> doConvertOrderDetails(SouProject souProject,
                                                                     CompSouProject compProject,
                                                                     SouRound currentRound,
                                                                     List<SouItem> souItemList,
                                                                     List<CompSouItem> compItemList,
                                                                     Map<String/* souItemId_round */, CompSouItemHis> compSouItemHisMap,
                                                                     List<SouOrderItem> souOrderItemList,
                                                                     List<CompSouOrderItem> compOrderItemList) {
        if (souOrderItemList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long/* souItemId */, SouItem> souItemMap = souItemList.stream()
                .collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        Map<Long/* souItemId */, CompSouItem> compItemMap = compItemList.stream()
                .collect(Collectors.toMap(CompSouItem::getSouItemId, Function.identity()));
        Map<Long/* orderItemId */, CompSouOrderItem> compOrderItemMap = compOrderItemList.stream()
                .collect(Collectors.toMap(CompSouOrderItem::getOrderItemId, Function.identity()));

        List<VendorViewOrderDetail> detailList = new ArrayList<>(souOrderItemList.size());
        for (SouOrderItem souOrderItem : souOrderItemList) {
            VendorViewOrderDetail detail = new VendorViewOrderDetail();
            detailList.add(detail);
            SouItem souItem = souItemMap.get(souOrderItem.getSouItemId());
            CompSouItem compItem = new CompSouItem();
            CompSouOrderItem compOrderItem = new CompSouOrderItem();

            BeanUtils.copyProperties(souItem, detail);
            BeanUtils.copyProperties(compItem, detail);
            BeanUtils.copyProperties(souOrderItem, detail);
            BeanUtils.copyProperties(compOrderItem, detail);

            CompSouItemHis itemHis = new CompSouItemHis();
            detail.setLatestOrderNotaxPrice(itemHis.getLatestOrderNotaxPrice());
            detail.setLatestOrderTaxPrice(itemHis.getLatestOrderTaxPrice());
        }

        /* 处理敏感字段 */
        handleSensitiveFields(souProject, compProject, new SouRound(), detailList);

        return detailList;
    }

    private static List<ApiSouOrderFileVO> doConvertOrderHeadFiles(List<SouFileConfig> fileConfigList,
                                                                   List<SouOrderFile> orderFileList) {
        if (orderFileList.isEmpty()) {
            return Collections.emptyList();
        }
        List<ApiSouOrderFileVO> voList = new ArrayList<>(fileConfigList.size() + orderFileList.size());
        Map<Long/* fileConfigId */, SouFileConfig> fileConfigMap = fileConfigList.stream()
                .collect(Collectors.toMap(SouFileConfig::getSouFileConfigId, Function.identity()));

        for (SouOrderFile orderFile : orderFileList) {
            ApiSouOrderFileVO vo = SouObjectXUtil.convertTargetObj(orderFile, ApiSouOrderFileVO.class);
            voList.add(vo);

            if (vo.getSouFileConfigId() != null) {
                SouObjectXUtil.mergeProperties(fileConfigMap.get(vo.getSouFileConfigId()), vo);
            }
        }
        return voList;
    }

    /**
     * 处理敏感字段
     */
    private static void handleSensitiveFields(SouProject souProject, CompSouProject compProject, SouRound currentRound, List<VendorViewOrderDetail> detailList) {
        boolean canShowSelectStatus = SouProjectStatusEnum.PRICE_END.equals(souProject.getProjectStatus());
        for (VendorViewOrderDetail detail : detailList) {
            if (!canShowSelectStatus) {
                /* 已定价 */
                detail.setSelectStatus(null);
            }
        }
    }

}
