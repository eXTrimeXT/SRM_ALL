package com.midea.cloud.srm.model.sou.expert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源 - 专家申请 - 适用组织
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@TableName("scc_npm_sou_expert_org")
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertOrgRelation extends BaseEntity<ExtSouExpertOrgRelation> {

    @TableId("EXPERT_ORG_ID")
    @ApiModelProperty("ID")
    private Long expertOrgId;

    /** @see ExtSouExpertApply#getExpertApplyId */
    @TableField("EXPERT_APPLY_ID")
    @ApiModelProperty("专家申请ID")
    private Long expertApplyId;

    @TableField("ORG_ID")
    @ApiModelProperty("适用组织ID(组织等级不固定)")
    private Long orgId;

    @TableField("ORG_CODE")
    @ApiModelProperty("适用组织编码(组织等级不固定)")
    private String orgCode;

    @TableField("ORG_NAME")
    @ApiModelProperty("适用组织名称(组织等级不固定)")
    private String orgName;

    @TableField("FULL_PATH_ID")
    @ApiModelProperty("组织全路径ID")
    private String fullPathId;

    @TableField("FULL_PATH_NAME")
    @ApiModelProperty("组织全路径名称")
    private String fullPathName;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
