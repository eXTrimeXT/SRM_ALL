package com.midea.cloud.common.pj.utils;


import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 * @author huangbf3
 */
@Slf4j
public class AesUtil {
    public static final String S_KEY = "ktSrVIWD76gglSws";

    public static void main(String[] args) {
        String sSrc= "344082693703808";

        try {
            String s = AesUtil.aesEnCode(sSrc, S_KEY);
            log.info(s);
            log.info(AesUtil.aesDeCode(s, S_KEY));
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
        return AesUtil.aesEnCode(sSrc, S_KEY);
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
        return AesUtil.aesDeCode(sSrc, S_KEY);
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