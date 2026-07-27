package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源 - 专家申请 - 查询最新申请信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertLatestApplyQueryDTO extends BaseObjectX {

    /** @see ExtSouExpertApply#getExpertApplyId */
    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("true-优先返回最新已审批的/false-返回最新的")
    private Boolean priorityApprovalPass;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (userId == null) {
            throw new IllegalArgumentException("缺少userId参数");
        }
    }

}
