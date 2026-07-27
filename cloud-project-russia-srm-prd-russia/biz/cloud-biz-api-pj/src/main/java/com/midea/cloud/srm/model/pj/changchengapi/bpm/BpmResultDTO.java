package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 * BPM接口返回结果
 */
@Data
public class BpmResultDTO<T> {

    @ApiModelProperty("响应码")
	private Integer code;

    @ApiModelProperty("业务编码")
	private String key;

    @ApiModelProperty("处理信息")
	private String message;

    @ApiModelProperty("返回数据")
	private T data;
}
