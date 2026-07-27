package com.midea.cloud.srm.model.perf.projectscore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author 100014337
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_npm_project_score_header")
public class ProjectScoreHeader extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("SCORE_HEADER_ID")
    private Long scoreHeaderId;

    @ApiModelProperty(value = "评分项目ID")
    @TableField("PROJECT_SCORE_ITEMS_ID")
    private Long projectScoreItemsId;

    @ApiModelProperty(value = "评分项目名称")
    @TableField("PROJECT_NAME")
    private String projectName;

    @ApiModelProperty(value = "合同编号")
    @TableField("CONTRACT_NO")
    private String contractNo;

    @ApiModelProperty(value = "合同名称")
    @TableField("CONTRACT_NAME")
    private String contractName;

    @ApiModelProperty(value = "品类ID")
    @TableField("CATEGORY_ID")
    private Long categoryId;

    @ApiModelProperty(value = "品类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;

    @ApiModelProperty(value = "品类名称")
    @TableField("CATEGORY_NAME")
    private String categoryName;

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

    @ApiModelProperty(value = "成绩")
    @TableField("SCORE")
    private BigDecimal score;

    @ApiModelProperty(value = "等级")
    @TableField("LEVEL_NAME")
    private String levelName;

    @ApiModelProperty(value = "整体评分状态")
    @TableField("PROJECT_STATUS")
    private String projectStatus;

    @ApiModelProperty(value = "履约类型")
    @TableField("PERFORMANCE_TYPE")
    private String performanceType;

    @ApiModelProperty(value = "处理备注")
    @TableField("SUPPLIER_REMARK")
    private String supplierRemark;

    @ApiModelProperty(value = "预警状态")
    @TableField("WARNING_STATUS")
    private String warningStatus;

    @ApiModelProperty(value = "预警单据号")
    @TableField("WARNING_CODE")
    private String warningCode;

    @ApiModelProperty(value = "评分时间")
    @TableField("CALC_DATE")
    private LocalDate calcDate;

    @ApiModelProperty(value = "预警单据ID")
    @TableField("WARNING_ID")
    private Long warningId;

    @ApiModelProperty(value = "备注")
    @TableField("REMARK")
    private String remark;

    @ApiModelProperty(value = "等级名称查询条件")
    @TableField(exist = false)
    private List<String> levelNames;

    @ApiModelProperty(value = "投资编号")
    @TableField("EXT_INVEST_NO")
    private String extInvestNo;

    /**
     * 评分大于-查询条件
     */
    @ApiModelProperty(value = "评分大于-查询条件")
    @TableField(exist = false)
    private BigDecimal scoreStart;

    /**
     * 评分小于于-查询条件
     */
    @ApiModelProperty(value = "评分小于于-查询条件")
    @TableField(exist = false)
    private BigDecimal scoreEnd;

    /**
     * 评分时间大于-查询条件
     */
    @ApiModelProperty(value = "评分时间大于-查询条件")
    @TableField(exist = false)
    private LocalDate calcDateStart;

    /**
     * 评分时间小于-查询条件
     */
    @ApiModelProperty(value = "评分时间小于-查询条件")
    @TableField(exist = false)
    private LocalDate calcDateEnd;

    @ApiModelProperty(value = "取消状态")
    @TableField("EXT_CANCEL_STATUS")
    private Integer extCancelStatus;

    @ApiModelProperty(value = "供应商集合-查询条件")
    @TableField(exist = false)
    private List<Long> companyIdList;

}
