package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.dto.init;

import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouProjectEditDTO extends SouProject {

    @ApiModelProperty("招投标拓展数据")
    private BidSouProject bidSouProject;

}
