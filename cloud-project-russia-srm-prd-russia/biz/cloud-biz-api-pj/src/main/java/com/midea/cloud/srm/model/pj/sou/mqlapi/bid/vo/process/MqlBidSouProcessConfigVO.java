package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.process;

import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招投标MQL - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouProcessConfigVO extends SouProcessConfig {

    @ApiModelProperty("招标拓展表")
    private BidSouProcessConfig bidSouProcessConfig;

}
