package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.init;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.init.ApiBidSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init.ApiBrgSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init.ApiInqSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 物料需求信息 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouInitDetailVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "物料需求信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouItemSwaggerVO extends ApiSouItemVO {

    /** @see ApiInqSouItemVO#getLadderType */
    @ApiModelProperty("阶梯价类型(standard-标准阶梯价、sum-累计阶梯价)(仅用于简易询价-inq)")
    private String ladderType;

    /** @see ApiInqSouItemVO#getIsFormula */
    @ApiModelProperty("是否公式报价(仅用于简易询价-inq)")
    private Enable isFormula;

    /**
     * @see ApiInqSouItemVO#getMaterialFormulaRelateId
     * @see ApiBidSouItemVO#getMaterialFormulaRelateId
     * @see ApiBrgSouItemVO#getMaterialFormulaRelateId
     */
    @ApiModelProperty("物料价格公式关联ID(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private Long materialFormulaRelateId;

    /**
     * @see ApiInqSouItemVO#getFormulaId
     * @see ApiBidSouItemVO#getFormulaId
     * @see ApiBrgSouItemVO#getFormulaId
     */
    @ApiModelProperty("公式ID(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private Long formulaId;

    /** @see ApiInqSouItemVO#getItemType */
    @ApiModelProperty("行类型(仅用于简易询价-inq)[字典值: DMAND_LINE_TYPE]")
    private String itemType;

    /**
     * @see ApiInqSouItemVO#getFormulaName
     * @see ApiBidSouItemVO#getFormulaName
     * @see ApiBrgSouItemVO#getFormulaName
     */
    @ApiModelProperty("公式名称(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private String formulaName;

    /**
     * @see ApiInqSouItemVO#getFormulaValue
     * @see ApiBidSouItemVO#getFormulaValue
     * @see ApiBrgSouItemVO#getFormulaValue
     */
    @ApiModelProperty("公式值(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private String formulaValue;

    /** @see ApiInqSouItemVO#getNotaxTargetPrice */
    @ApiModelProperty("未税目标价(仅用于简易询价-inq)")
    private BigDecimal notaxTargetPrice;

    /**
     * @see ApiInqSouItemVO#getItemFiles
     * @see ApiBidSouItemVO#getItemFiles
     * @see ApiBrgSouItemVO#getItemFiles
     * @see ApiCompSouItemVO#getItemFiles
     */
    @ApiModelProperty("附件(仅用于简易询价-inq/bid-招投标/竞价-comp)")
    private List<SceneFile> itemFiles;

    /**
     * @see ApiBidSouItemVO#getOrderType
     * @see ApiBrgSouItemVO#getOrderType
     */
    @ApiModelProperty("报价方式(冗余字段)(仅用于招投标-bid/项目式询价-brg)")
    private SouOrderTypeEnum orderType;

    /**
     * @see ApiBidSouItemVO#getTargetPrice
     * @see ApiBrgSouItemVO#getTargetPrice
     */
    @ApiModelProperty("拦标价(仅用于招投标-bid/项目式询价-brg)")
    private BigDecimal targetPrice;

    /**
     * @see ApiBidSouItemVO#getDeliveryPlace
     * @see ApiBrgSouItemVO#getDeliveryPlace
     * @see ApiCompSouItemVO#getDeliveryPlace
     * */
    @ApiModelProperty("交货地点(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String deliveryPlace;

    /**
     * @see ApiBidSouItemVO#getPriceType
     * @see ApiBrgSouItemVO#getPriceType
     * @see ApiCompSouItemVO#getPriceType
     */
    @ApiModelProperty("价格类型(仅用于招投标-bid/项目式询价-brg/竞价-comp)[字典值: PRICE_TYPE]")
    private String priceType;

    /**
     * @see ApiBidSouItemVO#getPurchaseType
     * @see ApiBrgSouItemVO#getPurchaseType
     */
    @ApiModelProperty("采购类型(仅用于招投标-bid/项目式询价-brg)")
    private String purchaseType;

    /**
     * @see ApiBidSouItemVO#getTradeTerm
     * @see ApiBrgSouItemVO#getTradeTerm
     * @see ApiCompSouItemVO#getTradeTerm
     */
    @ApiModelProperty("贸易条款(仅用于招投标-bid/项目式询价-brg/竞价-comp)[字典值: trade_clause]")
    private String tradeTerm;

    /**
     * @see ApiBidSouItemVO#getTransportType
     * @see ApiBrgSouItemVO#getTransportType
     */
    @ApiModelProperty("运输方式(仅用于招投标-bid/项目式询价-brg)[字典值: TRANSF_TYPE]")
    private String transportType;

    /**
     * @see ApiBidSouItemVO#getWarrantyPeriod
     * @see ApiBrgSouItemVO#getWarrantyPeriod
     * @see ApiCompSouItemVO#getWarrantyPeriod
     */
    @ApiModelProperty("保修期(月)(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private Integer warrantyPeriod;

    /** @see ApiCompSouItemVO#getOrderCurrency */
    @ApiModelProperty("报价币种(仅用于竞价-comp)")
    private String orderCurrency;

    /** @see ApiCompSouItemVO#getTaxKey */
    @ApiModelProperty("税率编码(仅用于竞价-comp)")
    private String taxKey;

    /** @see ApiCompSouItemVO#getTaxRate */
    @ApiModelProperty("税率(仅用于竞价-comp)")
    private BigDecimal taxRate;

    /** @see ApiCompSouItemVO#getStartOrderNotaxPrice */
    @ApiModelProperty("起拍价(原币未税)(仅用于竞价-comp)")
    private BigDecimal startOrderNotaxPrice;

    /** @see ApiCompSouItemVO#getStartOrderTaxPrice */
    @ApiModelProperty("起拍价(原币含税)(仅用于竞价-comp)")
    private BigDecimal startOrderTaxPrice;

    /** @see ApiCompSouItemVO#getStartStandardNotaxPrice */
    @ApiModelProperty("起拍价(本币未税)(仅用于竞价-comp)")
    private BigDecimal startStandardNotaxPrice;

    /** @see ApiCompSouItemVO#getStartStandardTaxPrice */
    @ApiModelProperty("起拍价(本币未税)(仅用于竞价-comp)")
    private BigDecimal startStandardTaxPrice;

    /** @see ApiCompSouItemVO#getStartStandardGroupNotaxPrice */
    @ApiModelProperty("起拍价(本币未税-组合)(仅用于竞价-comp)")
    private BigDecimal startStandardGroupNotaxPrice;

    /** @see ApiCompSouItemVO#getStartStandardGroupTaxPrice */
    @ApiModelProperty("起拍价(本币含税-组合)(仅用于竞价-comp)")
    private BigDecimal startStandardGroupTaxPrice;

    /** @see ApiCompSouItemVO#getRowType */
    @ApiModelProperty("行类型(仅用于竞价-comp)")
    private String rowType;

    /** @see ApiCompSouItemVO#getPaymentList */
    @ApiModelProperty("付款条款(仅用于竞价-comp)")
    private List<CompSouItemPayment> paymentList;

}
