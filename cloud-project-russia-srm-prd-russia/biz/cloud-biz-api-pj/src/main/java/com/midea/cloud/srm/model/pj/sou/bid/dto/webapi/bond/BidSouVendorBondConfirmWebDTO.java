package com.midea.cloud.srm.model.pj.sou.bid.dto.webapi.bond;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouVendorBond;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 招投标 - 采购商确认缴纳情况
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
public class BidSouVendorBondConfirmWebDTO {

    /** @see BidSouVendorBond#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /**
     * 供应商ID集合
     */
    @ApiModelProperty("供应商ID集合")
    private List<Long> vendorIds;

    /** @see BidSouVendorBond#getHasPay */
    @ApiModelProperty("是否已缴纳")
    private Enable hasPay;

    /**
     * 入参格式化及校验
     */
    public void formatAndValidate() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(vendorIds)) {
            throw new IllegalArgumentException("请勾选需要确认缴纳情况的数据");
        }
        if (hasPay == null) {
            hasPay = Enable.N;
        }
    }

}
