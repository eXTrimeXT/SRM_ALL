package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.dto.init;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.pj.sou.inq.enums.InqSouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.time.ZoneId;
import java.util.Date;

/**
 * MQL - 寻源单查询条件
 *
 * @author zhangwk12@midea.com
 * @since 2023/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlInqSouProjectQueryDTO extends BasePage {

    /** @see SouProject#getSouNo */
    @ApiModelProperty("寻源单号(模糊查询)")
    private String souNo;

    /** @see SouProject#getSouName */
    @ApiModelProperty("寻源单名称(模糊查询)")
    private String souName;

    /** @see InqSouProject#getExtProjectStatus */
    @ApiModelProperty("寻源单状态(等值查询)[字典:SOU_PROJECT_STATUS]")
    private InqSouProjectStatusEnum extProjectStatus;

    /** @see SouProject#getScoreRuleType */
    @ApiModelProperty("评分规则(等值查询)[字典:SOU_SCORE_RULE_TYPE]")
    private SouScoreRuleTypeEnum scoreRuleType;

    /** @see SouProject#getCreatedId */
    @ApiModelProperty("创建人ID")
    private Long createdId;

    /** @see SouProject#getCreatedBy */
    @ApiModelProperty("创建人账号(模糊查询)")
    private String createdBy;

    /** @see SouProject#getCreateApprovalStatus */
    @ApiModelProperty("立项审核状态(等值查询)[字典:BIDDING_APPROVAL_STATUS]")
    private SouApprovalStatusEnum createApprovalStatus;

    /** @see SouProject#getCreationDate */
    @ApiModelProperty("创建时间从")
    private Date creationDateFrom;

    /** @see SouProject#getCreationDate */
    @ApiModelProperty("创建时间到")
    private Date creationDateTo;

    /** @see SouProject#getPublishTime */
    @ApiModelProperty("发布时间从")
    private Date publishTimeFrom;

    /** @see SouProject#getPublishTime */
    @ApiModelProperty("发布时间到")
    private Date publishTimeTo;

    @ApiModelProperty("当前登录人ID")
    private Long currentUserId;

    /**
     * 入参格式化
     */
    public void formatParams() {
        // 寻源单号
        souNo = StringUtils.trimToNull(souNo);
        // 寻源单名称
        souName = StringUtils.trimToNull(souName);
        // 创建人账号
        createdBy = StringUtils.trimToNull(createdBy);
        // 创建时间从
        if (creationDateFrom != null) {
            creationDateFrom = getStartTimeOfDate(creationDateFrom);
        }
        // 创建时间到
        if (creationDateTo != null) {
            creationDateTo = getEndTimeOfDay(creationDateTo);
        }
        // 发布时间从
        if (publishTimeFrom != null) {
            publishTimeFrom = getStartTimeOfDate(publishTimeFrom);
        }
        // 发布时间到
        if (publishTimeTo != null) {
            publishTimeTo = getEndTimeOfDay(publishTimeTo);
        }
    }

    /** 获取一天的开始时刻 */
    public static Date getStartTimeOfDate(Date time) {
        return Date.from(time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    /** 获取一天的结束时刻 */
    public static Date getEndTimeOfDay(Date day) {
        return Date.from(day.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .atTime(23, 59, 59, 999999999)
                .atZone(ZoneId.systemDefault()).toInstant());
    }

}
