package com.midea.cloud.srm.model.pj.sou.car.meet.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <pre>
 * 会议管理-待办
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录
 * 修改后版本:
 * 修改人: ex_nongtb
 * 修改日期: 2022/5/15 11:21
 * 修改内容:
 * </pre>
 */
@ApiModel(description = "会议管理-待办")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("scc_sou_car_meet_todo")
public class MeetTodo extends BaseEntity {

    private static final long serialVersionUID = 8607196070166421765L;
    /**
     * 待办id
     */
    @ApiModelProperty("待办id")
    @TableId("MEET_TODO_ID")
    private Long meetTodoId;
    /**
     * 待办编码
     */
    @ApiModelProperty("待办编码（单据编码生成规则 ： SequenceCodeConstant.SEQ_MEET_TODO_CODE）")
    @TableField("MEET_TODO_CODE")
    private String meetTodoCode;
    /**
     * 会议Id
     */
    @ApiModelProperty("会议Id")
    @TableField("MEET_ID")
    private Long meetId;
    /**
     * 库存组织ID
     */
    @ApiModelProperty("库存组织ID")
    @TableField("ORG_INV_ID")
    private Long invId;
    /**
     * 库存编码
     */
    @ApiModelProperty("库存编码")
    @TableField("ORG_INV_CODE")
    private String invCode;
    /**
     * 库存组织名称
     */
    @ApiModelProperty("库存组织名称")
    @TableField("ORG_INV_NAME")
    private String invName;
    /**
     * 议题类型（议题表上会类型）
     */
    @ApiModelProperty("议题类型（议题表上会类型）")
    @TableField("TOPIC_TYPE")
    private String topicType;
    /**
     * 议题ID
     */
    @ApiModelProperty("议题ID")
    @TableField("TOPIC_ID")
    private Long topicId;
    /**
     * 议题编码
     */
    @ApiModelProperty("议题编码")
    @TableField("TOPIC_CODE")
    private String topicCode;
    /**
     * 议题名称
     */
    @ApiModelProperty("议题名称")
    @TableField("TOPIC_NAME")
    private String topicName;
    /**
     * 待办状态
     */
    @ApiModelProperty("待办状态-枚举类-MeetTodoStatus")
    @TableField("TODO_STATUS")
    private String todoStatus;
    /**
     * 待办负责人ID
     */
    @ApiModelProperty("待办负责人ID")
    @TableField("TODO_DIRECTOR_ID")
    private Long todoDirectorId;
    /**
     * 待办负责人姓名
     */
    @ApiModelProperty("待办负责人姓名")
    @TableField("TODO_DIRECTOR_NAME")
    private String todoDirectorName;
    /**
     * 待办跟踪人ID
     */
    @ApiModelProperty("待办跟踪人ID")
    @TableField("TODO_STALKER_ID")
    private Long todoStalkerId;
    /**
     * 待办跟踪人姓名
     */
    @ApiModelProperty("待办跟踪人姓名")
    @TableField("TODO_STALKER_NAME")
    private String todoStalkerName;
    /**
     * 待办预计完成时间
     */
    @ApiModelProperty("待办预计完成时间")
    @TableField("TODO_EXPECT_FINISH_TIME")
    private Date todoExpectFinishTime;
    /**
     * 待办提醒时间
     */
    @ApiModelProperty("待办提醒时间")
    @TableField("TODO_REMINDER_TIME")
    private Date todoReminderTime;
    /**
     * 预警状态
     */
    @ApiModelProperty("预警状态-枚举类-MeetTodoWarningStatus")
    @TableField("EARLY_WARNING_STATUS")
    private String earlyWarningStatus;
    /**
     * 待办事项内容
     */
    @ApiModelProperty("待办事项内容")
    @TableField("TODO_CONTENT")
    private String todoContent;
    /**
     * 待办事项跟踪说明
     */
    @ApiModelProperty("待办事项跟踪说明")
    @TableField("TODO_STALKER_REMARK")
    private String todoStalkerRemark;

    /**
     * 责任部门ID
     */
    @ApiModelProperty("责任部门ID")
    @TableField("DEPARTMENT_ID")
    private Long departmentId;

    /**
     * 责任部门名称
     */
    @ApiModelProperty("责任部门名称")
    @TableField("DEPARTMENT_NAME")
    private String department;
}
