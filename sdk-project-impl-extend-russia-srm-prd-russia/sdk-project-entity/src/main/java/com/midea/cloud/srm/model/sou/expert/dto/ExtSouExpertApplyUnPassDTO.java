package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;

/**
 * 寻源 - 专家库 - 专家申请
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExtSouExpertApplyUnPassDTO extends BaseObjectX {

    /** @see ExtSouExpertApply#getExpertApplyId */
    @ApiModelProperty("专家申请ID")
    private Long expertApplyId;

    /** @see ExtSouExpertApply#getApplyStatus */
    @ApiModelProperty("审批状态")
    private ExtSouExpertApplyStatusEnum applyStatus;

    public void formatParams() {
        if (expertApplyId == null) {
            throw new IllegalArgumentException("缺少expertApplyId参数");
        }
        if (applyStatus == null) {
            throw new IllegalArgumentException("缺少applyStatus参数");
        }
        switch (applyStatus) {
            case REJECTED:
                //已驳回
            case ABANDONED:
                //已废弃
            case WITHDRAW:
                //已撤回
                break;
            default:
                throw new IllegalArgumentException(MessageFormat.format("错误的接口调用:参数错误{0}", applyStatus.name()));
        }
    }

}
