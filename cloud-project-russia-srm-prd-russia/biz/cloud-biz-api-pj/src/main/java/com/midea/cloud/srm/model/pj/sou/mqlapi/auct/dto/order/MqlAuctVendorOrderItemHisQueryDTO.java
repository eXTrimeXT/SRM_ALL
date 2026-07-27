package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.order;

import com.midea.cloud.srm.model.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 竞价 MQL - 供应商历史报价明细查询条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctVendorOrderItemHisQueryDTO extends BasePage {

    @ApiModelProperty("寻源单ID(必填)")
    private Long projectId;

    @ApiModelProperty("物料名称")
    private String itemDesc;

    @ApiModelProperty("轮次")
    private Integer round;

    @ApiModelProperty("true-采购商端/false-供应商端")
    private Boolean buyer;

    @ApiModelProperty("供应商ID(如果是供应商端，则必填)")
    private Long vendorId;

    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        itemDesc = StringUtils.trimToNull(itemDesc);
        if (!buyer && vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
    }

}
