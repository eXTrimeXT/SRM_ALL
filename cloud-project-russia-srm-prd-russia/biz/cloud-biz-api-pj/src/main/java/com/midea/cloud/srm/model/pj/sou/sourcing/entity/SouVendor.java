package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSignUpStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.quotationSatausStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 寻源核心 - 邀请供应商
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_vendor")
@ApiModel("寻源供应商")
public class SouVendor extends BaseEntity<SouVendor> {

    @ApiModelProperty("ID")
    @TableId("SOU_VENDOR_ID")
    private Long souVendorId;

    /**
     * @see SouProject#getProjectId
     */
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


    @ApiModelProperty("报价状态")
    @TableField("quotation_sataus")
    private quotationSatausStatusEnum quotationSataus;

    @ApiModelProperty("报名时间")
    @TableField("SIGN_UP_TIME")
    private Date signUpTime;

    @ApiModelProperty("报名驳回原因")
    @TableField("SIGN_UP_REJECT_REASON")
    private String signUpRejectReason;

    @ApiModelProperty("联系人名称")
    @TableField(value = "LINKMAN_NAME", updateStrategy = FieldStrategy.IGNORED)
    private String linkmanName;

    @ApiModelProperty("电话")
    @TableField(value = "PHONE", updateStrategy = FieldStrategy.IGNORED)
    private String phone;

    @ApiModelProperty("邮箱")
    @TableField(value = "EMAIL", updateStrategy = FieldStrategy.IGNORED)
    private String email;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

    @ApiModelProperty("保证金缴纳时间")
    @TableField("DEPOSIT_PAY_TIME")
    private Date depositPayTime;

}
