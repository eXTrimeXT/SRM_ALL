package com.midea.cloud.srm.model.sou.expert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertFrozenStatusEnum;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertJobStatusEnum;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertLevelEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源 - 专家库
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@TableName("scc_npm_sou_expert")
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpert extends BaseEntity<ExtSouExpert> {

    @TableId("EXPERT_ID")
    @ApiModelProperty("ID")
    private Long expertId;

    /** @see ExtSouExpertApply#getExpertApplyId */
    @TableField("EXPERT_APPLY_ID")
    @ApiModelProperty("专家申请ID")
    private Long expertApplyId;

    @TableField("APPLY_ID_FULL_PATH")
    @ApiModelProperty("申请单ID记录，如【1,2,3】每次升级记录一次，最新记录放后面")
    private String applyIdFullPath;

    // ------------------------------------------------------ 冗余字段 -------------------------------------------------------
    /** @see ExtSouExpertApply#getExpertApplyNo */
    @TableField("EXPERT_APPLY_NO")
    @ApiModelProperty("专家申请编号")
    private String expertApplyNo;

    /**
     * @see ExtSouExpertApply#getApplyLevel
     * @see ExtSouExpertLevelEnum
     */
    @TableField("EXPERT_LEVEL")
    @ApiModelProperty("专家等级")
    private String expertLevel;

    /** @see ExtSouExpertApply#getApplyById */
    @TableField("EXPERT_USER_ID")
    @ApiModelProperty("专家ID")
    private Long expertUserId;

    /** @see ExtSouExpertApply#getApplyBy */
    @TableField("EXPERT_USERNAME")
    @ApiModelProperty("专家账号")
    private String expertUsername;

    @TableField("EXPERT_USER_CODE")
    @ApiModelProperty("专家工号")
    private String expertUserCode;

    /** @see ExtSouExpertApply#getApplyByNickname */
    @TableField("EXPERT_FULL_NAME")
    @ApiModelProperty("专家昵称")
    private String expertFullName;

    /**
     * @see ExtSouExpertApply#getJobStatus
     * @see ExtSouExpertJobStatusEnum
     */
    @TableField("JOB_STATUS")
    @ApiModelProperty("在职状态")
    private String jobStatus;

    /** ----------------------------------------------------- 其他参数信息 ----------------------------------------------------- */
    @TableField("HAS_QUITE")
    @ApiModelProperty("是否已退出")
    private Enable hasQuite;

    @TableField("HAS_FROZEN")
    @ApiModelProperty("是否已冻结")
    private Enable hasFrozen;

    @TableField("QUITE_REASON")
    @ApiModelProperty("退出原因")
    private String quiteReason;

    /** @see ExtSouExpertFrozenStatusEnum */
    @TableField("FROZEN_STATUS")
    @ApiModelProperty("冻结状态")
    private String frozenStatus;

    @TableField("FROZEN_REASON")
    @ApiModelProperty("冻结/解冻原因")
    private String frozenReason;

    @TableField("FROZEN_REJECT_REASON")
    @ApiModelProperty("拒绝冻结/解冻说明")
    private String frozenRejectReason;

}
