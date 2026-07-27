package com.midea.cloud.srm.model.sou.bidturns.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: panmq
 * @Date: 2024/04/07/ $
 * @Description: 招标负责人转办请求参数
 */
@ApiModel("招标负责人转办请求参数")
@Data
public class NpmSouBidTurnRquestParamDto extends BaseDTO {

    @ApiModelProperty("招标单ID")
    private Long projectId;

    @ApiModelProperty("转办账号")
    private String userName;

    @ApiModelProperty("转办ID")
    private Long userId;

    @ApiModelProperty("转办名字")
    private String fullName;
}
