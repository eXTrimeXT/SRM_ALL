package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.bond;

import com.midea.cloud.srm.model.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 竞价 MQL - 供应商保证金查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/24
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MqlAuctSouVendorBondQueryDTO extends BasePage {

    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("供应商ID(非必填)")
    private Long vendorId;

    @ApiModelProperty("true-采购商端/false-供应商端")
    private boolean isBuyer = false;

    private String souType;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (!isBuyer && vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
    }

}
