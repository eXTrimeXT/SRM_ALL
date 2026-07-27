package com.midea.cloud.srm.mall.request.base;


import com.midea.cloud.srm.mall.common.ResultCode;

public class DefaultResultCode implements ResultCode {
    private String code;

    private String message;

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
