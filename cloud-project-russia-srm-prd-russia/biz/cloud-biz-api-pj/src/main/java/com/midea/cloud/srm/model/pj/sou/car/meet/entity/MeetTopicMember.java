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
 *  会议管理-议题-议题成员
 * </pre>
 *
 * @author lianjh7@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: May 10, 2022 5:45:30 PM
 *  修改内容:
 * </pre>
 */

@ApiModel(description = "会议管理-议题-议题成员")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_car_meet_topic_member")
public class MeetTopicMember extends BaseEntity {

    private static final long serialVersionUID = 204684639553840345L;
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId("TOPIC_MEMBER_ID")
    private Long topicMemberId;
    /**
     * 议题ID
     */
    @ApiModelProperty("议题ID")
    @TableField("TOPIC_ID")
    private Long topicId;
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
     * 负责人
     */
    @ApiModelProperty("是否为负责人：Y-是/N-否")
    @TableField("IN_CHARGE")
    private Enable inCharge;
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
     * 角色
     */
    @ApiModelProperty("角色")
    @TableField("TOPIC_ROLE_CODE")
    private String topicRoleCode;
    /**
     * 是否是单据带过来的成员
     */
    @ApiModelProperty("是否为单据带过来的成员：Y-是/N-否")
    @TableField("FROM_RFQ")
    private Enable fromRfq;
}