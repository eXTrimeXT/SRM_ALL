package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init.ApiInqSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 寻源单查询条件 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouProjectQueryDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "寻源单查询条件")
@EqualsAndHashCode(callSuper = true)
public class ApiSouProjectQuerySwaggerDTO extends BasePage {

    /** @see ApiSouProjectQueryDTO#getSouNo */
    @ApiModelProperty("寻源单号(模糊查询)")
    private String souNo;

    /** @see ApiSouProjectQueryDTO#getSouName */
    @ApiModelProperty("寻源单名称(模糊查询)")
    private String souName;

    /** @see ApiSouProjectQueryDTO#getProjectStatus */
    @ApiModelProperty("寻源单状态(等值查询)[字典:SOU_PROJECT_STATUS]")
    private SouProjectStatusEnum projectStatus;

    /** @see ApiSouProjectQueryDTO#getScoreRuleType */
    @ApiModelProperty("评分规则(等值查询)[字典:SOU_SCORE_RULE_TYPE]")
    private SouScoreRuleTypeEnum scoreRuleType;

    /** @see ApiSouProjectQueryDTO#getCreatedId */
    @ApiModelProperty("创建人ID")
    private Long createdId;

    /** @see ApiSouProjectQueryDTO#getCreatedBy */
    @ApiModelProperty("创建人账号(模糊查询)")
    private String createdBy;

    /** @see ApiSouProjectQueryDTO#getCreateApprovalStatus */
    @ApiModelProperty("立项审核状态(等值查询)[字典:BIDDING_APPROVAL_STATUS]")
    private SouApprovalStatusEnum createApprovalStatus;

    /** @see ApiSouProjectQueryDTO#getCreationDateFrom */
    @ApiModelProperty("创建时间从")
    private Date creationDateFrom;

    /** @see ApiSouProjectQueryDTO#getCreationDateTo */
    @ApiModelProperty("创建时间到")
    private Date creationDateTo;

    /** @see ApiSouProjectQueryDTO#getPublishTimeFrom */
    @ApiModelProperty("发布时间从")
    private Date publishTimeFrom;

    /** @see ApiSouProjectQueryDTO#getPublishTimeTo */
    @ApiModelProperty("发布时间到")
    private Date publishTimeTo;

    /** @see ApiSouProjectQueryDTO#getCurrentUserId */
    @ApiModelProperty("当前登录人ID")
    private Long currentUserId;

    /** @see ApiInqSouProjectQueryDTO#getExtProjectStatus */
    @ApiModelProperty("单据状态(仅用于简易询价-inq)")
    private String extProjectStatus;

}
