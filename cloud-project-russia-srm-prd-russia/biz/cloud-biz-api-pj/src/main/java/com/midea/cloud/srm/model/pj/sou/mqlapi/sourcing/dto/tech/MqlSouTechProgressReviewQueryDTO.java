package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.tech;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTechScoreStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * MQL - 工作小组成员查询寻源单条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouTechProgressReviewQueryDTO extends BasePage {

    /** @see SouProject#getSouNo */
    @ApiModelProperty("询价单号")
    private String souNo;

    /** @see SouProject#getSouName */
    @ApiModelProperty("询价名称")
    private String souName;

    /** @see SouProject#getProjectStatus */
    @ApiModelProperty("项目状态")
    private String projectStatus;

    /** @see SouProject#getScoreRuleType */
    @ApiModelProperty("评分规则")
    private String scoreRuleType;

    /** @see SouProject#getCreatedBy */
    @ApiModelProperty("发布人")
    private String createdBy;

    /** @see SouTechScoreHead#getScoreStatus */
    @ApiModelProperty("技术评分状态")
    private SouTechScoreStatusEnum scoreStatus;

    /** @see SouProject#getCreationDate */
    @ApiModelProperty("创建日期从")
    private Date creationDateFrom;
    @ApiModelProperty("创建日期到")
    private Date creationDateTo;

    /** @see SouProject#getPublishTime */
    @ApiModelProperty("发布日期从")
    private Date publishTimeFrom;
    @ApiModelProperty("发布日期到")
    private Date publishTimeTo;

    /** @see SouProject#getOrderEndTime */
    @ApiModelProperty("报价截止时间从")
    private Date orderEndTimeFrom;
    @ApiModelProperty("报价截止时间到")
    private Date orderEndTimeTo;

    @ApiModelProperty("当前人员ID(必填)")
    protected Long currentUserId;

    @ApiModelProperty("寻源场景")
    private String souType;

    /**
     * 入参格式化
     */
    public void formatParams() {
        // 寻源单号
        souNo = StringUtils.trimToNull(souNo);
        // 寻源名称
        souName = StringUtils.trimToNull(souName);
        // 发布人
        createdBy = StringUtils.trimToNull(createdBy);
        if (currentUserId == null) {
            throw new IllegalArgumentException("缺少currentUserId参数");
        }
        // 创建日期范围
        if (creationDateFrom != null) {
            creationDateFrom = ApiSouProjectQueryDTO.getStartTimeOfDate(creationDateFrom);
        }
        if (creationDateTo != null) {
            creationDateTo = ApiSouProjectQueryDTO.getEndTimeOfDay(creationDateTo);
        }
        // 发布日期范围
        if (publishTimeFrom != null) {
            publishTimeFrom = ApiSouProjectQueryDTO.getStartTimeOfDate(publishTimeFrom);
        }
        if (publishTimeTo != null) {
            publishTimeTo = ApiSouProjectQueryDTO.getEndTimeOfDay(publishTimeTo);
        }
        // 报价截止时间范围
        if (orderEndTimeFrom != null) {
            orderEndTimeFrom = ApiSouProjectQueryDTO.getStartTimeOfDate(orderEndTimeFrom);
        }
        if (orderEndTimeTo != null) {
            orderEndTimeTo = ApiSouProjectQueryDTO.getEndTimeOfDay(orderEndTimeTo);
        }
    }

}
