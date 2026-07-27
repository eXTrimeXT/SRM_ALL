package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.control;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemRecord;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorAuth;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MQL - 物料变更的供应商权限
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouItemRefreshAuthDTO extends BaseObjectX {

    /** @see SouItemRecord#getRecordId */
    @ApiModelProperty("变更记录ID")
    private Long recordId;

    /** @see SouVendorAuth#getVendorId */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouVendorAuth#getForbidPrice */
    @ApiModelProperty("是否禁止报价")
    private Enable forbidPrice;

}
