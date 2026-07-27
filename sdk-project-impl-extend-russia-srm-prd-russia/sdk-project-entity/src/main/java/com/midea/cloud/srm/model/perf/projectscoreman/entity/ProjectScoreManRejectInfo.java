package com.midea.cloud.srm.model.perf.projectscoreman.entity;

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
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_npm_project_score_man_reject_info")
public class ProjectScoreManRejectInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("REJECT_INFO_ID")
    private Long rejectInfoId;

    @ApiModelProperty(value = "项目化绩效评分主表ID")
    @TableField("PROJECT_SCORE_MAN_ID")
    private Long projectScoreManId;

    @ApiModelProperty(value = "轮次")
    @TableField("SCORE_ROUND")
    private BigDecimal scoreRound;

    @ApiModelProperty(value = "驳回说明")
    @TableField("REJECT_INFO")
    private String rejectInfo;

    @ApiModelProperty(value = "驳回日期")
    @TableField("REJECT_DATE")
    private LocalDate rejectDate;

}
