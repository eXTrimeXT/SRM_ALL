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
 *  会议管理-议题
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

@ApiModel(description = "会议管理-议题")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_car_meet_topic")
public class MeetTopic extends BaseEntity {

    private static final long serialVersionUID = 2892850355048403334L;
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId("TOPIC_ID")
    private Long topicId;
    /**
     * 会议ID
     */
    @ApiModelProperty("会议ID")
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
     * 议题编号
     */
    @ApiModelProperty("议题编号（单据编码生成规则 ： SequenceCodeConstant.SEQ_MEET_TOPIC_CODE）")
    @TableField("TOPIC_CODE")
    private String topicCode;
    /**
     * 议题名称
     */
    @ApiModelProperty("议题名称")
    @TableField("TOPIC_NAME")
    private String topicName;
    /**
     * 上会类型
     */
    @ApiModelProperty("上会类型")
    @TableField("TOPIC_TYPE")
    private String topicType;
    /**
     * 议题模板ID
     */
    @ApiModelProperty("议题模板ID")
    @TableField("MODEL_ID")
    private Long modelId;
    /**
     * 议题模板编码
     */
    @ApiModelProperty("议题模板编码")
    @TableField("MODEL_CODE")
    private String modelCode;
    /**
     * 议题模板编码
     */
    @ApiModelProperty("议题模板名称")
    @TableField("MODEL_NAME")
    private String modelName;
    /**
     * 关联单据号
     */
    @ApiModelProperty("关联单据号")
    @TableField("RELATION_BILL_ID")
    private Long relationBillId;
    /**
     * 关联单据号
     */
    @ApiModelProperty("关联单据号")
    @TableField("RELATION_BILL_CODE")
    private String relationBillCode;
    /**
     * 议题状态
     * DRAFT : "拟定"
     * APPLY : "上会申请中"
     * MEETING : "上会中"
     * FINISH : "上会完成"
     */
    @ApiModelProperty("议题状态（枚举类：MeetTopicStatus）")
    @TableField("STATUS")
    private String status;
    /**
     * 待决议内容
     */
    @ApiModelProperty("待决议内容")
    @TableField("AWAIT_RESOLUTION")
    private String awaitResolution;
    /**
     * 会议纪要
     */
    @ApiModelProperty("会议纪要")
    @TableField("MEET_MINUTES")
    private String meetMinutes;
    /**
     * 议题结论
     * APPROVAL : "批准"
     * REJECT : "驳回"
     * UPGRADE : "升级"
     * DEFAULT : "默认值"
     */
    @ApiModelProperty("议题结论（枚举类：TopicResolutionStatus）")
    @TableField("TOPIC_CONCLUSION")
    private String topicConclusion;
    /**
     * 升级等级
     */
    @ApiModelProperty("升级等级")
    @TableField("UPGRADE")
    private String upgrade;
    /**
     * 负责人id
     */
    @ApiModelProperty("负责人id")
    @TableField("IN_CHARGE_ID")
    private Long inChargeId;
    /**
     * 负责人名字
     */
    @ApiModelProperty("负责人名字")
    @TableField("IN_CHARGE_NAME")
    private String inChargeName;
    /**
     * 负责人部门名称
     */
    @ApiModelProperty("负责人部门名称")
    @TableField("IN_CHARGE_DEPT_NAME")
    private String inChargeDeptName;

    /**
     * 完成日期
     */
    @ApiModelProperty("完成日期")
    @TableField("COMPLETE_DATE")
    private Date completeDate;
    /**
     * 品类ID
     */
    @ApiModelProperty("品类ID")
    @TableField("CATEGORY_ID")
    private Long categoryId;
    /**
     * 品类编码
     */
    @ApiModelProperty("品类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;
    /**
     * 品类名称
     */
    @ApiModelProperty("品类名称")
    @TableField("CATEGORY_FULL_NAME")
    private String categoryFullName;
}