package com.midea.cloud.srm.model.pj.extapis.cmscloud.enums;

/**
 * @Author: panmq
 * @Date: 2024/03/04/ $
 * @Description: 财务共享-付款结果回推-结果枚举
 */
public enum CmscloudResultEnum {
    /**
     * 处理成功
     */
    SUCCESS("200", "处理成功")
    ;

    private String code;

    private String msg;

    private CmscloudResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
