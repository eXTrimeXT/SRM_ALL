package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.tech;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * MQL - 技术评分
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouTechScoreDTO extends SouTechScoreHead {

    @ApiModelProperty("当前用户ID(如果没有groupId参数，则必须传递这个，否则无法得知是哪个工作小组成员)")
    private Long currentUserId;

    @ApiModelProperty("评分详情")
    private List<SouTechScoreLine> techScoreDetails;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    private Boolean tempSave = true;

    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (getProjectId() == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (Enable.Y.equals(getIsProxy())) {
            if (getGroupId() == null) {
                throw new IllegalArgumentException("代理报价时，请传递groupId参数");
            }
        } else {
            if (getGroupId() == null && currentUserId == null) {
                throw new IllegalArgumentException("请传递groupId或者currentUserId，否则无法获取工作小组成员信息");
            }
        }
        if (getVendorId() == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
        setTechComments(StringUtils.trimToNull(getTechComments()));
        int length = 300;
        if (getTechComments() != null && getTechComments().length() > length) {
            throw new IllegalArgumentException("技术评分建议的长度不能超过300");
        }
        if (techScoreDetails == null || techScoreDetails.size() == 0) {
            throw new IllegalArgumentException("缺少技术评分信息");
        }
    }

}
