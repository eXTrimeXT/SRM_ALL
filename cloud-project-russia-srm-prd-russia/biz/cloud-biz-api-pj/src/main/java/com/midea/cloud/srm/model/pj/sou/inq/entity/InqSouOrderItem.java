package com.midea.cloud.srm.model.pj.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @author huangbf3
 * 寻源-简易询价-供应商报价行信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("scc_sou_inq_order_item")
@ApiModel(description = "寻源-简易询价-供应商报价行信息ID")
public class InqSouOrderItem extends ExtInqSouOrderItem {

    /** @see SouOrderItem#getOrderItemId */
    @TableId("ORDER_ITEM_ID")
    @ApiModelProperty("寻源核心-供应商报价行ID")
    private Long orderItemId;

    /** @see SouOrder#getOrderId */
    @TableField("ORDER_ID")
    @ApiModelProperty("报价单ID")
    private Long orderId;

    /** @see InqSouItem#getIsFormula */
    @TableField("IS_FORMULA")
    @ApiModelProperty("是否公式报价(冗余字段)")
    private Enable isFormula;

    /** @see InqSouItem#getFormulaId */
    @TableField("FORMULA_ID")
    @ApiModelProperty("公式ID(冗余字段)")
    private Long formulaId;

    /** @see InqSouItem#getFormulaName */
    @TableField("FORMULA_NAME")
    @ApiModelProperty("公式名称(冗余字段)")
    private String formulaName;

    /** @see InqSouItem#getFormulaValue */
    @TableField("FORMULA_VALUE")
    @ApiModelProperty("公式值(冗余字段)")
    private String formulaValue;

    @TableField("FORMULA_ATTR_VALUES")
    @ApiModelProperty("供应商填写的公式报价信息")
    private String formulaAttrValues;

}
