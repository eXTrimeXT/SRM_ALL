package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.order;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.order.MqlAuctSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.init.MqlAuctSouItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderItemResultVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价 MQL - 报价结果列表查询
 * PS: 参考 {@link MqlSouOrderItemResultVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouOrderItemResultVO extends MqlAuctSouOrderItemDTO {

    @ApiModelProperty("报价单")
    private SouOrder souOrder;
    @ApiModelProperty("供应商")
    private SouVendor souVendor;
    @ApiModelProperty("物料需求")
    private MqlAuctSouItemVO souItem;

}
