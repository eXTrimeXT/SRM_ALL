package com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto;

import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.PriceApprovalItem;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 价格审批单 - 生成合同的参数
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalGenerateContractDTO extends BaseObjectX {

    /** @see PriceApprovalItem#getApprovalItemId */
    @ApiModelProperty("价格审批单明细ID")
    private Long approvalItemId;

    @Nullable
    @ApiModelProperty("转合同分配数量(为null/0的时候默认是分配剩余所有可分配数量)")
    private BigDecimal toAssignNum;

    /**
     * 入参格式化
     */
    public static void formatParams(@Nullable List<PriceApprovalGenerateContractDTO> params) {
        if (CollectionUtils.isEmpty(params)) {
            throw new IllegalArgumentException("缺少数据");
        } else {
            params.forEach(param -> {
                if (param.approvalItemId == null) {
                    throw new IllegalArgumentException("缺少approvalItemId字段");
                }
                if (param.toAssignNum != null && param.toAssignNum.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("toAssignNum不能小于0");
                }
            });
        }
    }

}
