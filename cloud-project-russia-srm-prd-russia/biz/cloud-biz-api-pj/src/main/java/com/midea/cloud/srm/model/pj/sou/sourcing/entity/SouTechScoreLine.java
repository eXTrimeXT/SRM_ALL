package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 寻源核心 - 技术评分行信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_tech_score_line")
public class SouTechScoreLine extends BaseEntity<SouTechScoreLine> {

    @TableId("TECH_SCORE_LINE_ID")
    @ApiModelProperty("ID")
    private Long techScoreLineId;

    /** @see SouProject#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouScoreRuleLine#getScoreRuleLineId */
    @TableField("SCORE_RULE_LINE_ID")
    @ApiModelProperty("评分规则明细ID")
    private Long scoreRuleLineId;

    /** @see SouTechScoreHead#getTechScoreHeadId */
    @TableField("TECH_SCORE_HEAD_ID")
    @ApiModelProperty("技术评分头ID")
    private Long techScoreHeadId;

    /** @see SouOrder#getOrderId */
    @TableField("ORDER_ID")
    @ApiModelProperty("报价单ID")
    private Long orderId;

    /** @see SouGroup#getGroupId */
    @TableField("GROUP_ID")
    @ApiModelProperty("小组成员ID")
    private Long groupId;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID(冗余字段)")
    private Long vendorId;

    @TableField("SCORE")
    @ApiModelProperty("评分")
    private BigDecimal score;

}
