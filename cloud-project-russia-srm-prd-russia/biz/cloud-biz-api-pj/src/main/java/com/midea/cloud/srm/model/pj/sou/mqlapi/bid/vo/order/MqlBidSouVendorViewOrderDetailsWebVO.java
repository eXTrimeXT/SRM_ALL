package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.order;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouItem;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProject;
import com.midea.cloud.srm.model.pj.sou.bid.enums.BidSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderFileVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouWinStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招投标MQL - 供应商报价
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
public class MqlBidSouVendorViewOrderDetailsWebVO {

    /** @see BidSouProcessConfig#getBargainType */
    @ApiModelProperty("询价类型")
    private BidSouTypeEnum bargainType;
    /** @see SouProject#getScoreRuleType */
    @ApiModelProperty("评分规则")
    private SouScoreRuleTypeEnum scoreRuleType;
    /** @see SouProject#getOrderWay */
    @ApiModelProperty("当前最新轮次")
    private Integer currentRound;
    @ApiModelProperty("报价方式")
    private SouOrderWayEnum orderWay;
    @ApiModelProperty("供应商报价信息")
    private List<VendorViewOrderDetail> orderDetails;
    @ApiModelProperty("供应商报价附件")
    private List<MqlSouOrderFileVO> orderFileList;

    @Data
    private static class VendorViewOrderDetail {
        /** @see SouOrderItem#getOrderItemId */
        @ApiModelProperty("报价行ID")
        private Long orderItemId;

        /** @see SouOrderItem#getSouItemId */
        @ApiModelProperty("物料需求行ID")
        private Long souItemId;

        /** @see SouOrderItem#getRound */
        @ApiModelProperty("轮次")
        private Integer round;

        /** @see SouItem#getOrgOuName */
        @ApiModelProperty("业务实体名称")
        private String orgOuName;

        /** @see SouItem#getOrgInvName */
        @ApiModelProperty("库存组织名称")
        private String orgInvName;

        /** @see SouItem#getItemCode */
        @ApiModelProperty("物料编码")
        private String itemCode;

        /** @see SouItem#getItemDesc */
        @ApiModelProperty("物料名称")
        private String itemDesc;

        /** @see SouItem#getItemGroup */
        @ApiModelProperty("组合")
        private String itemGroup;

        /** @see SouItem#getRequireQuantity */
        @ApiModelProperty("需求数量")
        private BigDecimal requireQuantity;

        /** @see SouItem#getUnit */
        @ApiModelProperty("单位")
        private String unit;

        /** @see SouItem#getRemark */
        @ApiModelProperty("物料需求备注")
        private String remark;

        /** @see SouOrderItem#getOrderCurrency */
        @ApiModelProperty("供应商的报价币种")
        private String orderCurrency;

        /** @see SouOrderItem#getOrderNotaxPrice */
        @ApiModelProperty("原币未税单价")
        private BigDecimal orderNotaxPrice;

        /** @see SouOrderItem#getStandardNotaxPrice */
        @ApiModelProperty("本币未税单价")
        private BigDecimal standardNotaxPrice;

        /** @see SouOrderItem#getOrderRemark */
        @ApiModelProperty("报价备注")
        private String orderRemark;

        /**
         * PS: 如果是历史轮次数据，本字段受 {@link BidSouProject#getPublicLowestPrice} 字段影响
         * PS: 如果是最终轮次数据，本字段受 {@link BidSouProject#getVisibleFinalPrice} 字段影响
         * @see SouOrderItem#getStandardNotaxMinPrice
         */
        @ApiModelProperty("本轮未税最低价")
        private BigDecimal standardNotaxMinPrice;

        /**
         * PS: 如果是历史轮次数据，本字段受 {@link BidSouProject#getPublicLowestPrice} 字段影响
         * PS: 如果是最终轮次数据，本字段受 {@link BidSouProject#getVisibleFinalPrice} 字段影响
         * @see SouOrderItem#getStandardNotaxMaxPrice
         */
        @ApiModelProperty("本轮未税最高价")
        private BigDecimal currentRoundMaxNotaxPrice;

