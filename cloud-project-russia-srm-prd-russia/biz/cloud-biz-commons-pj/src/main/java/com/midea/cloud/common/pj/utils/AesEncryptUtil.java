package com.midea.cloud.common.pj.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.UUID;

/**
 * @author huangbf3
 */
@Slf4j
public class AesEncryptUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ALGORITHM_STR = "AES/ECB/PKCS7Padding";

    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    public static String encrypt(String data, String key) {
        byte[] keyBytes = convertKey(key);
        byte[] crypted = null;
        try {
            SecretKeySpec sKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
            cipher.init(Cipher.ENCRYPT_MODE, sKey);
            crypted = cipher.doFinal(data.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return new String(Base64.encodeBase64(crypted));
    }

    /**
     * 解密
     *
     * @param data
     * @param key
     * @return
     */
    public static String decrypt(String data, String key) {
        byte[] keyBytes = convertKey(key);
        byte[] output = null;
        try {
            SecretKeySpec sKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
            cipher.init(Cipher.DECRYPT_MODE, sKey);
            output = cipher.doFinal(Base64.decodeBase64(data));
            return new String(output);
        } catch (Exception e) {
            log.info("当前密码为非AES加密,密码为:{}", data);
        }
        return UUID.randomUUID().toString();
    }

    /**
     * 转换密钥，如果长度小16位，byte数组后面补0，如果大于16位，提示报错
     *
     * @param key
     * @return
     */
    private static byte[] convertKey(String key) {
        //兼容前端加密仅在16位时生效
        int length = 16;
        if (key.length() > length) {
            key = key.substring(0, length);
        } else {
            key = String.format("%-16s", key).replace(' ', '0');
        }
        byte[] keyBytes = key.getBytes();
        if (keyBytes.length <= length) {
            byte[] temp = new byte[length];
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        } else {
            throw new RuntimeException("仅支持16及16位长度以内密钥");
        }
        return keyBytes;
    }

    public static String appendEncryptStr(String account, String platformCode) {
        long now = System.currentTimeMillis();
        return account + "\n"
                + platformCode + "\n"
                + now;
    }

    public static void main(String[] args) {
        //加密的secrete
        String key = "c2dc44f3b9401ebfa39acc14eda2f07e";
        //平台码
        String platformCode = "8085d502830fde98330fc4ee1c47d518";
        String encrypted = AesEncryptUtil.encrypt(platformCode, key);
        log.info("加密后数据: " + encrypted);
    }

}