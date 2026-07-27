package com.midea.cloud.srm.model.sou.expert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源 - 专家申请 - 适用品类
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@TableName("scc_npm_sou_expert_category")
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertCategoryRelation extends BaseEntity<ExtSouExpertCategoryRelation> {

    @TableId("EXPERT_CATEGORY_ID")
    @ApiModelProperty("ID")
    private Long expertCategoryId;

    /** @see ExtSouExpertApply#getExpertApplyId */
    @TableField("EXPERT_APPLY_ID")
    @ApiModelProperty("专家申请ID")
    private Long expertApplyId;

    @TableField("CATEGORY_ID")
    @ApiModelProperty("品类ID(末级)")
    private Long categoryId;

    @TableField("CATEGORY_CODE")
    @ApiModelProperty("品类编码(末级)")
    private String categoryCode;

    @TableField("CATEGORY_NAME")
    @ApiModelProperty("品类名称(末级)")
    private String categoryName;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