        /** @see SouOrderItem#getPriceStartTime */
        @ApiModelProperty("定价开始时间")
        private Date priceStartTime;

        /** @see SouOrderItem#getPriceEndTime */
        @ApiModelProperty("定价截止时间")
        private Date priceEndTime;

        /** @see SouOrderItem#getWinStatus */
        @ApiModelProperty("当前轮次是否入围")
        private SouWinStatusEnum winStatus;

        /**
         * PS: 如果是历史轮次数据，本字段受 {@link BidSouProject#getPublicTotalRank} 字段影响
         * PS: 如果是最终轮次数据，本字段受 {@link BidSouProject#getVisibleRankResult} 字段影响
         * @see SouOrderItem#getRanking
         */
        @ApiModelProperty("排名")
        private Integer ranking;

        /** @see BidSouItem#getPriceType */
        @ApiModelProperty("价格类型")
        private String priceType;

        /** @see BidSouItem#getFormulaValue */
        @ApiModelProperty("公式值")
        private String formulaValue;

        /** @see BidSouOrderItem#getFormulaResult */
        @ApiModelProperty("供应商填写的公式json")
        private String formulaResult;

        /** @see SouOrderItem#getIsProxy */
        @ApiModelProperty("是否代理报价")
        private Enable isProxy;

    }

    /**
     * 便捷方法
     */
    public static MqlBidSouVendorViewOrderDetailsWebVO convert(SouProject souProject,
                                                               BidSouProject bidProject,
                                                               SouRound currentRound,
                                                               List<SouItem> souItemList,
                                                               List<BidSouItem> bidItemList,
                                                               List<SouOrderItem> souOrderItemList,
                                                               List<BidSouOrderItem> bidOrderLineList,
                                                               List<SouFileConfig> fileConfigList,
                                                               List<SouOrderFile> orderFileList) {
        MqlBidSouVendorViewOrderDetailsWebVO vo = new MqlBidSouVendorViewOrderDetailsWebVO();
        /* 询价类型 */
        vo.bargainType = bidProject.getBargainType();
        /* 评分规则 */
        vo.scoreRuleType = souProject.getScoreRuleType();
        /* 当前轮次 */
        vo.currentRound = souProject.getCurrentRound();
        /* 决标方式 */
        vo.orderWay = souProject.getOrderWay();
        /* 供应商报价信息 */
        vo.orderDetails = doConvertOrderDetails(bidProject, currentRound, souItemList, bidItemList, souOrderItemList, bidOrderLineList);
        /* 供应商报价附件 */
        vo.orderFileList = doConvertOrderHeadFiles(fileConfigList, orderFileList);

        return vo;
    }

