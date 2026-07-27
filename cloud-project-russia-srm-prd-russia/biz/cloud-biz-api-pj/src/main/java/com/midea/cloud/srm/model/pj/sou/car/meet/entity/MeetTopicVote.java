package com.midea.cloud.srm.model.pj.sou.car.meet.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 * 会议管理-议题-议题决议
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录
 * 修改后版本:
 * 修改人: ex_nongtb
 * 修改日期: 2022/5/17 14:28
 * 修改内容:
 * </pre>
 */
@ApiModel(description = "会议管理-议题-议题决议")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_car_meet_topic_vote")
public class MeetTopicVote extends BaseEntity {

    private static final long serialVersionUID = 8262769482140148884L;
    /**
     * 议题决策id
     */
    @ApiModelProperty("议题决策id")
    @TableId("VOTE_ID")
    private Long voteId;
    /**
     * 决策人ID
     */
    @ApiModelProperty("决策人ID")
    @TableField("VOTER_ID")
    private Long voterId;
    /**
     * 决策人名称
     */
    @ApiModelProperty("决策人名称")
    @TableField("VOTER_NAME")
    private String voterName;
    /**
     * 决策结果
     * APPROVAL : "批准"
     * REJECT : "驳回"
     * WAIT_APPROVAL : "待批准"
     */
    @ApiModelProperty("决策结果（枚举类：TopicReviewerStatus）")
    @TableField("VOTE_RESULTS")
    private String voteResults;
    /**
     * 会议id
     */
    @ApiModelProperty("会议id")
    @TableField("MEET_ID")
    private Long meetId;
    /**
     * 议题id
     */
    @ApiModelProperty("议题id")
    @TableField("TOPIC_ID")
    private Long topicId;
}
