package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价MQL - 寻源基础信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouProjectEditDTO extends SouProject {

    @ApiModelProperty("竞价拓展数据")
    private AuctSouProject auctSouProject;

}
