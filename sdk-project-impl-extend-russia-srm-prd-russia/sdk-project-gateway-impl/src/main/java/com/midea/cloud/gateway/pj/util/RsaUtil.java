package com.midea.cloud.gateway.pj.util;

import com.midea.cloud.common.exception.BaseException;
import com.mideacloud.common.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * <pre>
 *  IAM password 加密工具类
 * @author huangbf3
 * </pre>
 */
@Slf4j
public class RsaUtil {

    public static final String ALG_RSA_PKCS1 = "RSA/ECB/PKCS1Padding";

    private static final String KEY_FACTORY_RSA = "RSA";

    private final PublicKey publicKey;

    private final PrivateKey privateKey;

    private final String rsaAlg;

    public static RsaUtil instance(@NonNull String privateKey, @NonNull String publicKey) {
        return new RsaUtil(privateKey, publicKey);
    }

    public static RsaUtil instanceByPrivateKey(@NonNull String privateKey) {
        return new RsaUtil(privateKey, null);
    }

    public static RsaUtil instanceByPublicKey(@NonNull String publicKey) {
        return new RsaUtil(null, publicKey);
    }

    public RsaUtil(String privateKey, String publicKey) {
        this(privateKey, publicKey, ALG_RSA_PKCS1);
    }

    public RsaUtil(String privateKey, String publicKey, String rsaAlg) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_RSA);
            if (StringUtils.isNotBlank(publicKey)) {
                X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.standardBase64().decode(publicKey));
                this.publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            } else {
                this.publicKey = null;
            }
            if (StringUtils.isNotBlank(privateKey)) {
                PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.standardBase64().decode(privateKey));
                this.privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            } else {
                this.privateKey = null;
            }
            this.rsaAlg = rsaAlg;
        } catch (Exception e) {
            throw new BaseException("解析密钥错误({0})", e.getMessage());
        }
    }

    public String decode(String encodedByPublicKey) {
        try {
            if (privateKey != null) {
                Cipher cipher = Cipher.getInstance(rsaAlg);
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] result = cipher.doFinal(Base64.standardBase64().decode(encodedByPublicKey));
                return new String(result, StandardCharsets.UTF_8);
            } else {
                throw new BaseException("没有私钥可用");
            }
        } catch (Exception e) {
            log.error("!!! 解密错误，key:{}", encodedByPublicKey, e);
            throw new BaseException("认证失败");
        }
    }

    public String encode(String rawString) {
        try {
            if (publicKey != null) {
                Cipher cipher = Cipher.getInstance(rsaAlg);
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] result = cipher.doFinal(rawString.getBytes(StandardCharsets.UTF_8));
                return Base64.standardBase64().encode(result);
            } else {
                throw new BaseException("没有公钥可用");
            }
        } catch (Exception e) {
            throw new BaseException("加密错误", e.getMessage());
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

}
