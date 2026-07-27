package com.midea.cloud.srm.model.pj.sou.brg.dto.webapi.bond;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouVendorBond;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 项目式询价 - 采购商确认缴纳情况
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/27
 */
@Data
public class BrgSouVendorBondConfirmWebDTO {

    /** @see BrgSouVendorBond#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /**
     * 供应商ID集合
     */
    @ApiModelProperty("供应商ID集合")
    private List<Long> vendorIds;

    /** @see BrgSouVendorBond#getHasPay */
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
