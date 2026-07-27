package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.select;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select.MqlSouOrderReportQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价 MQL - 获取报价报表信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouOrderReportQueryDTO extends MqlSouOrderReportQueryDTO {

    @ApiModelProperty("轮次")
    private Integer round;

}
