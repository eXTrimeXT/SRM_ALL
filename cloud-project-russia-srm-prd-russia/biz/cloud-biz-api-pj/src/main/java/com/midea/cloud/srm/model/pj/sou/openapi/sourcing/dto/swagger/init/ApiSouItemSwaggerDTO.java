package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.init.ApiBidSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.init.ApiBrgSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init.ApiInqSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 物料需求信息 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouItemDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "物料需求信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouItemSwaggerDTO extends BaseObjectX {

    /** @see ApiSouItemDTO#getSouItemId */
    @ApiModelProperty("寻源核心 - 物料需求行ID")
    private Long souItemId;

    /** @see ApiSouItemDTO#getOrgOuCode */
    @ApiModelProperty(value = "业务实体编码", required = true)
    private String orgOuCode;

    /** @see ApiSouItemDTO#getOrgInvCode */
    @ApiModelProperty(value = "库存组织编码", required = true)
    private String orgInvCode;

    /** @see ApiSouItemDTO#getItemGroup */
    @ApiModelProperty("物料组合(用于组合报价场景，长度限制50)")
    private String itemGroup;

    /** @see ApiSouItemDTO#getNoCodeItem */
    @ApiModelProperty("是否无料号物料(Y/N)")
    private Enable noCodeItem;

    /** @see ApiSouItemDTO#getItemId */
    @ApiModelProperty("物料ID")
    private Long itemId;

    /** @see ApiSouItemDTO#getItemDesc */
    @ApiModelProperty("物料名称(无料号时才需要填写，长度限制255)")
    private String itemDesc;

    /** @see ApiSouItemDTO#getUnit */
    @ApiModelProperty("单位(无料号时才需要填写，长度限制30)")
    private String unit;

    /** @see ApiSouItemDTO#getCategoryId */
    @ApiModelProperty("品类ID(无料号时才需要填写)")
    private Long categoryId;

    /** @see ApiSouItemDTO#getCategoryId */
    @ApiModelProperty("品类编码")
    private String categoryCode;

    /** @see ApiSouItemDTO#getCategoryId */
    @ApiModelProperty("品类名称")
    private String categoryName;

    /** @see ApiSouItemDTO#getRequireQuantity */
    @ApiModelProperty("需求数量")
    private BigDecimal requireQuantity;

    /** @see ApiSouItemDTO#getRequireDate */
    @ApiModelProperty("需求时间")
    private Date requireDate;

    /** @see ApiSouItemDTO#getBuyAmount */
    @ApiModelProperty("预计采购金额")
    private BigDecimal buyAmount;

    /** @see ApiSouItemDTO#getPriceStartTime */
    @ApiModelProperty(value = "价格有效期从（原定价开始时间）", required = true)
    private Date priceStartTime;

    /** @see ApiSouItemDTO#getPriceEndTime */
    @ApiModelProperty(value = "价格有效期到（原定价结束时间）", required = true)
    private Date priceEndTime;

    /** @see ApiSouItemDTO#getSourceFromLineId */
    @ApiModelProperty("来源单据行ID")
    private Long sourceFromLineId;

    /** @see ApiSouItemDTO#getSourceFromLineNo */
    @ApiModelProperty("来源单据行号")
    private String sourceFromLineNo;

    /** @see ApiSouItemDTO#getRemark */
    @ApiModelProperty("备注")
    private String remark;

    /** @see ApiSouItemDTO#getIsLadder */
    @ApiModelProperty("是否阶梯报价")
    private Enable isLadder;

    /** @see ApiSouItemDTO#getSourceFromType */
    @ApiModelProperty("来源类型(冗余字段) [字典：SOU_SOURCE_FROM_TYPE]")
    private String sourceFromType;

    /** @see ApiSouItemDTO#getSortIndex */
    @ApiModelProperty(value = "排序", required = true)
    private Integer sortIndex;

    @ApiModelProperty("阶梯价模板信息")
    private List<SouItemLadder> ladderList;

    // ----------------------------------------------------- 寻源场景专用 -------------------------------------------------------
    /**
     * @see ApiInqSouItemDTO#getMaterialFormulaRelateId
     * @see ApiBidSouItemDTO#getMaterialFormulaRelateId
     * @see ApiBrgSouItemDTO#getMaterialFormulaRelateId
     * @see ApiCompSouItemDTO#getMaterialFormulaRelateId
     */
    @ApiModelProperty("物料价格公式关联ID(仅用于简易询价-inq/招投标-bid/项目式询价-brg/竞价-comp)")
    private Long materialFormulaRelateId;

    /**
     * @see ApiInqSouItemDTO#getFormulaId
     * @see ApiBidSouItemDTO#getFormulaId
     * @see ApiBrgSouItemDTO#getFormulaId
     * @see ApiCompSouItemDTO#getFormulaId
     */
    @ApiModelProperty("公式id(仅用于简易询价-inq/招投标-bid/项目式询价-brg/竞价-comp)")
    private Long formulaId;

    /**
     * @see ApiInqSouItemDTO#getFormulaName
     * @see ApiBidSouItemDTO#getFormulaName
     * @see ApiBrgSouItemDTO#getFormulaName
     * @see ApiCompSouItemDTO#getFormulaName
     */
    @ApiModelProperty("公式名称(仅用于简易询价-inq/招投标-bid/项目式询价-brg/竞价-comp)")
    private String formulaName;

    /**
     * @see ApiInqSouItemDTO#getFormulaValue
     * @see ApiBidSouItemDTO#getFormulaValue
     * @see ApiBrgSouItemDTO#getFormulaValue
     * @see ApiCompSouItemDTO#getFormulaValue
     */
    @ApiModelProperty("公式值(仅用于简易询价-inq/招投标-bid/项目式询价-brg/竞价-comp)")
    private String formulaValue;

    /**
     * @see ApiBidSouItemDTO#getTargetPrice
     * @see ApiBrgSouItemDTO#getTargetPrice
     */
    @ApiModelProperty("拦标价(仅用于招投标-bid/项目式询价-brg)")
    private BigDecimal targetPrice;

    /**
     * @see ApiBidSouItemDTO#getDeliveryPlace
     * @see ApiBrgSouItemDTO#getDeliveryPlace
     * @see ApiCompSouItemDTO#getDeliveryPlace
     */
    @ApiModelProperty("交货地点(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String deliveryPlace;

    /**
     * @see ApiBidSouItemDTO#getPriceType
     * @see ApiBrgSouItemDTO#getPriceType
     * @see ApiCompSouItemDTO#getPriceType
     */
    @ApiModelProperty("价格类型(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String priceType;

    /**
     * @see ApiBidSouItemDTO#getPurchaseType
     * @see ApiBrgSouItemDTO#getPurchaseType
     */
    @ApiModelProperty("采购类型(仅用于招投标-bid/项目式询价-brg)")
    private String purchaseType;

    /**
     * @see ApiBidSouItemDTO#getTradeTerm
     * @see ApiBrgSouItemDTO#getTradeTerm
     * @see ApiCompSouItemDTO#getTradeTerm
     */
    @ApiModelProperty("贸易条款(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String tradeTerm;

    /**
     * @see ApiBidSouItemDTO#getTransportType
     * @see ApiBrgSouItemDTO#getTransportType
     */
    @ApiModelProperty("运输方式(仅用于招投标-bid/项目式询价-brg)")
    private String transportType;

    /**
     * @see ApiBidSouItemDTO#getWarrantyPeriod
     * @see ApiBrgSouItemDTO#getWarrantyPeriod
     * @see ApiCompSouItemDTO#getWarrantyPeriod
     */
    @ApiModelProperty("保修期(月)(仅用于招投标-bid/项目式询价/竞价)")
    private Integer warrantyPeriod;

    /**
     * @see ApiInqSouItemDTO#getItemFiles
     * @see ApiBidSouItemDTO#getItemFiles
     * @see ApiBrgSouItemDTO#getItemFiles
     * @see ApiCompSouItemDTO#getItemFiles
     */
    @ApiModelProperty("项目需求信息-标准附件(仅用于简易询价/招投标-bid/项目式询价-brg/竞价-comp)")
    private List<SceneFile> itemFiles;

    /** @see ApiInqSouItemDTO#getLadderType */
    @ApiModelProperty("阶梯价类型(standard-标准阶梯价、sum-累计阶梯价)(仅用于简易询-inq)")
    private String ladderType;

    /** @see ApiInqSouItemDTO#getIsFormula */
    @ApiModelProperty("是否公式报价(仅用于简易询价-inq)")
    private Enable isFormula;

    /** @see ApiInqSouItemDTO#getItemType */
    @ApiModelProperty("行类型(仅用于简易询价-inq)[字典值: DMAND_LINE_TYPE]")
    private String itemType;

    /** @see ApiInqSouItemDTO#getNotaxTargetPrice */
    @ApiModelProperty("未税目标价(仅用于简易询价-inq)")
    private BigDecimal notaxTargetPrice;

    /** @see ApiCompSouItemDTO#getOrderCurrency */
    @ApiModelProperty("报价币种(仅用于竞价-comp)")
    private String orderCurrency;

    /** @see ApiCompSouItemDTO#getTaxKey */
    @ApiModelProperty("税率编码(仅用于竞价-comp)")
    private String taxKey;

    /** @see ApiCompSouItemDTO#getTaxRate */
    @ApiModelProperty("税率(仅用于竞价-comp)")
    private BigDecimal taxRate;

    /** @see ApiCompSouItemDTO#getStartOrderNotaxPrice */
    @ApiModelProperty("起拍价(原币未税)(仅用于竞价-comp)")
    private BigDecimal startOrderNotaxPrice;

    /** @see ApiCompSouItemDTO#getStartOrderTaxPrice */
    @ApiModelProperty("起拍价(原币含税)(仅用于竞价-comp)")
    private BigDecimal startOrderTaxPrice;

    /** @see ApiCompSouItemDTO#getRowType */
    @ApiModelProperty("行类型(仅用于竞价-comp)")
    private String rowType;

    /** @see ApiCompSouItemDTO#getPaymentList */
    @ApiModelProperty("付款条款(仅用于竞价-comp)")
    private List<CompSouItemPayment> paymentList;

}
