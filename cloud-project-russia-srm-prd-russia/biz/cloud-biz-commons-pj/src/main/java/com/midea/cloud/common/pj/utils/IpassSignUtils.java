package com.midea.cloud.common.pj.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Author: panmq
 * @Date: 2024/03/11/ $
 * @Description: IPASS签名工具类
 */
@Slf4j
public class IpassSignUtils {

    /**
     * 获取 Authorization
     * @param appCode：应用编码
     * @param privateKey：秘钥
     * @return
     */
    public static String getSign(String appCode, String privateKey) {
        return "Bearer " + base64Encode(String.format("%s:%s", appCode, privateKey));
    }

    /**
     * base64加密
     * @param var0
     * @return
     */
    public static String base64Encode(String var0) {
        Base64.Encoder var1 = Base64.getEncoder();
        byte[] var2 = var0.getBytes(StandardCharsets.UTF_8);
        return var1.encodeToString(var2);
    }

    public static void main(String[] args) {
        /** 生成Authorization 演示代码 */
        log.info("Authorization: " + getSign("bxKmD2", "qNmW2KuWIDBMBmwt4Wza8iLq2C7w80YK"));
    }
}
