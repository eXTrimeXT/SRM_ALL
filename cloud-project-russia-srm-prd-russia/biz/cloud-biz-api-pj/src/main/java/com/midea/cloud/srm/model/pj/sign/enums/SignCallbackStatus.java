package com.midea.cloud.srm.model.pj.sign.enums;

import com.alibaba.excel.util.StringUtils;
import lombok.Data;

/**
 * 契约锁回传状态
 *
 * @author huangbf3@midea.com
 * @since 2022/06/16
 */
public enum SignCallbackStatus {


    /**
     * 备注
     */
    USING("USING","表示用印中", "using"),
    COMPLETE("COMPLETE","表示正常结束", "complete"),;

    private String status;
    private String name;

    private String method;

    SignCallbackStatus(String status, String name, String method){
        this.status = status;
        this.name = name;
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 根据状态获取回调方法名
     * @param status
     * @return
     */
    public static String getMethodByStatus(String status) {
        if(StringUtils.equals(SignCallbackStatus.USING.getStatus(),status)){
            return SignCallbackStatus.USING.getMethod();
        }else if(StringUtils.equals(SignCallbackStatus.COMPLETE.getStatus(),status)){
            return SignCallbackStatus.COMPLETE.getMethod();
        }
        return null;
    }
}
