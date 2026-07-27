package com.midea.cloud.srm.model.sou.expert.vo;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScore;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 专家库 - 专家评审列表
 *
 * @author zhangwk12@meicloud.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertScoreQueryVO extends ExtSouExpertScore {

    /** @see ExtSouExpertScoreLine#getUserId */
    @ApiModelProperty("招标组长ID")
    private Long leaderUserId;

    /** @see ExtSouExpertScoreLine#getUsername */
    @ApiModelProperty("招标组长账号")
    private String leaderUsername;

    /** @see ExtSouExpertScoreLine#getNickname */
    @ApiModelProperty("招标组长昵称")
    private String leaderNickname;

    /** @see ExtSouExpertScoreLine#getUserId */
    @ApiModelProperty("招标负责人ID")
    private Long managerUserId;

    /** @see ExtSouExpertScoreLine#getUsername */
    @ApiModelProperty("招标负责人账号")
    private String managerUsername;

    /** @see ExtSouExpertScoreLine#getNickname */
    @ApiModelProperty("招标负责人昵称")
    private String managerNickname;

    /** @see ExtSouExpert#getJobStatus */
    @ApiModelProperty("在职状态")
    private String jobStatus;

    /** @see ExtSouExpert#getHasQuite */
    @ApiModelProperty("是否已退出")
    private Enable hasQuite;

    @ApiModelProperty("评分明细")
    private List<ExtSouExpertScoreLine> scoreLineList;

}
