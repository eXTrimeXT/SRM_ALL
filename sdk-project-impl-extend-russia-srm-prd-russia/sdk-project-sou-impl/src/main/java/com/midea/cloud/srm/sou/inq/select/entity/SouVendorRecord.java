package com.midea.cloud.srm.sou.inq.select.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.entity.typehandler.SouVendorTypeHandler;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouVendorAddStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ex_liuxy46
 */
@ApiModel(description = "寻源核心 - 追加供应商记录表")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "scc_sou_vendor_record", autoResultMap = true)
public class SouVendorRecord extends BaseEntity<SouVendorRecord> {

    @TableId("record_id")
    @ApiModelProperty("ID")
    private Long recordId;

    /** @see SouProject#getProjectId */
    @TableField("project_id")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @TableField("vendor_id")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("linkman_name")
    @ApiModelProperty("联系人名称")
    private String linkmanName;

    @TableField("phone")
    @ApiModelProperty("电话")
    private String phone;

    @TableField("email")
    @ApiModelProperty("邮箱")
    private String email;

    @TableField("add_status")
    @ApiModelProperty("追加状态")
    private SouVendorAddStatusEnum addStatus;

    @TableField("execute_round")
    @ApiModelProperty("执行的轮次")
    private Integer executeRound;

    @TableField("reason")
    @ApiModelProperty("原因")
    private String reason;

    @TableField(value = "vendor_info", typeHandler = SouVendorTypeHandler.class)
    @ApiModelProperty("供应商信息")
    private SouVendor vendorInfo;
}
