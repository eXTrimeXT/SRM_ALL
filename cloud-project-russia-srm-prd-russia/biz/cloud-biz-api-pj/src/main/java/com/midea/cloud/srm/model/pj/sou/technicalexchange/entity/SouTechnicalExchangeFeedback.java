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
 *  技术交流-供应商反馈表 模型
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: Apr 28, 2022 11:02:08 AM
 *  修改内容:
 * </pre>
 */

@ApiModel(description = "技术交流-供应商反馈")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_technical_exchange_feedback")
public class SouTechnicalExchangeFeedback extends BaseEntity {

    private static final long serialVersionUID = 442905L;
    /**
     * 技术交流-供应商反馈ID
     */
    @ApiModelProperty("技术交流-供应商反馈ID")
    @TableId("TECHNICAL_EXCHANGE_FEEDBACK_ID")
    private Long technicalExchangeFeedbackId;
    /**
     * 采购商发布的交流单据的ID(指明这个反馈是反馈到哪个单据的)
     */
    @ApiModelProperty("采购商发布的交流单据的ID(指明这个反馈是反馈到哪个单据的)")
    @TableField("TECHNICAL_EXCHANGE_ID")
    private Long technicalExchangeId;
    /**
     * 供应商邀请表ID
     */
    @ApiModelProperty("供应商邀请表ID")
    @TableField("TECHNICAL_EXCHANGE_VENDOR_ID")
    private Long technicalExchangeVendorId;
    /**
     * 供应商ID
     */
    @ApiModelProperty("供应商ID")
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
     * 反馈的状态
     */
    @ApiModelProperty("反馈的状态")
    @TableField("FEEDBACK_STATUS")
    private String feedbackStatus;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;
}