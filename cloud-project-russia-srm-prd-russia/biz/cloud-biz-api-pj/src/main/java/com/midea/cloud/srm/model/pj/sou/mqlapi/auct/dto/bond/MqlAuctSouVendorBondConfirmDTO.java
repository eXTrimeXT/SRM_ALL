package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.bond;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

/**
 * 竞价 MQL - 保证金确认/驳回
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouVendorBondConfirmDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("供应商ID集合")
    private Set<Long> vendorIds;

    @ApiModelProperty("true-确认/false-驳回")
    private Boolean toConfirm;

    private String souType;

    /**
     * 入参格式化及校验
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(vendorIds)) {
            throw new IllegalArgumentException("请勾选需要确认缴纳情况的数据");
        }
    }

}
