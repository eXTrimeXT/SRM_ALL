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
 *  会议管理-会议/议题-常用组-常用组成员
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

@ApiModel(description = "会议管理-会议/议题-常用组-常用组成员")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_car_meet_group_member")
public class MeetGroupMember extends BaseEntity {

    private static final long serialVersionUID = -3947723260661894731L;
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId("GROUP_MEMBER_ID")
    private Long groupMemberId;
    /**
     * 用户组ID
     */
    @ApiModelProperty("用户组ID")
    @TableField("GROUP_ID")
    private Long groupId;
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
}