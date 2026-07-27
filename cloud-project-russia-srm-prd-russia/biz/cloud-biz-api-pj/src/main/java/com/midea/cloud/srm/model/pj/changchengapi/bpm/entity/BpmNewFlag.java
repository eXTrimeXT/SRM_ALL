package com.midea.cloud.srm.model.pj.changchengapi.bpm.entity;

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
 * @author ex_liuxy46
 */
@Data
@TableName("scc_bpm_new_flag")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "新BPM审批流标志表")
public class BpmNewFlag extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId("BPM_NEW_FLAG_ID")
    private Long bpmNewFlagId;

    @ApiModelProperty(value = "业务ID，用于业务单据关联")
    @TableField("BUSINESS_ID")
    private Long businessId;

    @ApiModelProperty(value = "审批流模板")
    @TableField("BUSSINESS_TYPE")
    private String bussinessType;

    @ApiModelProperty(value = "走新BPM审批流标志，Y/N")
    @TableField("NEW_BPM_FLAG")
    private String newBpmFlag;
}
