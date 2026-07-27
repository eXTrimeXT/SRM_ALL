package com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.init;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouItem;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招投标openAPI - 物料需求
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouItemVO extends ApiSouItemVO {

    /** @see BidSouItem#getOrderType */
    @ApiModelProperty("报价方式(冗余字段)")
    private SouOrderTypeEnum orderType;

    /** @see BidSouItem#getMaterialFormulaRelateId */
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;

    /** @see BidSouItem#getFormulaId */
    @ApiModelProperty("公式id")
    private Long formulaId;

    /** @see BidSouItem#getFormulaName */
    @ApiModelProperty("公式名称")
    private String formulaName;

    /** @see BidSouItem#getFormulaValue */
    @ApiModelProperty("公式值")
    private String formulaValue;

    /** @see BidSouItem#getTargetPrice */
    @ApiModelProperty("拦标价")
    private BigDecimal targetPrice;

    /** @see BidSouItem#getDeliveryPlace */
    @ApiModelProperty("交货地点")
    private String deliveryPlace;

    /** @see BidSouItem#getPriceType */
    @ApiModelProperty("价格类型[字典值: PRICE_TYPE]")
    private String priceType;

    /** @see BidSouItem#getPurchaseType */
    @ApiModelProperty("采购类型")
    private String purchaseType;

    /** @see BidSouItem#getTradeTerm */
    @ApiModelProperty("贸易条款[字典值: trade_clause]")
    private String tradeTerm;

    /** @see BidSouItem#getTransportType */
    @ApiModelProperty("运输方式[字典值: TRANSF_TYPE]")
    private String transportType;

    /** @see BidSouItem#getWarrantyPeriod */
    @ApiModelProperty("保修期(月)")
    private Integer warrantyPeriod;

    @ApiModelProperty("项目需求信息-标准附件")
    private List<SceneFile> itemFiles;

    public static List<ApiBidSouItemVO> convertBidVO(List<SouItem> souItemList,
                                                     List<BidSouItem> bidItemList) {
        if (bidItemList.isEmpty()) { return Collections.emptyList(); }

        Map<Long/* souItemId */, BidSouItem> bidItemMap = bidItemList.stream()
                .collect(Collectors.toMap(BidSouItem::getSouItemId, Function.identity()));

        List<ApiBidSouItemVO> voList = new ArrayList<>(bidItemList.size());
        for (SouItem souItem : souItemList) {
            ApiBidSouItemVO vo = new ApiBidSouItemVO();
            voList.add(vo);

            BeanUtils.copyProperties(souItem, vo);

            BidSouItem bidSouItem = bidItemMap.get(souItem.getSouItemId());
            BeanUtils.copyProperties(bidSouItem, vo);
        }
        return voList;
    }

}
