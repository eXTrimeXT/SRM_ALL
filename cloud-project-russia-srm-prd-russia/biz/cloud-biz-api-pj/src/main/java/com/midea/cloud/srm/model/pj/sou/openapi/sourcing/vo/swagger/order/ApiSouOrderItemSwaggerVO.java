package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.order;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.order.ApiBidSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.order.ApiBrgSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.order.ApiInqSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 报价明细 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "报价明细")
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderItemSwaggerVO extends ApiSouOrderItemVO {

    /** @see ApiInqSouOrderItemVO#getLadderType */
    @ApiModelProperty("阶梯价类型(standard-标准阶梯价、sum-累计阶梯价)(仅用于简易询价-inq)")
    private String ladderType;

    /** @see ApiInqSouOrderItemVO#getIsFormula */
    @ApiModelProperty("是否公式报价(仅用于简易询价-inq)")
    private Enable isFormula;

    /**
     * @see ApiInqSouOrderItemVO#getMaterialFormulaRelateId
     * @see ApiBidSouOrderItemVO#getMaterialFormulaRelateId
     * @see ApiBrgSouOrderItemVO#getMaterialFormulaRelateId
     */
    @ApiModelProperty("物料价格公式关联ID(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private Long materialFormulaRelateId;

    /**
     * @see ApiInqSouOrderItemVO#getFormulaId
     * @see ApiBidSouOrderItemVO#getFormulaId
     * @see ApiBrgSouOrderItemVO#getFormulaId
     */
    @ApiModelProperty("公式ID(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private Long formulaId;

    /** @see ApiInqSouOrderItemVO#getItemType */
    @ApiModelProperty("行类型(仅用于简易询价-inq)[字典值: DMAND_LINE_TYPE]")
    private String itemType;

    /**
     * @see ApiInqSouOrderItemVO#getFormulaName
     * @see ApiBidSouOrderItemVO#getFormulaName
     * @see ApiBrgSouOrderItemVO#getFormulaName
     */
    @ApiModelProperty("公式名称(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private String formulaName;

    /**
     * @see ApiInqSouOrderItemVO#getFormulaValue
     * @see ApiBidSouOrderItemVO#getFormulaValue
     * @see ApiBrgSouOrderItemVO#getFormulaValue
     */
    @ApiModelProperty("公式值(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private String formulaValue;

    /** @see ApiInqSouOrderItemVO#getNotaxTargetPrice */
    @ApiModelProperty("未税目标价(仅用于简易询价-inq)")
    private BigDecimal notaxTargetPrice;

    /** @see ApiInqSouOrderItemVO#getFormulaAttrValues */
    @ApiModelProperty("供应商填写的公式报价信息(仅用于简易询价-inq)")
    private String formulaAttrValues;

    /**
     * @see ApiInqSouOrderItemVO#getItemFiles
     * @see ApiBidSouOrderItemVO#getItemFiles
     * @see ApiBrgSouOrderItemVO#getItemFiles
     */
    @ApiModelProperty("物料需求附件(仅用于简易询价-inq/招投标-bid)")
    private List<SceneFile> itemFiles;

    /**
     * @see ApiInqSouOrderItemVO#getOrderItemFiles
     */
    @ApiModelProperty("报价附件(仅用于简易询价-inq)")
    private List<SceneFile> orderItemFiles;

    /**
     * @see ApiInqSouOrderItemVO#getPaymentList
     * @see ApiBidSouOrderItemVO#getPaymentList
     * @see ApiBrgSouOrderItemVO#getPaymentList
     */
    @ApiModelProperty("账期信息(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private List<InqSouOrderItemPayment> paymentList;

    /**
     * @see ApiBidSouOrderItemVO#getOrderType
     * @see ApiBrgSouOrderItemVO#getOrderType
     */
    @ApiModelProperty("报价方式(冗余字段)(仅用于招投标-bid/项目式询价-brg)")
    private SouOrderTypeEnum orderType;

    /**
     * @see ApiBidSouOrderItemVO#getTargetPrice
     * @see ApiBrgSouOrderItemVO#getTargetPrice
     */
    @ApiModelProperty("拦标价(仅用于招投标-bid/项目式询价-brg)")
    private BigDecimal targetPrice;

    /**
     * @see ApiBidSouOrderItemVO#getDeliveryPlace
     * @see ApiBrgSouOrderItemVO#getDeliveryPlace
     */
    @ApiModelProperty("交货地点(仅用于招投标-bid/项目式询价-brg)")
    private String deliveryPlace;

    /**
     * @see ApiBidSouOrderItemVO#getPriceType
     * @see ApiBrgSouOrderItemVO#getPriceType
     */
    @ApiModelProperty("价格类型(仅用于招投标-bid/项目式询价-brg)[字典值: PRICE_TYPE]")
    private String priceType;

    /**
     * @see ApiBidSouOrderItemVO#getPurchaseType
     * @see ApiBrgSouOrderItemVO#getPurchaseType
     */
    @ApiModelProperty("采购类型(仅用于招投标-bid/项目式询价-brg)")
    private String purchaseType;

    /**
     * @see ApiBidSouOrderItemVO#getTradeTerm
     * @see ApiBrgSouOrderItemVO#getTradeTerm
     */
    @ApiModelProperty("贸易条款(仅用于招投标-bid/项目式询价-brg)[字典值: trade_clause]")
    private String tradeTerm;

    /**
     * @see ApiBidSouOrderItemVO#getTransportType
     * @see ApiBrgSouOrderItemVO#getTransportType
     */
    @ApiModelProperty("运输方式(仅用于招投标-bid/项目式询价-brg)[字典值: TRANSF_TYPE]")
    private String transportType;

    /**
     * @see ApiBidSouOrderItemVO#getWarrantyPeriod
     * @see ApiBrgSouOrderItemVO#getWarrantyPeriod
     */
    @ApiModelProperty("保修期(月)(仅用于招投标-bid/项目式询价-brg)")
    private Integer warrantyPeriod;

    /**
     * @see ApiBidSouOrderItemVO#getMqo
     * @see ApiBrgSouOrderItemVO#getMqo
     */
    @ApiModelProperty("最小订单量(仅用于招投标-bid/项目式询价-brg)")
    private String mqo;

    /**
     * @see ApiBidSouOrderItemVO#getLeadTime
     * @see ApiBrgSouOrderItemVO#getLeadTime
     */
    @ApiModelProperty("供货周期(仅用于招投标-bid/项目式询价-brg)")
    private String leadTime;

    /**
     * @see ApiBidSouOrderItemVO#getDeliverDate
     * @see ApiBrgSouOrderItemVO#getDeliverDate
     */
    @ApiModelProperty("承诺交货期(仅用于招投标-bid/项目式询价-brg)")
    private Date deliverDate;

    /**
     * @see ApiBidSouOrderItemVO#getFormulaResult
     * @see ApiBrgSouOrderItemVO#getFormulaResult
     */
    @ApiModelProperty("供应商填写的公式报价json(仅用于招投标-bid/项目式询价-brg)")
    private String formulaResult;

}
