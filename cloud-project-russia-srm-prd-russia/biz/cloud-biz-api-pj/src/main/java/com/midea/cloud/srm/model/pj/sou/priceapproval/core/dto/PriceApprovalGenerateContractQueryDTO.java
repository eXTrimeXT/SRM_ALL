package com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 价格审批单 - 生成合同的条件参数
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalGenerateContractQueryDTO extends BaseObjectX {

    @ApiModelProperty("价格审批单ID集合")
    private Set<Long> approvalIds;

    /**
     * 入参校验
     */
    public void formatParams() {
        if (approvalIds == null) {
            throw new IllegalArgumentException("缺少approvalIds参数");
        } else {
            approvalIds = approvalIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
            if (approvalIds.isEmpty()) {
                throw new IllegalArgumentException("缺少approvalIds参数");
            }
        }
    }

}
