package com.midea.cloud.srm.model.pj.pricetax.dto;

import lombok.Data;

/**
 * @author huangbf3
 */
@Data
public class ResultPriceDto {

    /** 返回码 */
    private Integer code;

    /** 返回数据 */
    private ResultInfoDto result;

    /** 返回消息 */
    private String message;

}
