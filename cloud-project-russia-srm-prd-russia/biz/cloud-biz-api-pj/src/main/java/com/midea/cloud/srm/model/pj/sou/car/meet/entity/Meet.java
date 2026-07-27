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
 *  会议管理-会议表实体
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

@ApiModel(description = "会议管理-会议表实体")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_car_meet")
public class Meet extends BaseEntity {

    private static final long serialVersionUID = -5781261236234646779L;
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId("MEET_ID")
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
     * 会议编号
     */
    @ApiModelProperty("会议编号（单据编码生成规则 ： SequenceCodeConstant.SEQ_MEETING_CODE）")
    @TableField("MEET_CODE")
    private String meetCode;
    /**
     * 会议标题(会议名称)
     */
    @ApiModelProperty("会议标题(会议名称)")
    @TableField("MEET_TITLE")
    private String meetTitle;
    /**
     * 会议时间-开始时间
     */
    @ApiModelProperty("会议时间-开始时间")
    @TableField("MEET_START_TIME")
    private Date meetingStartTime;
    /**
     * 会议时间-结束时间
     */
    @ApiModelProperty("会议时间-结束时间")
    @TableField("MEET_END_TIME")
    private Date meetEndTime;
    /**
     * 会议时长(单位分钟)
     */
    @ApiModelProperty("会议时长(单位分钟)")
    @TableField("MEET_MIN")
    private String meetMin;
    /**
     * 会议地点
     */
    @ApiModelProperty("会议地点")
    @TableField("MEET_ADDR")
    private String meetAddr;
    /**
     * 会议状态
     * DRAFT : "拟定"
     * PUBLISHED : "已发布"
     * IN_PROGRESS : "进行中"
     * END : "结束"
     */
    @ApiModelProperty("会议状态（枚举类：MeetingStatus）")
    @TableField("STATUS")
    private String status;
}