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

import java.util.Date;

/**
 * <pre>
 *  采购商-供应商技术交流 模型
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: Apr 27, 2022 4:06:28 PM
 *  修改内容:
 * </pre>
 */

@ApiModel(description = "采购商-供应商技术交流主表实体类")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_technical_exchange")
public class SouTechnicalExchange extends BaseEntity {

    private static final long serialVersionUID = 459375L;
    /**
     * 技术交流数据ID
     */
    @ApiModelProperty("技术交流数据ID")
    @TableId("TECHNICAL_EXCHANGE_ID")
    private Long technicalExchangeId;
    /**
     * 交流单号
     */
    @ApiModelProperty("交流单号")
    @TableField("TECHNICAL_EXCHANGE_FORM_CODE")
    private String technicalExchangeFormCode;
    /**
     * 交流标题
     */
    @ApiModelProperty("交流标题")
    @TableField("TECHNICAL_EXCHANGE_TITLE")
    private String technicalExchangeTitle;
    /**
     * 业务实体ID
     */
    @ApiModelProperty("业务实体ID")
    @TableField("ORG_OU_ID")
    private Long orgOuId;
    /**
     * 业务实体编码
     */
    @ApiModelProperty("业务实体编码")
    @TableField("ORG_OU_CODE")
    private String orgOuCode;

    @ApiModelProperty("")
    @TableField("ORG_OU_NAME")
    private String orgOuName;
    /**
     * 交流类型
     */
    @ApiModelProperty("交流类型")
    @TableField("TECHNICAL_EXCHANGE_TYPE")
    private String technicalExchangeType;
    /**
     * 预计开始时间
     */
    @ApiModelProperty("预计开始时间")
    @TableField("TECHNICAL_EXCHANGE_START_TIME")
    private Date technicalExchangeStartTime;
    /**
     * 预计结束时间
     */
    @ApiModelProperty("预计结束时间")
    @TableField("TECHNICAL_EXCHANGE_END_TIME")
    private Date technicalExchangeEndTime;
    /**
     * 采购方联系人
     */
    @ApiModelProperty("采购方联系人")
    @TableField("LINK_MAN")
    private String linkMan;
    /**
     * 采购方联系电话
     */
    @ApiModelProperty("采购方联系电话")
    @TableField("PHONE")
    private String phone;
    /**
     * 采购方联系邮箱
     */
    @ApiModelProperty("采购方联系邮箱")
    @TableField("EMAIL")
    private String email;
    /**
     * 单据状态
     */
    @ApiModelProperty("单据状态")
    @TableField("TECHNICAL_EXCHANGE_FORM_STATUS")
    private String technicalExchangeFormStatus;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;

    @ApiModelProperty("发布时间")
    @TableField("TECHNICAL_EXCHANGE_RELEASE_TIME")
    private Date technicalExchangeReleaseTime;
}