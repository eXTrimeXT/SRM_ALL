package com.midea.cloud.srm.model.extapi.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 长城 - 询比价 - 供应商轮次信息
 * PS: 用来记录供应商在哪个轮次是可报价的
 * @author huangbf3
 */
@Data
@TableName("scc_npm_sou_inq_vendor_round")
@EqualsAndHashCode(callSuper = true)
public class ExtPjInqSouVendorRound extends BaseEntity<ExtPjInqSouVendorRound> {

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
