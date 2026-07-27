package com.midea.cloud.gateway.pj.util;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public abstract class AbstractPjLockUtil {

    @Autowired
    protected RedisUtil redisUtil;

    /**
     * businessType
     * @return
     */
    protected abstract String businessType();

    /**
     * @return
     */
    protected long keyTimeout() {
        long second = 3600 * 24 * 30 * 12L;
        return second;
    }

    public String getPublicKey() {
        String publicRedis = businessType() + PjCommon.SECRET_SUFFIX_PUBLIC;
        String publicKey = redisUtil.get(publicRedis);
        if (StringUtils.isNotBlank(publicKey)) {
            return publicKey;
        }
//
        return generateKey(publicRedis);
    }

    public String getPrivateKey() {
        String privateRedis = businessType() + PjCommon.SECRET_SUFFIX_PRIVATE;
        String privateKey = redisUtil.get(privateRedis);
        if (StringUtils.isNotBlank(privateKey)) {
            return privateKey;
        }
//
        return generateKey(privateRedis);
    }

    public void clearKey() {
        String publicRedis = businessType() + PjCommon.SECRET_SUFFIX_PUBLIC;
        String privateRedis = businessType() + PjCommon.SECRET_SUFFIX_PRIVATE;
        redisUtil.del(publicRedis);
        redisUtil.del(privateRedis);
    }

    private String generateKey(String valueKey) {
        String businessType = businessType();
        String lockKey = businessType + PjCommon.SECRET_SUFFIX_LOCK;
        Boolean isLock = redisUtil.tryLock(lockKey, 10);
        try {
            if (Boolean.TRUE.equals(isLock)) {
//
                String valueKeyData = redisUtil.get(valueKey);
                if (StringUtils.isNotBlank(valueKeyData)) {
                    return valueKeyData;
                }
                String[] arr = new String[0];
                try {
                    arr = genKeyPair(4096);
                } catch (Exception e) {
                    log.error("", e);
                    throw new BaseException("");
                }
                String privateKey = arr[0];
                String publicKey = arr[1];
                redisUtil.set(businessType + PjCommon.SECRET_SUFFIX_PRIVATE, privateKey, keyTimeout());
                redisUtil.set(businessType + PjCommon.SECRET_SUFFIX_PUBLIC, publicKey, keyTimeout());
                return redisUtil.get(valueKey);
            } else {
//
                throw new BaseException("");
            }
        } finally {
            redisUtil.unLock(lockKey);
        }
    }

    public String decrypt(String encryptStr) throws Exception {
        return decrypt(encryptStr, getPrivateKey());
    }

    public String encrypt(String str) throws Exception {
        String enc = encrypt(str, getPublicKey());
        if (enc != null && enc.length() > 0) {
            return new String(Base64.encodeBase64(enc.getBytes(StandardCharsets.UTF_8)), StandardCharsets.
                    UTF_8);
        }
        return null;
    }

    public static String[] genKeyPair(int keySize) throws Exception {
// KeyPairGeneratorRSA
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
// 96-1024
        // keyPair
        keyPairGen.initialize(keySize, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
// Map
        String[] keyPairs = new String[2];
// 0
        keyPairs[0] = Base64.encodeBase64String(privateKey.getEncoded());
// 1
        keyPairs[1] = Base64.encodeBase64String(publicKey.getEncoded());
        return keyPairs;
    }

    /**
     * RSA
     *
     * @param str
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String str, String publicKey) throws Exception {
//base64
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec
                (decoded));
//RSA
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        if (cipher != null) {
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
            return outStr;
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String mw = "WU1DQ0VGcjZIcm1LajZHR3JEMGlZRDFvSW9JQWRkZVRVTnh4SDJOVDNrSnljT0ZLeFJ6V1ZaOWoxNUJJdlgzaDVkN21BRDRiY1NhMzNuNWRIQ1JRNDRVSFN2VUNqVUhzRURzeU96d3dYMmJDN1grZldLNjUwbmlEOFFRQXU0M1lUaHRDeXdCOStPS2NJazh4LzN5eFo4dFBCYVZoMlgyZmp4V2dJL0JzTFdvb2I3MjFjdlRxdEhmekp4MjdVdEhINjZPZE52dmZmYXVxa1krR0M3QmV6N08xampmUlF1YUtZMEViQXgrSEhtanR0WTR2QUw0Y3k2akJoRFZoTW5mbUFoL1BxdUJEcEhBU2VNbEtieU1zRFRvTEx0MFNPMXZhdGpJNVBNUUJxU25jenFvSnZxeVZ4Y1pnSU42ZU4wcHB5YzhBcW9FWEhreEJXTXFMcHh5RUdScS93SUE3S29PWnBqYW0wRS9VS3lyYUEvcUtUbkxmeTl2YnExT21XK3BKZ1RmeXBmVFoyUmx4cUc5cTlSZUhVZHJvL25LZFRIUGVTTDFOSjNzcFh3TENxYVZWOWI4UzBLUGFlZ2J0dTFzWWs2Y2dsWUMrTmRBejBmWFQ0R3NlMEJqMWxYSmtKMFdscW0ya3RQYXJIdWUwUVNRVWxHbEZJdVFaeXNwTmVwSlRyM2EyWlBOT1YvanFjUytrK0daK0RnSXM0U09OOUhnbjY5SThYWGp2THVjWUpPVW94bU5jVlNRUTluTjN3UEs3VkZ0OFY0MTkzODBDZ1FlUHpIczZkUmZNZXJPVHc1T2sxK2J2VFVwSEpNb1Y2SlY2WXFpRHRDS0FaMEJlR0k1bDNwc2kxbVIwTTJuSEgxY3d6VzlmWnVxcnRaWjhYV1FIOU82OW8zcXN3UHc9";
        String privateKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA2sZKmoYloHQJ/3FoUsyiR/EsoiVzYY+nBNho9KkLYFcz4ex28SXwQsBdilsFUHIMjeRN+ddEJpxsMRzVztPMDVm9mephX7RA6gTYl6dghqBAK0NghvnfkpR88Kvk4r5pw1sGyhfiV8aVVRCp+xYtzQipyT+Vuiyy4bYjtXIz4dRwv4CZhIoIGo8DEw50MnYXOAFCXu/WpSdnLA6+fWlFzZ3rE0Kuzf4ryKAfb1fETokF11JdhgU7HYWJzcOqdhJ20Q6TJZ3gwSfDeqAN3YvD3kHukkRhGDQrS0RhM8s/yerU9CEwhugxDiZTREnkm7XcIe9QHZwN8uA6PUkbLGCQPS+yGjasusYxax5fQ0non6I7gx6PsjzWSOSytxpEDzVp3ggPfYGbv+mJwZSPwAWNSsZfdFbuRSw4wKkGx9N12QsUEcMepCFH79tmVRW55gr6FhzRghqxcGDe2wvBN6jtCtJkoTFpG1O/ZJ59P2gBLJhIAEmNygApfwDorTSHLYEuCwrlLjivbOrDKIQvPb5niJWayOLfP5pPqmWVC6bD9V05Q1NLxZJ/ke2GeDjcxQ0SF8v37MMLnwZJd2nBHgHGNZ5+8kFxIsx3Nxn0MGu4K+KKUDdjZq47mEWkIxId/ZaJjDbI1dWzqVCFb7SahcdgXhf725E5+HnnY96pLrmIfwsCAwEAAQ==";
        log.info(decrypt("U2FsdGVkX18wTR2/CRl6gDXGyJcrID3thWm+FKBSj74=",privateKey));
//        log.info(encrypt("U2FsdGVkX18WTR2/CR16gDXGyJcrID3thWm+FKBSj74=",privateKey));
    }

    /**
     * RSA
     *
     * @param str
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String str, String privateKey) throws Exception {
//64
        byte[] inputByte = Base64.decodeBase64(Base64.decodeBase64(str));
// byte[] inputByte = Base64.decodeBase64(str);
//base64
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new
                PKCS8EncodedKeySpec(decoded));
//RSA
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }
}
