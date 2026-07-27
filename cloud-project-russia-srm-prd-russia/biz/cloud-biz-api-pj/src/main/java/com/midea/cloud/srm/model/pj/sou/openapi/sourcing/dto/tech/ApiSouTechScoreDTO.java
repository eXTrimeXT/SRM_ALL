package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechScoreLineDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 寻源openAPI - 技术评分
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouTechScoreDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /**
     * 当 isProxyScore = true 时，该字段必须有值
     * 当 isProxyScore = false 时，根据用户ID来定位 groupId
     * @see SouGroup#getGroupId
     */
    @ApiModelProperty("评委ID(可为空)")
    private Long groupId;
    @ApiModelProperty("当前用户ID(如果没有groupId参数，则必须传递这个，否则无法得知是哪个工作小组成员)")
    private Long currentUserId;

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("是否代理报价")
    private boolean isProxyScore = false;

    /** @see SouTechScoreHead#getTechComments */
    @ApiModelProperty("技术评分意见")
    private String techComments;

    @ApiModelProperty("评分详情")
    private List<ApiSouTechScoreLineDTO> techScoreDetails;

    @ApiModelProperty("true-暂存/false-提交")
    private boolean isTempSave = true;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (isProxyScore) {
            if (groupId == null) {
                throw new IllegalArgumentException("代理报价时，请传递groupId参数");
            }
        } else {
            if (groupId == null && currentUserId == null) {
                throw new IllegalArgumentException("请传递groupId或者currentUserId，否则无法获取工作小组成员信息");
            }
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
        techComments = StringUtils.trimToNull(techComments);
        int length = 300;
        if (techComments != null && techComments.length() > length) {
            throw new IllegalArgumentException("技术评分建议的长度不能超过300");
        }
        if (techScoreDetails == null || techScoreDetails.size() == 0) {
            throw new IllegalArgumentException("缺少技术评分信息");
        }
    }

}
