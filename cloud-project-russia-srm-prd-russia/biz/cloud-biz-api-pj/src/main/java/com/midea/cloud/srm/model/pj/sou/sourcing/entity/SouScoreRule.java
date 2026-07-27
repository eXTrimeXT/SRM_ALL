package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源.核心表 - 评分规则
 * PS: 仅用于记录综合评分场景；对应合理低价/高价，纯价格维度，无需在这里记录
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_score_rule")
@ApiModel("寻源评分规则")
public class SouScoreRule extends BaseEntity<SouScoreRule> {

    @ApiModelProperty("ID")
    @TableId("SCORE_RULE_ID")
    private Long scoreRuleId;

    @ApiModelProperty("评分规则编码(SEQ_SOU_SCORE_RULE_NO)")
    @TableField("SCORE_RULE_NO")
    private String scoreRuleNo;

    @ApiModelProperty("评分规则名称")
    @TableField("SCORE_RULE_NAME")
    private String scoreRuleName;

    /** @see SouTypeEnum */
    @ApiModelProperty("寻源方式")
    @TableField("SOU_TYPE")
    private String souType;

    @ApiModelProperty("总分")
    @TableField("TOTAL_SCORE")
    private BigDecimal totalScore;

    @ApiModelProperty("评选方法")
    @TableField("SCORE_RULE_TYPE")
    private SouScoreRuleTypeEnum scoreRuleType;

    @ApiModelProperty("评分精度")
    @TableField("SCORE_PRECISION")
    private Integer scorePrecision;

    @ApiModelProperty("状态")
    @TableField("SCORE_RULE_STATUS")
    private SouScoreRuleStatusEnum scoreRuleStatus;

}
