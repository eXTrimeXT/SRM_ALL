package com.midea.cloud.srm.model.extapi.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "寻源-简易询价物料需求行")
@TableName("scc_sou_inq_item")
public class PjInqSouItem extends BaseEntity<PjInqSouItem> {

    @TableId("SOU_ITEM_ID")
    @ApiModelProperty("寻源核心-物料需求行ID")
    private Long souItemId;
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;
    @TableField("LADDER_TYPE")
    @ApiModelProperty("阶梯价类型(standard-标准阶梯价、sum-累计阶梯价)")
    private String ladderType;
    @TableField("IS_FORMULA")
    @ApiModelProperty("是否公式报价(Y/N)")
    private Enable isFormula;
    @TableField("MATERIAL_FORMULA_RELATE_ID")
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;
    @TableField("FORMULA_ID")
    @ApiModelProperty("公式ID")
    private Long formulaId;
    @TableField("ITEM_TYPE")
    @ApiModelProperty("行类型[字典值: DMAND_LINE_TYPE]")
    private String itemType;
    @TableField("FORMULA_NAME")
    @ApiModelProperty("公式名称")
    private String formulaName;
    @TableField("FORMULA_VALUE")
    @ApiModelProperty("公式值")
    private String formulaValue;
    @TableField("NOTAX_TARGET_PRICE")
    @ApiModelProperty("未税目标价（报价阶段设定目标价时才使用）")
    private BigDecimal notaxTargetPrice;

    @TableField("EXT_MATERIAL_MODEL")
    @ApiModelProperty("物料规格型号")
    private String extMaterialModel;

    @TableField("EXT_BRAND")
    @ApiModelProperty("品牌")
    private String extBrand;

    @TableField("EXT_AREA_ID")
    @ApiModelProperty("区域ID")
    private String extAreaId;

    @TableField("EXT_AREA_CODE")
    @ApiModelProperty("区域编码")
    private String extAreaCode;

    @TableField("EXT_AREA_NAME")
    @ApiModelProperty("区域名称")
    private String extAreaName;

    @TableField("EXT_SOURCE_FROM_LINE_IDS")
    @ApiModelProperty("来源单据明细ID集合(因为原表用Long类型，无法支持)")
    private String extSourceFromLineIds;

    @TableField("HAS_CLOSE")
    @ApiModelProperty("是否关闭")
    private Enable hasClose;

}