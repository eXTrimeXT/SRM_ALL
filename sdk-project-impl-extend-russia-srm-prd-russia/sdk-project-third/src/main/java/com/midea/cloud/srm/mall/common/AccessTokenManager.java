package com.midea.cloud.srm.mall.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.mall.config.UriPropertiesConfiguration;
import com.midea.cloud.srm.mall.result.jd.common.AccessTokenResultDTO;
import com.midea.cloud.srm.mall.utils.SrmHttpRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/2/17 14:36
 *  修改内容:
 * </pre>
 */
@Component
public class AccessTokenManager {
    @Autowired
    private UriPropertiesConfiguration uriPropertiesConfiguration;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SrmHttpRequestUtil srmHttpUtil;

    public String getAccessToken() {
        String accessToken = redisUtil.get(Constant.JD_ACCESS_TOKEN);
        if (accessToken == null) {
            String refreshToken = redisUtil.get(Constant.JD_REFRESH_TOKEN);
            if (refreshToken != null) {
                accessToken = fetchAccessToken(refreshToken);
            } else {
                accessToken = loginAndGetAccessToken();
            }
        }
        return accessToken;
    }

    private String fetchAccessToken(String refreshToken) {
            Map<String, Object> params = new HashMap<>(15);
            params.put("refresh_token",refreshToken);
            params.put("client_id",uriPropertiesConfiguration.getClientId());
            params.put("client_secret",uriPropertiesConfiguration.getClientSecret());
            AccessTokenResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getRefreshTokenUrl(), params,"application/x-www-form-urlencoded", AccessTokenResultDTO.class);
            if (resultDTO.isSuccess()) {
                String accessToken=resultDTO.getResult().getAccess_token();
                //更新accessToken
                redisUtil.del(Constant.JD_ACCESS_TOKEN);
                redisUtil.set(Constant.JD_ACCESS_TOKEN,accessToken, resultDTO.getResult().getExpires_in());
                //更新refreshToken
                redisUtil.del(Constant.JD_REFRESH_TOKEN);
                redisUtil.set(Constant.JD_REFRESH_TOKEN,resultDTO.getResult().getRefresh_token(), resultDTO.getResult().getRefresh_token_expires() / 1000);
                return accessToken;
            }else {
               return loginAndGetAccessToken();
            }
    }
    private String loginAndGetAccessToken() {
        try {
            Map<String, Object> params = new HashMap<>(15);
            params.put("grant_type","access_token");
            params.put("client_id",uriPropertiesConfiguration.getClientId());
            params.put("client_secret",uriPropertiesConfiguration.getClientSecret());
            params.put("timestamp",DateUtil.formatDateTime(DateUtil.date()));
            params.put("username","长城汽车平台采购账号");
            params.put("password",SecureUtil.md5(uriPropertiesConfiguration.getPassword()));
            String sign=this.getSign(params);
            params.put("sign",sign);
            AccessTokenResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getAccessTokenUrl(), params,"application/x-www-form-urlencoded", AccessTokenResultDTO.class);
            if (resultDTO.isSuccess()) {
                String accessToken=resultDTO.getResult().getAccess_token();
                redisUtil.del(Constant.JD_ACCESS_TOKEN);
                redisUtil.set(Constant.JD_ACCESS_TOKEN,accessToken,resultDTO.getResult().getExpires_in());
                redisUtil.del(Constant.JD_REFRESH_TOKEN);
                redisUtil.set(Constant.JD_REFRESH_TOKEN,resultDTO.getResult().getRefresh_token() , resultDTO.getResult().getRefresh_token_expires()  / 1000);
                return accessToken;
            }else {
                throw new RuntimeException("获取京东平台Access Token失败："+resultDTO.getResultMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("获取京东平台Access Token失败");
        }
    }

    private String getSign(Map<String, Object> params) {
        String sign=params.get("client_secret").toString()+params.get("timestamp")+params.get("client_id")+params.get("username")+params.get("password")+params.get("grant_type")+params.get("client_secret");
        return SecureUtil.md5(sign).toUpperCase();
    }
}
