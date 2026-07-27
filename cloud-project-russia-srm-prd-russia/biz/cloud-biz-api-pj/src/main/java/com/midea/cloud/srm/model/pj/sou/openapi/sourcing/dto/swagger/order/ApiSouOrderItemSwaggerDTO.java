package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.order;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.order.ApiBidSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.order.ApiBrgSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.order.ApiCompSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.order.ApiInqSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class ApiSouOrderItemSwaggerDTO extends ApiSouOrderItemDTO {

    /** @see ApiInqSouOrderItemDTO#getFormulaAttrValues */
    @ApiModelProperty("供应商填写的公式报价信息(仅用于简易询价-inq)")
    protected String formulaAttrValues;

    /** @see ApiInqSouOrderItemDTO#getOrderItemFiles */
    @ApiModelProperty("附件(仅用于简易询价-inq)")
    private List<SceneFile> orderItemFiles;

    /**
     * @see ApiInqSouOrderItemDTO#getPaymentList
     * @see ApiBidSouOrderItemDTO#getPaymentList
     * @see ApiBrgSouOrderItemDTO#getPaymentList
     */
    @ApiModelProperty("账期(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private List<InqSouOrderItemPayment> paymentList;

    /**
     * @see ApiBidSouOrderItemDTO#getTransportType
     * @see ApiBrgSouOrderItemDTO#getTransportType
     */
    @ApiModelProperty("运输方式(仅用于招投标-bid/项目式询价-brg)[字典值: TRANSF_TYPE]")
    private String transportType;

    /**
     * @see ApiBidSouOrderItemDTO#getMqo
     * @see ApiBrgSouOrderItemDTO#getMqo
     */
    @ApiModelProperty("最小订单量(仅用于招投标-bid/项目式询价-brg)")
    private String mqo;

    /**
     * @see ApiBidSouOrderItemDTO#getLeadTime
     * @see ApiBrgSouOrderItemDTO#getLeadTime
     */
    @ApiModelProperty("供货周期(仅用于招投标-bid/项目式询价-brg)")
    private String leadTime;

    /**
     * @see ApiBidSouOrderItemDTO#getWarrantyPeriod
     * @see ApiBrgSouOrderItemDTO#getWarrantyPeriod
     */
    @ApiModelProperty("保修期(仅用于招投标-bid/项目式询价-brg)")
    private Integer warrantyPeriod;

    /**
     * @see ApiBidSouOrderItemDTO#getDeliverDate
     * @see ApiBrgSouOrderItemDTO#getDeliverDate
     */
    @ApiModelProperty("承诺交货期(仅用于招投标-bid/项目式询价-brg)")
    private Date deliverDate;

    /**
     * @see ApiBidSouOrderItemDTO#getFormulaResult
     * @see ApiBrgSouOrderItemDTO#getFormulaResult
     * @see ApiCompSouOrderItemDTO#getFormulaResult
     */
    @ApiModelProperty("供应商填写的公式报价json(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String formulaResult;

    /** @see ApiCompSouOrderItemDTO#getFormulaResult */
    @ApiModelProperty("提交人ID(仅用于竞价-comp)")
    private Long submitById;

    /** @see ApiCompSouOrderItemDTO#getFormulaResult */
    @ApiModelProperty("提交人账号(仅用于竞价-comp)")
    private String submitBy;

    /** @see ApiCompSouOrderItemDTO#getFormulaResult */
    @ApiModelProperty("提交人IP(仅用于竞价-comp)")
    private String submitByIp;

    /** @see ApiCompSouOrderItemDTO#getFormulaResult */
    @ApiModelProperty("提交人昵称(仅用于竞价-comp)")
    private String submitFullName;

    /** @see ApiCompSouOrderItemDTO#getFormulaResult */
    @ApiModelProperty("提交时间(仅用于竞价-comp)")
    private Date submitTime;

}
