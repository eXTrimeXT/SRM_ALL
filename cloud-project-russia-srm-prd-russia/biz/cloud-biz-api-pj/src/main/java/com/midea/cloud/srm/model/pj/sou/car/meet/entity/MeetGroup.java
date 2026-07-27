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
 *  会议管理-会议/议题-常用组
 * </pre>
 *
 * @author lianjh7@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: May 10, 2022 10:56:19 AM
 *  修改内容:
 * </pre>
 */

@ApiModel(description = "会议管理-会议/议题-常用组")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_car_meet_group")
public class MeetGroup extends BaseEntity {

    private static final long serialVersionUID = 1289024202697709285L;
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId("GROUP_ID")
    private Long groupId;

    /**
     * 所属用户ID
     */
    @ApiModelProperty("所属用户ID")
    @TableField(value = "USER_ID")
    private Long userId;

    /**
     * 用户组名称
     */
    @ApiModelProperty("用户组名称")
    @TableField("GROUP_NAME")
    private String groupName;

    /**
     * 常用组类型（会议常用组/议题常用组）
     */
    @ApiModelProperty("常用组类型（会议常用组/议题常用组）")
    @TableField("GROUP_TYPE")
    private String groupType;
}