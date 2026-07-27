package com.midea.cloud.srm.model.pj.sou.car.meet.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 *  会议管理-会议-会议成员表
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: May 11, 2022 3:32:28 PM
 *  修改内容:
 * </pre>
 */

@ApiModel(description = "会议管理-会议-会议成员表")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_car_meet_member")
public class MeetMember extends BaseEntity {

    private static final long serialVersionUID = 1331592533587321242L;
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId("MEET_MEMBER_ID")
    private Long meetMemberId;
    /**
     * 会议ID-该表与会议为一对多关系
     */
    @ApiModelProperty("会议ID")
    @TableField("MEET_ID")
    private Long meetId;
    /**
     * 成员ID
     */
    @ApiModelProperty("成员ID")
    @TableField("FULL_NAME_ID")
    private Long fullNameId;
    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    @TableField("FULL_NAME")
    private String fullName;
    /**
     * 部门
     */
    @ApiModelProperty("部门")
    @TableField("DEPT_NAME")
    private String deptName;
    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @TableField("MOBILE_NO")
    private String mobileNo;
    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    @TableField("EMAIL")
    private String email;
    /**
     * 主持人
     */
    @ApiModelProperty("是否为主持人：Y-是/N-否")
    @TableField("CHAIR")
    private Enable chair;
    /**
     * 决策人
     */
    @ApiModelProperty("是否为决策人：Y-是/N-否")
    @TableField("REVIEWER")
    private Enable reviewer;
    /**
     * 负责人
     */
    @ApiModelProperty("是否为负责人：Y-是/N-否")
    @TableField("IN_CHARGE")
    private Enable inCharge;
    /**
     * 负责议题ID
     */
    @ApiModelProperty("负责议题ID")
    @TableField("TOPIC_ID")
    private String topicId;
    /**
     * 负责议题编码
     */
    @ApiModelProperty("负责议题编码")
    @TableField("TOPIC_CODE")
    private String topicCode;
    /**
     * 是否是会议受邀者
     */
    @ApiModelProperty("是否为会议受邀者：Y-是/N-否")
    @TableField("ENABLE_MEET_INVITEE")
    private Enable enableMeetInvitee;
    /**
     * 是否是会议纪要发送人
     */
    @ApiModelProperty("是否为会议纪要发送人：Y-是/N-否")
    @TableField("ENABLE_MINUTES_SENDER")
    private Enable enableMinutesSender;

    /**
     * 是否是单据带过来的成员
     */
    @ApiModelProperty("是否为单据带过来的成员：Y-是/N-否")
    @TableField("FROM_RFQ")
    private Enable fromRfq;
}