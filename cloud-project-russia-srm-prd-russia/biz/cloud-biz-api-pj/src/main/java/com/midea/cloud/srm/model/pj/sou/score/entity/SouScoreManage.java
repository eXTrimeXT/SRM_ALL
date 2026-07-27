package com.midea.cloud.srm.model.pj.sou.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreDimension;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 评分管理
 * @author: hesl41
 * @Date: 2022/10/17 10:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_score_manage")
@ApiModel("评分管理")
public class SouScoreManage extends BaseEntity<SouScoreManage> {

    @ApiModelProperty("ID")
    @TableId("SCORE_MANAGE_ID")
    private Long scoreManageId;

    @ApiModelProperty("评分管理编号")
    @TableField("SCORE_MANAGE_NO")
    private String scoreManageNo;

    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("报价单ID")
    @TableField("ORDER_HEAD_ID")
    private Long orderHeadId;

    @ApiModelProperty("寻源单编号")
    @TableField("SOU_NO")
    private String souNo;

    @ApiModelProperty("寻源单名称")
    @TableField("SOU_NAME")
    private String souName;

    /**
     * @see SouGroup#getScoreAuth() 有权限的人，fullName
     */
    @ApiModelProperty("评分人")
    @TableField("SCORE_MANAGE_NAME")
    private String scoreManageName;

    @ApiModelProperty("寻源单类型")
    @TableField("SOU_TYPE")
    private String souType;

    @ApiModelProperty("轮次")
    @TableField("ROUND")
    private Integer round;
    /**
     * @see SouScoreDimension#getScoreDimensionId()
     */
    @ApiModelProperty("评分维度表ID")
    @TableField("SCORE_DIMENSION_ID")
    private Long scoreDimensionId;

    @ApiModelProperty("评分维度编码")
    @TableField("DIMENSION_CODE")
    private String dimensionCode;

    @ApiModelProperty("评分状态:已完成Y,未完成N")
    @TableField("SCORE_MANAGE_STATUS")
    private String scoreManageStatus;

//------------------下面的    exist = false

    @ApiModelProperty("寻源单状态")
    @TableField(exist = false)
    private String souProjectStatus;

    /**
     * 寻源单创建时间
     */
    @ApiModelProperty("寻源单创建时间")
    @TableField(exist = false)
    private Date souCreationDate;
    /**
     * 寻源单发布时间
     */
    @ApiModelProperty("寻源单发布时间")
    @TableField(exist = false)
    private Date publishTime;

    @ApiModelProperty("发布人")
    @TableField(exist = false)
    private String publishName;

    /** @see SouRound#getOrderEndTime */
    @ApiModelProperty("报价截止时间(冗余字段)")
    @TableField(exist = false)
    private Date orderEndTime;

    /**
     * @see SouProject#getEarliestBusinessOpenTime
     */
    @TableField(exist = false)
    @ApiModelProperty("开标时间")
    private Date earliestBusinessOpenTime;

}
