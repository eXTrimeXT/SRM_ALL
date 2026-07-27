package com.midea.cloud.srm.mall.utils;

import com.midea.cloud.srm.mall.common.ResultCode;
import com.midea.cloud.srm.mall.request.base.DefaultResultCode;
import com.midea.cloud.srm.mall.result.MallResult;

@SuppressWarnings("unchecked")
public class ResultUtils {

    public static <TResultCode extends ResultCode, TData> MallResult<TResultCode, TData> buildResult(String code, TData data) {
        DefaultResultCode resultCode = new DefaultResultCode();
        resultCode.setCode(code);
        MallResult mallResult = new MallResult();
        mallResult.setResultCode(resultCode);
        mallResult.setData(data);
        return mallResult;
    }

    public static <TResultCode extends ResultCode, TData> MallResult<TResultCode, TData> buildResult(String code, TData data, String message) {
        DefaultResultCode resultCode = new DefaultResultCode();
        resultCode.setCode(code);
        resultCode.setMessage(message);
        MallResult mallResult = new MallResult();
        mallResult.setResultCode(resultCode);
        mallResult.setData(data);
        return mallResult;
    }

}
