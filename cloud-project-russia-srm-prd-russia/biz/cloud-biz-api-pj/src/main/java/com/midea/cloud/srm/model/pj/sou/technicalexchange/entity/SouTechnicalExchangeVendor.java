package com.midea.cloud.srm.model.pj.sou.technicalexchange.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <pre>
 *  技术交流-邀请供应商 模型
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: Apr 28, 2022 10:55:25 AM
 *  修改内容:
 * </pre>
 */

@ApiModel(description = "技术交流-邀请供应商")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_technical_exchange_vendor")
public class SouTechnicalExchangeVendor extends BaseEntity {
    @ApiModelProperty("")
    private static final long serialVersionUID = 197091L;
    /**
     * 技术交流-邀请供应商数据ID
     */
    @ApiModelProperty("技术交流-邀请供应商数据ID")
    @TableId("TECHNICAL_EXCHANGE_VENDOR_ID")
    private Long technicalExchangeVendorId;
    /**
     * 技术交流单据ID
     */
    @ApiModelProperty("技术交流单据ID")
    @TableField("TECHNICAL_EXCHANGE_ID")
    private Long technicalExchangeId;
    /**
     * 供应商id
     */
    @ApiModelProperty("供应商id")
    @TableField("VENDOR_ID")
    private Long vendorId;
    /**
     * 供应商编码
     */
    @ApiModelProperty("供应商编码")
    @TableField("VENDOR_CODE")
    private String vendorCode;
    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    @TableField("VENDOR_NAME")
    private String vendorName;
    /**
     * 联系人
     */
    @ApiModelProperty("联系人")
    @TableField("LINK_MAN")
    private String linkMan;
    /**
     * 联系电话
     */
    @ApiModelProperty("联系电话")
    @TableField("PHONE")
    private String phone;
    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    @TableField("EMAIL")
    private String email;
    /**
     * 反馈状态
     */
    @ApiModelProperty("反馈状态")
    @TableField("FEEDBACK_STATUS")
    private String feedbackStatus;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;
}