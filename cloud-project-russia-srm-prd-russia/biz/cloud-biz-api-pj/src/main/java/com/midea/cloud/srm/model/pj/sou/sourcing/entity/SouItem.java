package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源核心 - 物料需求行
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@ApiModel(description = "寻源核心 - 物料需求行")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_item")
public class SouItem extends BaseEntity<SouItem> {

    @ApiModelProperty("寻源核心 - 物料需求行ID")
    @TableId("SOU_ITEM_ID")
    private Long souItemId;

    /**
     * @see SouProject#getProjectId
     */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /**  组织信息 */
    @ApiModelProperty("业务实体ID")
    @TableField("ORG_OU_ID")
    private Long orgOuId;

    @ApiModelProperty("业务实体编码")
    @TableField("ORG_OU_CODE")
    private String orgOuCode;

    @ApiModelProperty("业务实体名称")
    @TableField("ORG_OU_NAME")
    private String orgOuName;

    @ApiModelProperty("库存组织ID")
    @TableField("ORG_INV_ID")
    private Long orgInvId;

    @ApiModelProperty("库存组织编码")
    @TableField("ORG_INV_CODE")
    private String orgInvCode;

    @ApiModelProperty("库存组织名称")
    @TableField("ORG_INV_NAME")
    private String orgInvName;

    /** 物料信息 */
    @ApiModelProperty("物料组合(用于组合报价场景)")
    @TableField("ITEM_GROUP")
    private String itemGroup;

    @ApiModelProperty("是否无料号物料(Y/N)")
    @TableField("NO_CODE_ITEM")
    private Enable noCodeItem;

    @ApiModelProperty("物料ID")
    @TableField("ITEM_ID")
    private Long itemId;

    @ApiModelProperty("物料编码")
    @TableField("ITEM_CODE")
    private String itemCode;

    @ApiModelProperty("物料名称")
    @TableField("ITEM_DESC")
    private String itemDesc;

    /**
     * @see MaterialItem#getUnit
     */
    @ApiModelProperty("单位")
    @TableField("UNIT")
    private String unit;

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

    @ApiModelProperty("品类ID")
    @TableField("CATEGORY_ID")
    private Long categoryId;

    @ApiModelProperty("品类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;

    @ApiModelProperty("品类名称")
    @TableField("CATEGORY_NAME")
    private String categoryName;

    /** 采购商要求 */
    @ApiModelProperty("需求数量")
    @TableField("REQUIRE_QUANTITY")
    private BigDecimal requireQuantity;

    @ApiModelProperty("需求时间")
    @TableField("REQUIRE_DATE")
    private Date requireDate;

    @ApiModelProperty("是否阶梯报价")
    @TableField("IS_LADDER")
    private Enable isLadder;

    @ApiModelProperty("预计采购金额")
    @TableField("BUY_AMOUNT")
    private BigDecimal buyAmount;

    @ApiModelProperty("价格有效期从（原定价开始时间）")
    @TableField("PRICE_START_TIME")
    private Date priceStartTime;

    @ApiModelProperty("价格有效期到（原定价结束时间）")
    @TableField("PRICE_END_TIME")
    private Date priceEndTime;

    // --------------------------------------------------------- 关联上游单据 --------------------------------------------------------
    /**
     * @see SouProject#getSourceFromType
     */
    @ApiModelProperty("来源类型(冗余字段) [字典：SOU_SOURCE_FROM_TYPE]")
    @TableField("SOURCE_FROM_TYPE")
    private String sourceFromType;

    /**
     * @see SouProject#getSourceFromId
     */
    @ApiModelProperty("来源单据ID(冗余字段)")
    @TableField("SOURCE_FROM_ID")
    private Long sourceFromId;

    /**
     * @see SouProject#getSourceFromNo
     */
    @ApiModelProperty("来源单据号(冗余字段)")
    @TableField("SOURCE_FROM_NO")
    private String sourceFromNo;

    @ApiModelProperty("来源单据行ID")
    @TableField("SOURCE_FROM_LINE_ID")
    private Long sourceFromLineId;

    @ApiModelProperty("来源单据行号")
    @TableField("SOURCE_FROM_LINE_NO")
    private String sourceFromLineNo;

    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
