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
@ApiModel("结算记账-结算行-附件列表")
public class ApiSettleAcountingRequestItemsAttach {
    @ApiModelProperty("附件名称")
    private String attachName;
    @ApiModelProperty("附件链接")
    private String attachUrl;
}
