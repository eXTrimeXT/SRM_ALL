package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order.MqlSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init.MqlSouItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MQL - 报价明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderItemVO extends MqlSouOrderItemDTO {

    @ApiModelProperty("物料需求")
    private SouItem souItem;

}
