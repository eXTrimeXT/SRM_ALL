package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 寻源需求单报名表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
@ApiModel(description = "寻源需求单报名表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_sou_req_apply")
public class SouReqApply extends BaseEntity {
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId
    private Long applyId;

    /**
     * 寻源单ID
     */
    @ApiModelProperty("寻源单ID")
    private Long reqHeadId;

    /**
     * 供应商id
     */
    @ApiModelProperty("供应商id")
    private Long vendorId;

    /**
     * 供应商编码/企业标识
     */
    @ApiModelProperty("供应商编码/企业标识")
    private String vendorCode;

    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /**
     * 是否内部供应商（Y是，N否）
     */
    @ApiModelProperty("是否内部供应商（Y是，N否）")
    private String isInternalVendor;

    /**
     * 是否被推荐（Y是，N否）
     */
    @ApiModelProperty("是否被推荐（Y是，N否）")
    private String isRecomm;
    /**
     * 报名状态
     */
    @ApiModelProperty("报名状态")
    private String applyStatus;

    /**
     * 报名联系人
     */
    @ApiModelProperty("报名联系人")
    private String applyContactName;

    /**
     * 报名联系手机号
     */
    @ApiModelProperty("报名联系手机号")
    private String applyPhone;

    /**
     * 报名联系邮箱
     */
    @ApiModelProperty("报名联系邮箱")
    private String applyEmail;

    /**
     * 意向金缴纳凭证附件ID
     */
    @ApiModelProperty("意向金缴纳凭证附件ID")
    private Long depositFileId;

    /**
     * 意向金缴纳凭证附件名称
     */
    @ApiModelProperty("意向金缴纳凭证附件名称")
    private String depositFileName;

    /**
     * 意向金缴纳状态
     */
    @ApiModelProperty("意向金缴纳状态")
    private String depositStatus;

    /**
     * 意向金退款状态（值对应字典SOU_DEPOSIT_REFUND_STATUS）
     */
    @ApiModelProperty("意向金退款状态（值对应字典SOU_DEPOSIT_REFUND_STATUS）")
    private String depositRefundStatus;
    /**
     * 供应商缴纳账户
     */
    @ApiModelProperty("供应商缴纳账户")
    private String vendorBankAccount;

    /**
     * 缴纳户名
     */
    @ApiModelProperty("缴纳户名")
    private String vendorBankAccountName;

    /**
     * 缴纳银行
     */
    @ApiModelProperty("缴纳银行")
    private String vendorBankName;

    /**
     * 银行联行号
     */
    @ApiModelProperty("银行联行号")
    private String vendorBankNumber;

    /**
     * 是否代理（Y是，N否）
     */
    @ApiModelProperty("是否代理（Y是，N否）")
    private String isAgent;

    /**
     * 代理品牌
     */
    @ApiModelProperty("代理品牌")
    private String agentBrand;

    /**
     * 报名失败原因
     */
    @ApiModelProperty("报名失败原因")
    private String applyFailReason;
    /**
     * 报名处理方式
     */
    @ApiModelProperty("报名处理方式")
    private String applyHandleType;
    @ApiModelProperty("报名撤回原因")
    private String withdrawReason;
    /**
     * 报名处理原因
     */
    @ApiModelProperty("报名处理原因")
    private String applyHandleReason;
    @TableField(exist = false)
    private List<SceneFile> fileUploads;

    @ApiModelProperty("报名时间")
    private Date signupTime;

    @ApiModelProperty("退款时间")
    private Date refundPaymentDate;

    /**
     * 退款失败原因
     */
    @ApiModelProperty("退款失败原因")
    private String refundFailCause;

    @ApiModelProperty("付款时间")
    private Date transTime;
}
