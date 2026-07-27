package com.midea.cloud.srm.model.pj.ccapisettleacountings.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/04/09/ $
 * @Description:
 */
@Data
@ApiModel("结算记账-请求")
public class ApiSettleAcountingRequestDto {

    @ApiModelProperty("请求头")
    private ApiSettleAcountingRequestHead header;

    @ApiModelProperty("结算行")
    private List<ApiSettleAcountingRequestItems> items;


}
