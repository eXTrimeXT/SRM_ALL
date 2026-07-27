package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源openAPI - 物料需求信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@Data
@ApiModel("物料需求信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouItemEditDTO extends BaseObjectX {

    /**
     * @see SouItem#getSouItemId
     */
    @ApiModelProperty("寻源核心 - 物料需求行ID")
    private Long souItemId;

    /**
     * @see SouItem#getOrgOuCode
     */
    @ApiModelProperty(value = "业务实体编码", required = true)
    private String orgOuCode;

    /**
     * @see SouItem#getOrgInvCode
     */
    @ApiModelProperty(value = "库存组织编码", required = true)
    private String orgInvCode;

    /**
     * @see SouItem#getNoCodeItem
     */
    @ApiModelProperty("是否无料号物料(Y/N)")
    private Enable noCodeItem;

    /**
     * @see SouItem#getItemId
     */
    @ApiModelProperty("物料ID")
    private Long itemId;

    /**
     * @see SouItem#getItemDesc
     */
    @ApiModelProperty("物料名称(无料号时才需要填写，长度限制255)")
    private String itemDesc;

    /**
     * @see SouItem#getUnit
     */
    @ApiModelProperty("单位(无料号时才需要填写，长度限制30)")
    private String unit;

    /**
     * @see SouItem#getCategoryId
     */
    @ApiModelProperty("品类ID(无料号时才需要填写)")
    private Long categoryId;

    /**
     * @see SouItem#getCategoryId
     */
    @ApiModelProperty("品类编码")
    private String categoryCode;

    /**
     * @see SouItem#getCategoryId
     */
    @ApiModelProperty("品类名称")
    private String categoryName;

    /**
     * @see SouItem#getRequireQuantity
     */
    @ApiModelProperty("需求数量")
    private BigDecimal requireQuantity;

    /**
     * @see SouItem#getRequireDate
     */
    @ApiModelProperty("需求时间")
    private Date requireDate;

    /**
     * @see SouItem#getBuyAmount
     */
    @ApiModelProperty("预计采购金额")
    private BigDecimal buyAmount;

    /**
     * @see SouItem#getPriceStartTime
     */
    @ApiModelProperty(value = "价格有效期从（原定价开始时间）", required = true)
    private Date priceStartTime;

    /**
     * @see SouItem#getPriceEndTime
     */
    @ApiModelProperty(value = "价格有效期到（原定价结束时间）", required = true)
    private Date priceEndTime;

    /**
     * @see SouItem#getSourceFromId
     */
    @ApiModelProperty("来源单据ID")
    private Long sourceFromId;

    /**
     * @see SouItem#getSourceFromNo
     */
    @ApiModelProperty("来源单据号")
    private String sourceFromNo;

    /**
     * @see SouItem#getSourceFromLineId
     */
    @ApiModelProperty("来源单据行ID")
    private Long sourceFromLineId;

    /**
     * @see SouItem#getSourceFromLineNo
     */
    @ApiModelProperty("来源单据行号")
    private String sourceFromLineNo;

    /**
     * @see SouItem#getRemark
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * @see SouItem#getIsLadder
     */
    @ApiModelProperty("是否阶梯报价")
    private Enable isLadder;

    /**
     * @see SouItem#getSourceFromType
     */
    @ApiModelProperty("来源类型(冗余字段) [字典：SOU_SOURCE_FROM_TYPE]")
    private String sourceFromType;

    /**
     * @see SouItem#getSortIndex
     */
    @ApiModelProperty(value = "排序", required = true)
    private Integer sortIndex;

    /**
     * 新增部分开始
     */
    @ApiModelProperty("物料编码")
    private String itemCode;

    @ApiModelProperty("物料组合(用于组合报价场景，长度限制50)")
    private String itemGroup;

    @ApiModelProperty("所属单位")
    private String affiliatedUnit;

    @ApiModelProperty("投标保证金")
    private BigDecimal bidSecurity;

    @ApiModelProperty("履约保证金")
    private BigDecimal performanceBond;

    @ApiModelProperty("预付款")
    private BigDecimal advanceCharge;

    @ApiModelProperty("月约产量")
    private BigDecimal monthlyProduction;

    @ApiModelProperty("计量单位")
    private String meteringUnit;

    @ApiModelProperty("起拍价格（元）")
    private BigDecimal startPrice;

    @ApiModelProperty("梯次价格（元）")
    private BigDecimal echelonPrice;
    //---------- 新增部分结束 ---------------
}
