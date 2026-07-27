package com.midea.cloud.srm.feign.pj.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * <pre>
 *  返参基类
 * </pre>
 *
 * @author huanghb14@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-2-11 16:05
 *  修改内容:
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaasResult<T> implements Serializable {

    private String code;
    private String message;
    private T data;
    private String errorMsgTrace;
    /**
     * 调用链跟踪ID
     */
    private String traceId;

    public static<T> PaasResult buildSuccess() {
        PaasResultCode resultCode = PaasResultCode.SUCCESS;
        PaasResult result = new PaasResult(resultCode.getCode(), resultCode.getMessage(), "", "", getTraceId());
        return result;
    }

    public static<T> PaasResult buildSuccess(T t) {
        PaasResultCode resultCode = PaasResultCode.SUCCESS;
        PaasResult result = new PaasResult(resultCode.getCode(), resultCode.getMessage(), t, "", getTraceId());
        return result;
    }

    public static<T> PaasResult build(PaasResultCode code) {
        PaasResult result = new PaasResult(code.getCode(), code.getMessage(), null, "", getTraceId());
        return result;
    }

    public static<T> PaasResult build(PaasResultCode code, T t) {
        PaasResult result = new PaasResult(code.getCode(), code.getMessage(), t, "", getTraceId());
        return result;
    }

    public static<T> PaasResult build(PaasResultCode code, String message) {
        PaasResult result = new PaasResult(code.getCode(), message, null, "", getTraceId());
        return result;
    }

    public static<T> PaasResult build(PaasResultCode code, String message, T t) {
        PaasResult result = new PaasResult(code.getCode(), message, t, "", getTraceId());
        return result;
    }

    public static<T> PaasResult build(String code, String message, T t) {
        PaasResult result = new PaasResult(code, message, t, "", getTraceId());
        return result;
    }

    public static<T> PaasResult build(String code, String message) {
        PaasResult result = new PaasResult(code, message, null, "", getTraceId());
        return result;
    }

    public static<T> PaasResult build(String code, String message, String errorMsgTrace) {
        PaasResult result = new PaasResult(code, message, null, errorMsgTrace, getTraceId());
        return result;
    }

    public static<T> PaasResult build(String code, String message, T t, String errorMsgTrace) {
        PaasResult result = new PaasResult(code, message, t, errorMsgTrace, getTraceId());
        return result;
    }
    
    /**
     * 获取调用链跟踪ID
     * @return
     */
    public static String getTraceId() {
        String traceIdTemp = MDC.get("X-B3-TraceId");
        if( traceIdTemp==null ) {
        	traceIdTemp ="";
        }
        return traceIdTemp;
    }
    

}
