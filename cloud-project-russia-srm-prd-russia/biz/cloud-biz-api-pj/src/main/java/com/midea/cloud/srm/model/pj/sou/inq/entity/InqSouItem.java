package com.midea.cloud.srm.model.pj.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.formula.entity.MaterialFormulaRelate;
import com.midea.cloud.srm.model.base.formula.entity.PricingFormulaHeader;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <pre>
 *  询价-询价信息行表 模型
 * </pre>
 *
 * @author zhongbh
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人: zhangwk12@meicloud.com
 *  修改日期: 2020-03-16 14:55:25
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_inq_item")
@ApiModel(description = "寻源-简易询价物料需求行")
public class InqSouItem extends ExtInqSouItem {

    private static final long serialVersionUID = 1L;

    /** @see SouItem#getSouItemId */
    @TableId("SOU_ITEM_ID")
    @ApiModelProperty("寻源核心-物料需求行ID")
    private Long souItemId;

    /** @see SouItem#getProjectId */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @TableField("LADDER_TYPE")
    @ApiModelProperty("阶梯价类型(standard-标准阶梯价、sum-累计阶梯价)")
    private String ladderType;

    @TableField("IS_FORMULA")
    @ApiModelProperty("是否公式报价(Y/N)")
    private Enable isFormula;

    /** @see MaterialFormulaRelate#getRelateId */
    @TableField("MATERIAL_FORMULA_RELATE_ID")
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;

    /**
     * @see MaterialFormulaRelate#getFormulaId
     * @see PricingFormulaHeader#getPricingFormulaHeaderId
     */
    @TableField("FORMULA_ID")
    @ApiModelProperty("公式ID")
    private Long formulaId;

    @TableField("ITEM_TYPE")
    @ApiModelProperty("行类型[字典值: DMAND_LINE_TYPE]")
    private String itemType;

    /**
     * @see MaterialFormulaRelate#getFormulaName
     * @see PricingFormulaHeader#getPricingFormulaName
     */
    @TableField("FORMULA_NAME")
    @ApiModelProperty("公式名称")
    private String formulaName;

    /**
     * @see MaterialFormulaRelate#getFormulaValue
     * @see PricingFormulaHeader#getPricingFormulaValue
     */
    @TableField("FORMULA_VALUE")
    @ApiModelProperty("公式值")
    private String formulaValue;

    @TableField("NOTAX_TARGET_PRICE")
    @ApiModelProperty("未税目标价（报价阶段设定目标价时才使用）")
    private BigDecimal notaxTargetPrice;

}
