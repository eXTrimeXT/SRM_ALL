package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScore;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 专家评审 - 列表查询条件
 *
 * @author zhangwk12@meicloud.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertScoreQueryDTO extends BasePage {

    /** @see ExtSouExpertScore#getSouName */
    @ApiModelProperty("项目名称")
    private String souName;

    /**
     * @see ExtSouExpertScoreLine#getUsername
     * {@link ExtSouExpertScoreLine} == SCORE_LEADER
     */
    @ApiModelProperty("招标组长名称")
    private String leaderUserName;

    /**
     * @see ExtSouExpertScoreLine#getUsername
     * {@link ExtSouExpertScoreLine} == SCORE_MANAGER
     */
    @ApiModelProperty("招标负责人名称")
    private String managerUserName;

    /** @see ExtSouExpertScore#getProjectAddress */
    @ApiModelProperty("项目所在地")
    private String projectAddress;

    /** @see ExtSouExpertScore#getScoreTime */
    @ApiModelProperty("评价时间从")
    private Date scoreTimeFrom;

    /** @see ExtSouExpertScore#getScoreTime */
    @ApiModelProperty("评价时间到")
    private Date scoreTimeTo;

    /**
     * 入参格式化
     */
    public void formatParams() {
        souName = StringUtils.trimToNull(souName);
        leaderUserName = StringUtils.trimToNull(leaderUserName);
        managerUserName = StringUtils.trimToNull(managerUserName);
        projectAddress = StringUtils.trimToNull(projectAddress);
    }

}
