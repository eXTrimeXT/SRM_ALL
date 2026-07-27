package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto;

import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums.PrSouRequirementCancelStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;

/**
 * 招标计划 - 取消单据 - 审批未通过时的回调
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Data
@ApiModel(description = "审批未通过时的回调")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExtPrSouRequirementCancelUnPassDTO extends BaseObjectX {

    /** @see ExtPrSouRequirementCancel#getRequirementCancelId */
    @ApiModelProperty("取消单据ID")
    private Long requirementCancelId;

    /** @see ExtPrSouRequirementCancel#getCancelStatus */
    @ApiModelProperty("审批状态")
    private PrSouRequirementCancelStatusEnum cancelStatus;

    public void formatParams() {
        if (requirementCancelId == null) {
            throw new IllegalArgumentException("缺少requirementCancelId参数");
        }
        if (cancelStatus == null) {
            throw new IllegalArgumentException("缺少cancelStatus参数");
        }
        switch (cancelStatus) {
            case REJECTED:
                //已驳回
            case ABANDONED:
                //已废弃
            case WITHDRAW:
                //已撤回
                break;
            default:
                throw new IllegalArgumentException(MessageFormat.format("错误的接口调用:参数错误{0}", cancelStatus.name()));
        }
    }

}
