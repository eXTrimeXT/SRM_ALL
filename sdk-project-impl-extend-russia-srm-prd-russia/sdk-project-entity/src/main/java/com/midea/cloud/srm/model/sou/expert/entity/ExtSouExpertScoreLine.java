package com.midea.cloud.srm.model.sou.expert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertScoreGroupTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源 - 专家库 - 专家评审明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/20
 */
@Data
@TableName("scc_npm_sou_expert_score_line")
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertScoreLine extends BaseEntity<ExtSouExpertScoreLine> {

    @TableId("EXPERT_SCORE_LINE_ID")
    @ApiModelProperty("ID")
    private Long expertScoreLineId;

    /** @see ExtSouExpertScore#getExpertScoreId */
    @TableField("EXPERT_SCORE_ID")
    @ApiModelProperty("专家评审ID")
    private Long expertScoreId;

    @TableField("USER_ID")
    @ApiModelProperty("用户ID")
    private Long userId;

    @TableField("USERNAME")
    @ApiModelProperty("用户账号")
    private String username;

    @TableField("NICKNAME")
    @ApiModelProperty("用户昵称")
    private String nickname;

    /** @see ExtSouExpertScoreGroupTypeEnum */
    @TableField("GROUP_TYPE")
    @ApiModelProperty("用户职责")
    private String groupType;

    @TableField("SCORE")
    @ApiModelProperty("评分")
    private BigDecimal score;

    /**
     * 如果是招标普通评分成员，那么这个字段有值，他的评分是由该代理用户来评的。
     * 只有该代理用户有这条数据的评分权限。
     */
    @TableField("PROXY_USER_ID")
    @ApiModelProperty("代理评分用户")
    private Long proxyUserId;

}
