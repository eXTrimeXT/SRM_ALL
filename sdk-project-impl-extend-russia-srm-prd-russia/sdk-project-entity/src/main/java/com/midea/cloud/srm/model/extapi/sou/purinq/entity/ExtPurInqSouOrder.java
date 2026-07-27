package com.midea.cloud.srm.model.extapi.sou.purinq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("scc_npm_sou_purinq_order")
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouOrder extends BaseEntity<ExtPurInqSouOrder> {

    /** @see SouOrder#getOrderId */
    @ApiModelProperty("寻源核心-供应商报价头ID")
    @TableId("ORDER_ID")
    private Long orderId;

    /** @see SouOrder#getProjectId */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("报价人")
    @TableField("ORDER_BY_NICKNAME")
    private String orderByNickname;

    @ApiModelProperty("报价电话")
    @TableField("ORDER_PHONE")
    private String orderPhone;

    @ApiModelProperty("报价邮箱")
    @TableField("ORDER_EMAIL")
    private String orderEmail;

}
