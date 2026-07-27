package com.midea.cloud.srm.model.supcooperate.historyprices.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/03/28/ $
 * @Description:
 */
@ApiModel("历史价格接口请求体")
@Data
public class HistoryPriceApiRequestDto extends BaseDTO {

    @ApiModelProperty("请求序号，唯一标识")
    private String serialNum;

    @ApiModelProperty("系统来源，非必填")
    private String systemSource;

    @ApiModelProperty("历史价格数据列表，必填")
    private List<SccScHistoryPriceTempDto> data;
}
