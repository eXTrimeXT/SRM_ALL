package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.bond;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouVendorBond;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.init.MqlAuctSouProjectVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价 MQL - 供应商保证金
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouVendorBondVO extends AuctSouVendorBond {

    @ApiModelProperty("寻源单信息")
    private MqlAuctSouProjectVO souProject;

}
