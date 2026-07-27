package com.midea.cloud.srm.model.pj.sou.qa.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 采购商-澄清-项目信息变更
 * @author: hesl41
 * @Date: 2022/10/10 10:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_answer_info_change")
@ApiModel("采购商-澄清-项目信息变更")
public class SouAnswerInfoChange extends BaseEntity<SouAnswerInfoChange> {

    private static final long serialVersionUID = 1L;

    @TableId("SOU_ANSWER_INFO_CHANGE_ID")
    @ApiModelProperty("主键ID")
    protected Long souAnswerInfoChangeId;

    @TableField("ANSWER_ID")
    @ApiModelProperty("澄清表ID")
    protected Long answerId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源模块基础表ID,该表与基础表为一对一关系")
    @TableField("PROJECT_ID")
    protected Long projectId;

    @TableField("CHANGE_TYPE")
    @ApiModelProperty("变更信息 - before之前,after之后 ")
    protected String changeType;


    /** @see SouProject#getSignUpEndTime */
    @TableField("SIGN_UP_END_TIME")
    @ApiModelProperty("变更信息 - 报名截止时间")
    protected Date signUpEndTime;


    /** @see SouRound#getOrderStartTime */
    @TableField("ORDER_START_TIME")
    @ApiModelProperty("变更信息 - 投标开始时间")
    protected Date orderStartTime;


    /** @see SouRound#getOrderEndTime */
    @TableField("ORDER_END_TIME")
    @ApiModelProperty("变更信息 - 报名截止时间")
    protected Date orderEndTime;


    /** @see SouProject#getOrderSite */
    @TableField("ORDER_SITE")
    @ApiModelProperty("变更信息 - 投标地点")
    protected String orderSite;


}
