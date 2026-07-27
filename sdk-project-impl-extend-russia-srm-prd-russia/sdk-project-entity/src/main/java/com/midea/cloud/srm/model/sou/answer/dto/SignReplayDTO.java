package com.midea.cloud.srm.model.sou.answer.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "供应商-签署回复")
@Data
@EqualsAndHashCode(callSuper=false)
public class SignReplayDTO implements Serializable {
    /**
     * 签署地址
     */
    @ApiModelProperty(value = "签署地址")
    private String signUrl;
    /**
     * 返回对象
     */
    @ApiModelProperty(value = "返回对象")
    private ReplayDTO replayDTO;
}
