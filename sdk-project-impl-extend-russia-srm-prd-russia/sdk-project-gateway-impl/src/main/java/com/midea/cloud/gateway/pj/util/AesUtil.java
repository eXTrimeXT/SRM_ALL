package com.midea.cloud.gateway.pj.util;


import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public class AesUtil {
    @Value("${secret:ktSrVIWD76gglSws}")
    public static String sKey = "ktSrVIWD76gglSws";

    public static void main(String[] args) {
        String sSrc= "344082693703808";
//        String sKey = "ktSrVIWD76gglSws";

        try {
            String s = AesUtil.aesEnCode(sSrc, sKey);
            log.info(s);
            log.info(AesUtil.aesDeCode(s, sKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加密
     * @param sSrc
     * @return
     */
    public static String aesEnCode(String sSrc) {
        return AesUtil.aesEnCode(sSrc, sKey);
    }

    /**
     * 加密
     * @param sSrc
     * @param sKey
     * @return
     */
    public static String aesEnCode(String sSrc, String sKey) {
        AES aes = new AES(Mode.CFB, Padding.NoPadding,
                new SecretKeySpec(sKey.getBytes(), "AES"),
                new IvParameterSpec(sKey.getBytes()));
        String aesContent = aes.encryptHex(sSrc);
        return aesContent;
    }

    /**
     * 解密
     * @param sSrc
     * @return
     */
    public static String aesDeCode(String sSrc){
        return AesUtil.aesDeCode(sSrc, sKey);
    }

    /**
     * 解密
     * @param sSrc
     * @param sKey
     * @return
     */
    public static String aesDeCode(String sSrc, String sKey) {
        AES aes = new AES(Mode.CFB, Padding.NoPadding,
                new SecretKeySpec(sKey.getBytes(), "AES"),
                new IvParameterSpec(sKey.getBytes()));
        String aesContent = aes.decryptStr(sSrc);
        return aesContent;
    }



}
