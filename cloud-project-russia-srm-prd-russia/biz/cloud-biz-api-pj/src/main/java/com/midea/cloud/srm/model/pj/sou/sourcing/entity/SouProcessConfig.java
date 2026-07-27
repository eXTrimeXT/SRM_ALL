package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源.核心表 - 流程配置
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_process_config")
@ApiModel("寻源流程配置")
public class SouProcessConfig extends BaseEntity<SouProcessConfig> {

    @ApiModelProperty("ID")
    @TableId("PROCESS_CONFIG_ID")
    protected Long processConfigId;

    @ApiModelProperty("流程配置名称")
    @TableField("PROCESS_CONFIG_NAME")
    protected String processConfigName;

    /** @see SouTypeEnum */
    @ApiModelProperty("寻源类型")
    @TableField("SOU_TYPE")
    protected String souType;

    @ApiModelProperty("状态")
    @TableField("PROCESS_STATUS")
    protected SouProcessConfigStatusEnum processStatus;

    @ApiModelProperty("发布范围")
    @TableField("PUBLISH_SCOPE")
    protected SouPublishScopeEnum publishScope;

    @ApiModelProperty("评选方式(低价/高价/综合)【评分规则】")
    @TableField("SCORE_RULE_TYPE")
    protected SouScoreRuleTypeEnum scoreRuleType;

    @ApiModelProperty("节点 - 项目信息")
    @TableField("PROJECT_INFO")
    protected Enable projectInfo;

    @ApiModelProperty("节点 - 项目需求")
    @TableField("REQUIRE_INFO")
    protected Enable requireInfo;

    @ApiModelProperty("节点 - 邀请供应商")
    @TableField("INVITE_VENDOR")
    protected Enable inviteVendor;

    @ApiModelProperty("节点 - 评分规则")
    @TableField("SCORE_RULE")
    protected Enable scoreRule;

    @ApiModelProperty("节点 - 立项审批")
    @TableField("CREATE_APPROVAL")
    protected Enable createApproval;

    @ApiModelProperty("节点 - 报名管理")
    @TableField("SIGN_UP_MANAGEMENT")
    protected Enable signUpManagement;

    @ApiModelProperty("节点 - 投标控制")
    @TableField("BIDING_CONTROL")
    protected Enable bidingControl;

    @ApiModelProperty("节点 - 技术标管理")
    @TableField("TECH_MANAGEMENT")
    protected Enable techManagement;

    @ApiModelProperty("节点 - 商务标管理")
    @TableField("BUSINESS_MANAGEMENT")
    protected Enable businessManagement;

    @ApiModelProperty("节点 - 评选")
    @TableField("EVALUATION")
    protected Enable evaluation;

    @ApiModelProperty("备注")
    @TableField("REMARK")
    protected String remark;

}
