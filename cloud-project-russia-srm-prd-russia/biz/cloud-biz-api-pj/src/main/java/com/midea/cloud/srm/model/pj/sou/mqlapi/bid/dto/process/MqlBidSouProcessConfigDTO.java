package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.dto.process;

import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.process.MqlSouProcessConfigDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/04/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouProcessConfigDTO extends MqlSouProcessConfigDTO {

    @ApiModelProperty("招标拓展表")
    private BidSouProcessConfig bidSouProcessConfig;

}
