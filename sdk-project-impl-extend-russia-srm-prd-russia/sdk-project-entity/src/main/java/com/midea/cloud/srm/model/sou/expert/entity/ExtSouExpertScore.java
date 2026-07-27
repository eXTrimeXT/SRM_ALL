package com.midea.cloud.srm.model.sou.expert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertScoreStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源 - 专家库 - 专家评审信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/20
 */
@Data
@TableName("scc_npm_sou_expert_score")
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertScore extends BaseEntity<ExtSouExpertScore> {

    @TableId("EXPERT_SCORE_ID")
    @ApiModelProperty("ID")
    private Long expertScoreId;

    @TableField("SOU_PROJECT_ID")
    @ApiModelProperty("寻源ID")
    private Long souProjectId;

    @TableField("SOU_NO")
    @ApiModelProperty("寻源编号")
    private String souNo;

    @TableField("SOU_NAME")
    @ApiModelProperty("寻源项目名称")
    private String souName;

    @TableField("PROJECT_ADDRESS")
    @ApiModelProperty("项目所在地")
    private String projectAddress;

    @TableField("TOTAL_AMOUNT_BY_TEN_KILO")
    @ApiModelProperty("概算金额(万元)")
    private BigDecimal totalAmountByTenKilo;

    /** @see ExtSouExpert#getExpertId */
    @TableField("EXPERT_ID")
    @ApiModelProperty("专家库表ID")
    private Long expertId;

    /** @see ExtSouExpert#getExpertUserId */
    @TableField("EXPERT_USER_ID")
    @ApiModelProperty("专家ID")
    private Long expertUserId;

    /** @see ExtSouExpert#getExpertUsername */
    @TableField("EXPERT_USERNAME")
    @ApiModelProperty("专家账号")
    private String expertUsername;

    /** @see ExtSouExpert#getExpertFullName */
    @TableField("EXPERT_FULL_NAME")
    @ApiModelProperty("专家昵称")
    private String expertFullName;

    /** @see ExtSouExpertScoreStatusEnum */
    @TableField("SCORE_STATUS")
    @ApiModelProperty("评价状态")
    private String scoreStatus;

    @TableField("SCORE_RESULT")
    @ApiModelProperty("评价结果")
    private BigDecimal scoreResult;

    @TableField("SCORE_TIME")
    @ApiModelProperty("评价时间")
    private Date scoreTime;

    @TableField("SCORE_FOR_LEADER")
    @ApiModelProperty("评分对象是否为组长")
    private Enable scoreForLeader;

}
