package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 寻源openAPI - 工作小组成员查看需要评分的寻源单信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouTechProgressReviewVO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouProject#getSouNo */
    @ApiModelProperty("寻源单号")
    private String souNo;

    /** @see SouProject#getSouName */
    @ApiModelProperty("询价名称")
    private String souName;

    /** @see SouProject#getProjectStatus */
    @ApiModelProperty("项目状态")
    private SouProjectStatusEnum projectStatus;

    /** @see SouTechScoreHead#getScoreStatus */
    @ApiModelProperty("评分状态")
    private SouTechScoreStatusEnum scoreStatus;

    /** @see SouProject#getScoreRuleType */
    @ApiModelProperty("评分规则")
    private SouScoreRuleTypeEnum scoreRuleType;

    /** @see SouProject#getCurrentRound */
    @ApiModelProperty("当前轮次")
    private Integer currentRound;

    /** @see SouProject#getCreatedBy */
    @ApiModelProperty("发布人账号")
    private String createdBy;

    /** @see SouProject#getCreatedFullName */
    @ApiModelProperty("发布人名称")
    private String createdFullName;

    /** @see SouProject#getCreationDate */
    @ApiModelProperty("创建时间")
    private Date creationDate;

    /** @see SouProject#getPublishTime */
    @ApiModelProperty("发布时间")
    private Date publishTime;

    /** @see SouProject#getOrderEndTime */
    @ApiModelProperty("报价截止时间")
    private Date orderEndTime;

}
