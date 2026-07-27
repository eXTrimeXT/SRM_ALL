package com.midea.cloud.srm.model.pj.sapcreatesupview.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author fubiao
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_pj_sap_company_intf")
@ApiModel(description = "供应商编码同步SAP")
public class SapCompanyIntf extends BaseEntity {
    @ApiModelProperty(value = "主键ID")
    @TableField("SAP_COMPANY_INTF_ID")
    private Long sapCompanyIntfId;

    @ApiModelProperty(value = "供应商编码")
    @TableField("ORG_CODE")
    private String orgCode;

    @ApiModelProperty(value = "供应商来源状态（ADD新增 UPDATE 修改）")
    @TableField("SUP_STATE")
    private String supState;

    @ApiModelProperty(value = "处理状态，PENDING：未处理，COMPLETED：处理完成，ERROR：处理错误")
    @TableField("PROCESS_STATUS")
    private String processStatus;

    @ApiModelProperty(value = "处理信息")
    @TableField("PROCESS_MESSAGE")
    private String processMessage;

    @ApiModelProperty(value = "处理时间")
    @TableField("PROCESS_DATE")
    private Date processDate;

    @ApiModelProperty(value = "重试次数（超过三次不再重试）")
    @TableField("APP_NUM")
    private Long appNum;
}
