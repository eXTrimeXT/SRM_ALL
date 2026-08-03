package com.midea.cloud.srm.biz.pj.logistics.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangbf3
 */
@Data
public class FailMessage implements Serializable {

    private String message;
    private Boolean result;
    private String returnCode;

    public FailMessage() {
        this.message = "查询无结果，请隔段时间再查";
        this.result = false;
        this.returnCode = "500";
    }
}
