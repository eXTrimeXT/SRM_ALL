package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.select;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.init.MqlAuctSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.order.MqlAuctSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.select.MqlSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价 MQL - 评选列表信息
 * PS: 参考 {@link MqlSouSelectQueryVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouSelectQueryVO extends MqlAuctSouOrderItemVO {

    @ApiModelProperty("汇率")
    private BigDecimal priceTax;

    @ApiModelProperty("寻源单")
    private MqlAuctSouProjectVO souProject;
    @ApiModelProperty("供应商")
    private SouVendor souVendor;

}
