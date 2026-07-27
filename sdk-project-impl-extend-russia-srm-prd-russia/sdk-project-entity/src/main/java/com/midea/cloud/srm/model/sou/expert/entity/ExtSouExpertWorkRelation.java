package com.midea.cloud.srm.model.sou.expert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.bid.purchaser.bidexpert.entity.BidExpertWorkRecord;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 寻源 - 专家申请 - 亲属工作经历
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_npm_sou_expert_work_relate")
public class ExtSouExpertWorkRelation extends BaseEntity<BidExpertWorkRecord> {

    @TableId("EXPERT_WORK_RELATE_ID")
    @ApiModelProperty("ID")
    private Long expertWorkRelateId;

    /** @see ExtSouExpertApply#getExpertApplyId */
    @TableField("EXPERT_APPLY_ID")
    @ApiModelProperty("专家申请ID")
    private Long expertApplyId;

    @TableField("RELATIVE_NAME")
    @ApiModelProperty("亲属名称")
    private String relativeName;

    @TableField("RELATIVE_TYPE")
    @ApiModelProperty("亲属关系")
    private String relativeType;

    @TableField("WORK_UNIT")
    @ApiModelProperty("工作单位")
    private String workUnit;

    @TableField("JOB")
    @ApiModelProperty("职务")
    private String job;

    @TableField("ENTRY_DATE")
    @ApiModelProperty("入职时间")
    private LocalDate entryDate;

    @TableField("QUIT_DATE")
    @ApiModelProperty("离职时间")
    private LocalDate quitDate;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
