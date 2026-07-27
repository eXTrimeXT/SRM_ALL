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

/**
 * <pre>
 *  项目化绩效项目-评分人 模型
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
@TableName("scc_npm_project_score_items_person")
@ApiModel(description = "项目化绩效项目")
public class ProjectScoreItemsPerson extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId("PROJECT_SCORE_ITEMS_PERSON_ID")
    private Long projectScoreItemsPersonId;

    @ApiModelProperty(value = "项目id")
    @TableField("PROJECT_SCORE_ITEMS_ID")
    private Long projectScoreItemsId;

    @ApiModelProperty(value = "品类ID")
    @TableField("CATEGORY_ID")
    private Long categoryId;

    @ApiModelProperty(value = "品类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;

    @ApiModelProperty(value = "品类名称")
    @TableField("CATEGORY_NAME")
    private String categoryName;

    @ApiModelProperty(value = "绩效模型id")
    @TableField("TEMPLATE_HEAD_ID")
    private Long templateHeadId;

    @ApiModelProperty(value = "绩效模型名称")
    @TableField("TEMPLATE_NAME")
    private String templateName;

    @ApiModelProperty(value = "评分人账号")
    @TableField("SCORE_MAN_ACCOUNT")
    private String scoreManAccount;

    @ApiModelProperty(value = "评分人姓名")
    @TableField("SCORE_MAN_NAME")
    private String scoreManName;

    @ApiModelProperty(value = "评分人ID")
    @TableField("SCORE_MAN_ID")
    private Long scoreManId;

    @ApiModelProperty(value = "邮箱")
    @TableField("EMAIL")
    private String email;

    @ApiModelProperty(value = "备注")
    @TableField("REMARK")
    private String remark;

    @ApiModelProperty(value = "审批状态")
    @TableField("APPROVE_STATUS")
    private String approveStatus;
}
