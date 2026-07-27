package com.midea.cloud.srm.model.sou.agreement.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "集采协议管理变更记录")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_jc_agreement_change")
public class SccSouJcAgreementChange extends BaseEntity<SccSouJcAgreementChange> {

    @ApiModelProperty("变更记录id")
    @TableId("AGREEMENT_CHANGE_ID")
    private Long agreementChangeId;

    @ApiModelProperty("协议id")
    @TableField("AGREEMENT_ID")
    private Long agreementId;

    @ApiModelProperty("协议信息id")
    @TableField("AGREEMENT_INFO_ID")
    private Long agreementInfoId;

    @ApiModelProperty("阶梯价id")
    @TableField("TIERED_PRICING_ID")
    private Long tieredPricingId;

    @ApiModelProperty("类型，1协议，2协议信息，3阶梯价")
    @TableField("TYPE")
    private Integer type;

    @ApiModelProperty("字段名称")
    @TableField("FIELD_NAME")
    private String fieldName;

    @ApiModelProperty("字段值")
    @TableField("FIELD_VALUE")
    private String fieldValue;

    @ApiModelProperty("老值")
    @TableField("OLD_VALUE")
    private String oldValue;

    @ApiModelProperty("新值")
    @TableField("NEW_VALUE")
    private String newValue;

    @ApiModelProperty("变更版本")
    @TableField("CHANGE_VERSION")
    private Integer changeVersion;
}