    private static List<VendorViewOrderDetail> doConvertOrderDetails(BidSouProject bidProject,
                                                                     SouRound currentRound,
                                                                     List<SouItem> souItemList,
                                                                     List<BidSouItem> bidItemList,
                                                                     List<SouOrderItem> souOrderItemList,
                                                                     List<BidSouOrderItem> bidOrderItemList) {
        if (souOrderItemList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long/* souItemId */, SouItem> souItemMap = souItemList.stream()
                .collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        Map<Long/* souItemId */, BidSouItem> bidItemMap = bidItemList.stream()
                .collect(Collectors.toMap(BidSouItem::getSouItemId, Function.identity()));
        Map<Long/* orderItemId */, BidSouOrderItem> bidOrderItemMap = bidOrderItemList.stream()
                .collect(Collectors.toMap(BidSouOrderItem::getOrderItemId, Function.identity()));

        List<VendorViewOrderDetail> detailList = new ArrayList<>(souOrderItemList.size());
        for (SouOrderItem souOrderItem : souOrderItemList) {
            VendorViewOrderDetail detail = new VendorViewOrderDetail();
            detailList.add(detail);
            SouItem souItem = souItemMap.get(souOrderItem.getSouItemId());
            BidSouItem bidItem = bidItemMap.get(souOrderItem.getSouItemId());
            BidSouOrderItem bidOrderItem = bidOrderItemMap.get(souOrderItem.getOrderItemId());

            BeanUtils.copyProperties(souItem, detail);
            BeanUtils.copyProperties(bidItem, detail);
            BeanUtils.copyProperties(souOrderItem, detail);
            BeanUtils.copyProperties(bidOrderItem, detail);
        }

        /* 处理敏感字段 */
        handleSensitiveFields(bidProject, currentRound, detailList);

        return detailList;
    }

    private static List<MqlSouOrderFileVO> doConvertOrderHeadFiles(List<SouFileConfig> fileConfigList, List<SouOrderFile> orderFileList) {
        if (orderFileList.isEmpty()){
            return Collections.emptyList();
        }
        List<MqlSouOrderFileVO> voList = new ArrayList<>(fileConfigList.size() + orderFileList.size());
        Map<Long/* fileConfigId */, SouFileConfig> fileConfigMap = fileConfigList.stream()
                .collect(Collectors.toMap(SouFileConfig::getSouFileConfigId, Function.identity()));

        for (SouOrderFile orderFile : orderFileList) {
            MqlSouOrderFileVO vo = SouObjectXUtil.convertTargetObj(orderFile, MqlSouOrderFileVO.class);
            voList.add(vo);

            if (vo.getSouFileConfigId() != null) {
                SouObjectXUtil.mergeProperties(fileConfigMap.get(vo.getSouFileConfigId()), vo);
            }
        }
        return voList;
    }


    /** 处理敏感字段 */
    private static void handleSensitiveFields(BidSouProject bidProject, SouRound currentRound, List<VendorViewOrderDetail> detailList) {
//        是否公开上轮最低价
        boolean canShowLastRoundMinMaxPrice = Enable.Y.equals(bidProject.getPublicLowestPrice());
//        是否可以公开上轮排名结果
        boolean canShowLastRoundRank = Enable.Y.equals(bidProject.getPublicTotalRank()) ;

        for (VendorViewOrderDetail detail : detailList) {
            /* 本轮未税最低价 */
            if (canShowLastRoundMinMaxPrice) {
                if (currentRound.getRound().equals(detail.getRound())) {
                    /* 当前轮次(必须要本轮公开结果后，才可以查看) */
                    if (!Enable.Y.equals(currentRound.getHasPublishResult())) {
                        detail.standardNotaxMinPrice = null;
                    }
                }
            } else {
                detail.standardNotaxMinPrice = null;
            }
            /* 本轮未税最高价 */
            if (canShowLastRoundMinMaxPrice) {
                if (currentRound.getRound().equals(detail.getRound())) {
                    /* 当前轮次(必须要本轮公开结果后，才可以查看) */
                    if (!Enable.Y.equals(currentRound.getHasPublishResult())) {
                        detail.currentRoundMaxNotaxPrice = null;
                    }
                }
            } else {
                detail.setCurrentRoundMaxNotaxPrice(null);
            }
            /* 本轮是否入围 */
            if (currentRound.getRound().equals(detail.getRound())) {
                /* 当前轮次(必须要本轮公开结果后，才可以查看) */
                if (!Enable.Y.equals(currentRound.getHasPublishResult())) {
                    detail.winStatus = null;
                }
            }
            /* 排名 */
            if (canShowLastRoundRank) {
                if (currentRound.getRound().equals(detail.getRound())) {
                    /* 当前轮次(必须要本轮公开结果后，才可以查看) */
                    if (!Enable.Y.equals(currentRound.getHasPublishResult())) {
                        detail.ranking = null;
                    }
                }
            } else {
                detail.ranking = null;
            }
        }
    }

}
