package com.midea.cloud.srm.mall.result;


import com.midea.cloud.srm.mall.common.ResultCode;

public class MallResult<TResultCode extends ResultCode, TData> {

    public TData data;

    public TResultCode resultCode;

    public TData getData() {
        return data;
    }

    public TResultCode getResultCode() {
        return resultCode;
    }

    public void setData(TData data) {
        this.data = data;
    }

    public void setResultCode(TResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
