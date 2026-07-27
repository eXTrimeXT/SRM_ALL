package com.midea.cloud.srm.model.sou.bpmtodo.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/5/31
 */
@Data
@ApiModel("招标流程手机待办请求参数")
public class SouBpmtodoParam extends BaseDTO {

    /**
     * 招标单ID
     */
    @ApiModelProperty("招标单ID")
    private Long projectId;

    /**
     * 轮次
     */
    @ApiModelProperty("轮次")
    private Integer round;
}
