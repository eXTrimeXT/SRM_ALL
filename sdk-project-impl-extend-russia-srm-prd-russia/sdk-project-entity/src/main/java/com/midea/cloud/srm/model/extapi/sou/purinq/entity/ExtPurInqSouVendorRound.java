package com.midea.cloud.srm.model.extapi.sou.purinq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
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
@TableName("scc_npm_sou_purinq_vendorround")
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouVendorRound extends BaseEntity<ExtPurInqSouVendorRound> {

    @TableId("INQ_SOU_VENDOR_ROUND_ID")
    @ApiModelProperty("ID")
    private Long inqSouVendorRoundId;

    @TableField("PROJECT_ID")
    @ApiModelProperty("询价单ID")
    private Long projectId;

    @TableField("ROUND")
    @ApiModelProperty("轮次")
    private Integer round;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("CAN_ORDER")
    @ApiModelProperty("供应商在指定轮次是否可报价")
    private Enable canOrder;

}
