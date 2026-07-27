package com.midea.cloud.srm.model.perf.projectscorewarning.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_npm_project_score_warning")
public class ProjectScoreWarning extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("WARNING_ID")
    private Long warningId;

    @ApiModelProperty(value = "预警单据号")
    @TableField("WARNING_CODE")
    private String warningCode;

    @ApiModelProperty(value = "预警主题")
    @TableField("WARNING_NAME")
    private String warningName;

    @ApiModelProperty(value = "评分项目ID")
    @TableField("PROJECT_SCORE_ITEMS_ID")
    private Long projectScoreItemsId;

    @ApiModelProperty(value = "评分项目名称")
    @TableField("PROJECT_NAME")
    private String projectName;

    @ApiModelProperty(value = "公司id")
    @TableField("OU_ORGANIZATION_ID")
    private Long ouOrganizationId;

    @ApiModelProperty(value = "公司编码")
    @TableField("OU_ORGANIZATION_CODE")
    private String ouOrganizationCode;

    @ApiModelProperty(value = "公司名称")
    @TableField("OU_ORGANIZATION_NAME")
    private String ouOrganizationName;

    @ApiModelProperty(value = "预警类型")
    @TableField("PERF_MODEL_TYPE")
    private String perfModelType;

    @ApiModelProperty(value = "预警状态")
    @TableField("WARNING_STATUS")
    private String warningStatus;

    @ApiModelProperty(value = "查看状态(Y/N)")
    @TableField("READ_STATUS")
    private String readStatus;

    @ApiModelProperty(value = "板块id")
    @TableField("BU_ORGANIZATION_ID")
    private Long buOrganizationId;

    @ApiModelProperty(value = "板块编码")
    @TableField("BU_ORGANIZATION_CODE")
    private String buOrganizationCode;

    @ApiModelProperty(value = "板块名称")
    @TableField("BU_ORGANIZATION_NAME")
    private String buOrganizationName;

    @ApiModelProperty(value = "供应商ID")
    @TableField("COMPANY_ID")
    private Long companyId;

    @ApiModelProperty(value = "供应商编码")
    @TableField("COMPANY_CODE")
    private String companyCode;

    @ApiModelProperty(value = "供应商名称")
    @TableField("COMPANY_NAME")
    private String companyName;

    @ApiModelProperty(value = "招标负责人")
    @TableField("BID_MANAGER")
    private String bidManager;

    @ApiModelProperty(value = "招标负责人部门全路径")
    @TableField("BID_MANAGER_FULL_PATH")
    private String bidManagerFullPath;

    @ApiModelProperty(value = "合同经办人")
    @TableField("CONTRACT_MANAGER")
    private String contractManager;

    @ApiModelProperty(value = "合同经办人部门全路径")
    @TableField("CONTRACT_MANAGER_FULL_PATH")
    private String contractManagerFullPath;

    @ApiModelProperty(value = "预警详情")
    @TableField("WARNING_REMARK")
    private String warningRemark;

}
