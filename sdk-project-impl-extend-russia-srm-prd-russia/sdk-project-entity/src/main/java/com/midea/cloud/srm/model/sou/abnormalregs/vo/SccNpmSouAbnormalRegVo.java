package com.midea.cloud.srm.model.sou.abnormalregs.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/21
 */
@Data
@TableName("scc_npm_sou_abnormal_reg")
@ApiModel("异常登记实体Vo类，新增作废说明")
public class SccNpmSouAbnormalRegVo {
    @ApiModelProperty("异常登记表主键ID")
    private Long regId;
    @ApiModelProperty("招标ID")
    private Long projectId;
    @ApiModelProperty("招标项目编号")
    private String extProjectNo;
    @ApiModelProperty("异常登记状态，字典（NPM_ABNORMAL_REG_STATUS）")
    private String regStatus;
    @ApiModelProperty("项目名称")
    private String souName;
    @ApiModelProperty("反馈时间")
    private Date feedbackTime;
    @ApiModelProperty("异常发生时间")
    private Date abnormalTime;
    @ApiModelProperty("招标负责人ID")
    private Long souPrincipalId;
    @ApiModelProperty("招标负责人账号")
    private String souPrincipalNo;
    @ApiModelProperty("招标负责人")
    private String souPrincipalName;
    @ApiModelProperty("招标负责人科室ID")
    private String departmentId;
    @ApiModelProperty("招标负责人科室")
    private String departmentName;
    @ApiModelProperty("供应商负责人ID")
    private Long vendorPrincipalId;
    @ApiModelProperty("供应商负责人账号")
    private String vendorPrincipalNo;
    @ApiModelProperty("供应商负责人")
    private String vendorPrincipalName;
    @ApiModelProperty("供应商ID")
    private String vendorId;
    @ApiModelProperty("供应商编码")
    private String vendorCode;
    @ApiModelProperty("供应商名称")
    private String vendorName;
    @ApiModelProperty("异常类型(字典NPM_SOU_ABNORMAL_TYPE)")
    private String abnormalType;
    @ApiModelProperty("具体情况描述")
    private String abnormalDesc;
    @ApiModelProperty("处理结果")
    private String handlingResult;
    @ApiModelProperty("附件ID")
    private String fileId;
    @ApiModelProperty("附件名称")
    private String fileName;
    @ApiModelProperty("创建时间")
    private Date creationDate;
    @ApiModelProperty("创建人")
    private String createdFullName;
    @ApiModelProperty("最后更新时间")
    private Date lastUpdateDate;
    @ApiModelProperty("作废说明")
    private String abandonInstruction;
}
