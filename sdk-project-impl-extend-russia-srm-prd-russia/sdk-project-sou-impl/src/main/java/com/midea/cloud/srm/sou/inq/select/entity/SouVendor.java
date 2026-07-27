package com.midea.cloud.srm.sou.inq.select.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSignUpStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author ex_liuxy46
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_vendor")
@ApiModel("寻源供应商")
public class SouVendor extends BaseEntity<SouVendor> {

    @ApiModelProperty("ID")
    @TableId("SOU_VENDOR_ID")
    private Long souVendorId;
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;
    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;
    @ApiModelProperty("供应商编码")
    @TableField("VENDOR_CODE")
    private String vendorCode;
    @ApiModelProperty("供应商名称")
    @TableField("VENDOR_NAME")
    private String vendorName;
    @ApiModelProperty("加入轮次")
    @TableField("JOIN_ROUND")
    private Integer joinRound;
    @ApiModelProperty("报名状态")
    @TableField("SIGN_UP_STATUS")
    private SouSignUpStatusEnum signUpStatus;
    @ApiModelProperty("报名时间")
    @TableField("SIGN_UP_TIME")
    private Date signUpTime;
    @ApiModelProperty("报名驳回原因")
    @TableField("SIGN_UP_REJECT_REASON")
    private String signUpRejectReason;
    @ApiModelProperty("联系人名称")
    @TableField("LINKMAN_NAME")
    private String linkmanName;
    @ApiModelProperty("电话")
    @TableField("PHONE")
    private String phone;
    @ApiModelProperty("邮箱")
    @TableField("EMAIL")
    private String email;
    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;
    @TableField("reason")
    @ApiModelProperty("原因")
    private String reason;
}
