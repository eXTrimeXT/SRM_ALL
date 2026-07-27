package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTechScoreStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 寻源核心 - 技术评分头信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_tech_score_head")
@ApiModel("寻源核心-技术评分头信息")
public class SouTechScoreHead extends BaseEntity<SouTechScoreHead> {

    @TableId("TECH_SCORE_HEAD_ID")
    @ApiModelProperty("ID")
    private Long techScoreHeadId;

    /** @see SouProject#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouOrder#getOrderId */
    @TableField("ORDER_ID")
    @ApiModelProperty("报价单ID")
    private Long orderId;

    /** @see SouGroup#getGroupId */
    @TableField("GROUP_ID")
    @ApiModelProperty("评委ID")
    private Long groupId;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("SCORE_STATUS")
    @ApiModelProperty("评分状态[字典值: SOU_TECH_SCORE_STATUS]")
    private SouTechScoreStatusEnum scoreStatus;

    /** 相当于是 techScoreLine 的分数值 * 对应权重的总值 */
    @TableField("TOTAL_SCORE")
    @ApiModelProperty("技术评分(总分)")
    private BigDecimal totalScore;

    @TableField("TECH_COMMENTS")
    @ApiModelProperty("技术评分意见")
    private String techComments;

    @TableField("IS_PROXY")
    @ApiModelProperty("是否代理评分")
    private Enable isProxy;

}
