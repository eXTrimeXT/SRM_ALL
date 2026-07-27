package com.midea.cloud.srm.model.perf.projectscoreitem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * <pre>
 *  项目化绩效项目 模型
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023-11-07 15:10:37
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_npm_project_score_items")
@ApiModel(description = "项目化绩效项目")
public class ProjectScoreItems extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId("PROJECT_SCORE_ITEMS_ID")
    private Long projectScoreItemsId;

    @ApiModelProperty(value = "评分项目名称")
    @TableField("PROJECT_NAME")
    private String projectName;

    @ApiModelProperty(value = "项目状态")
    @TableField("PROJECT_STATUS")
    private String projectStatus;

    @ApiModelProperty(value = "合同编号")
    @TableField("CONTRACT_NO")
    private String contractNo;

    @ApiModelProperty(value = "合同名称")
    @TableField("CONTRACT_NAME")
    private String contractName;

    @ApiModelProperty(value = "履约阶段")
    @TableField("PERFORMANCE_CODE")
    private String performanceCode;

    @ApiModelProperty(value = "公司id")
    @TableField("OU_ORGANIZATION_ID")
    private Long ouOrganizationId;

    @ApiModelProperty(value = "公司编码")
    @TableField("OU_ORGANIZATION_CODE")
    private String ouOrganizationCode;

    @ApiModelProperty(value = "公司名称")
    @TableField("OU_ORGANIZATION_NAME")
    private String ouOrganizationName;

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

    @ApiModelProperty(value = "招标编号")
    @TableField("BID_CODE")
    private String bidCode;

    @ApiModelProperty(value = "招标结束时间")
    @TableField("BID_END_DATE")
    private LocalDate bidEndDate;

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

    @ApiModelProperty(value = "绩效开始月份(2020-01)")
    @TableField("PER_START_MONTH")
    private LocalDate perStartMonth;

    @ApiModelProperty(value = "绩效结束月份(2020-02)")
    @TableField("PER_END_MONTH")
    private LocalDate perEndMonth;

    @ApiModelProperty(value = "复核状态")
    @TableField("CHECK_STATUS")
    private String checkStatus;

    @ApiModelProperty(value = "合同id")
    @TableField("CONTRACT_HEAD_ID")
    private Long contractHeadId;

    @ApiModelProperty(value = "投资编号")
    @TableField("EXT_INVEST_NO")
    private String extInvestNo;

    @ApiModelProperty(value = "取消状态")
    @TableField("EXT_CANCEL_STATUS")
    private Integer extCancelStatus;

}
