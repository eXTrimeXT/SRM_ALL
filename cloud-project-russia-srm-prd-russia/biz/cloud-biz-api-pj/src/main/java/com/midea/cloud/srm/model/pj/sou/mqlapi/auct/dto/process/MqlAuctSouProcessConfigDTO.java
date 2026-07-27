package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.process;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.process.MqlSouProcessConfigDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价MQL - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouProcessConfigDTO extends MqlSouProcessConfigDTO {

    @ApiModelProperty("竞价流程配置")
    private AuctSouProcessConfig auctProcessConfig;

}
