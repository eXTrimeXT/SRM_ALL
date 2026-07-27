package com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto;

import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.PriceApprovalItem;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 价格审批单 - 处理虚拟物料
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalHandleVirtualItemDTO extends BaseObjectX {

    /** @see PriceApprovalItem#getApprovalItemId */
    @ApiModelProperty("价格审批单明细行ID")
    private Long approvalItemId;

    /** @see PriceApprovalItem#getItemCode */
    @ApiModelProperty("物料编码")
    private String itemCode;

    public static void formatParams(List<PriceApprovalHandleVirtualItemDTO> params) {
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("请选择需要处理的虚拟物料信息");
        }
        Iterator<PriceApprovalHandleVirtualItemDTO> iterator = params.iterator();
        Set<Long> approvalItemIds = new HashSet<>(params.size());
        while (iterator.hasNext()) {
            PriceApprovalHandleVirtualItemDTO param = iterator.next();

            if (param.getApprovalItemId() == null) {
                throw new IllegalArgumentException("缺少approvalItemId参数");
            }
            param.setItemCode(StringUtils.trimToNull(param.getItemCode()));
            if (param.getItemCode() == null) {
                throw new IllegalArgumentException("缺少itemCode参数");
            }
            if (!approvalItemIds.add(param.getApprovalItemId())) { iterator.remove(); }
        }
    }

}
