package com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo;

import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Data
public class RiskResponse<T> {

    private String code;

    private T data;

    public RiskResponse() {

    }

    public RiskResponse(T data) {
        this.data = data;
    }

    public RiskResponse(String code, T data) {
        this.code = code;
        this.data = data;
    }
}
