package com.midea.cloud.gateway.pj.controller;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.autoconfigure.SrmGlobalProperties;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IDassClient;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.gateway.pj.dto.UserLoginDTO;
import com.midea.cloud.gateway.pj.util.AbstractPjLockUtil;
import com.midea.cloud.gateway.pj.util.PjUserLockUtil;
import com.midea.cloud.gateway.pj.util.RsaUtil;
import com.midea.cloud.gateway.security.LoginLog;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.rbac.iam.IamLoginInfo;
import com.midea.cloud.srm.model.rbac.role.entity.Role;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * <pre>
 *  二开gateway
 * </pre>
 *
 * @author chenjs107@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/16 11:08
 *  修改内容:
 * </pre>
 */
@Slf4j
@Api(value = "PjTokenController", tags = {"供应商登录"})
@RestController
@RequestMapping("/sys")
public class PjTokenController {

    @Value("${srm.url.iam}")
    private String iamUrl;

    @Value("${srm.iam.client.id}")
    private String clientId;

    @Value("${srm.iam.client.secret}")
    private String clientSecret;

    @Value("${cloud.scc.useSecureCookie}")
    private boolean useSecureCookie;

    @Value("${global.srm.register-address}")
    private String srmUrl;

    @Resource
    private SrmGlobalProperties srmGlobalProperties;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RbacClient rbacClient;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private IDassClient iDassClient;

    @Resource
    private BaseClient baseClient;

    @Resource
    private LoginLog loginLog;
    private final static String ZERO = "0";
    private final static String CODE = "code";

    private final static String BUYER = "BUYER";

    @Autowired
    private PjUserLockUtil pjUserLockUtil;
    @Autowired
    private AbstractPjLockUtil abstractPjLockUtil;

    /**
     * 获取登录密码加密的公钥
     * @return
     */
    @GetMapping("/pj/pjPublicKey")
    public String publicKey(){
        return pjUserLockUtil.getPublicKey();
    }


    @PostMapping("/pj/iam/login")
    public Map<String, Object> pjSysLogin(@RequestBody UserLoginDTO userLoginDTO) throws Exception {
        HttpServletResponse response = HttpServletHolder.getResponse();
        HttpServletRequest request = HttpServletHolder.getRequest();

        if(userLoginDTO == null || StringUtils.isBlank(userLoginDTO.getUsername()) || StringUtils.isBlank(userLoginDTO.getPassword())) {
            throw new BaseException("用户名或密码错误");
        }

        LoginAppUser loginAppUser = rbacClient.findByUsername(userLoginDTO.getUsername());
        if(loginAppUser == null){
            throw new BaseException("用户名或密码错误");
        }

        userLoginDTO.setPassword(abstractPjLockUtil.decrypt(userLoginDTO.getPassword()));
        log.info("password:"+userLoginDTO.getPassword());

        /** 外部供应商登陆的时候判断一下是不是内部的账号 如果是禁止登陆
           *采购商（内部）：meicloud_usrm_cloud_rbac.scc_rbac_user.USER_TYPE = BUYER
           *供应商（外部）：meicloud_usrm_cloud_rbac.scc_rbac_user.USER_TYPE = VENDOR
           * 角色是 CADMIN SystemAdmin 也可以登录
           *     长城SRM管理员  系统管理员
         */
        if(BUYER.equals(loginAppUser.getUserType())){
            Boolean administrators = false;
            String paramKey = "BUYER_VENDOR_LOGIN";
            SystemConfigureDTO systemConfigureDTO = baseClient.getSystemConfigure(paramKey);
            if(systemConfigureDTO!=null&&StringUtils.isNotBlank(systemConfigureDTO.getParamValue())){
                String[] paramValueArr = systemConfigureDTO.getParamValue().split(";");
                Set<String> paramValueSet = new HashSet<>(Arrays.asList(paramValueArr));

                List<Role> roles = loginAppUser.getRolePermissions();

                for(int i = 0 ; i < roles.size(); i ++){
                    String roleCode = roles.get(i).getRoleCode();
                    if(paramValueSet.contains(roleCode)){
                        administrators = true;
                        break;
                    }
                }
            }
            if(!administrators){
                throw new BaseException("是内部的账号 禁止登陆");
            }
        }


        //获取IAM accesstoken
        String accessToken = getIamAccessToken();
        //获取IAM公钥, 加密密码
        String publicKey = getIamPublicKey(accessToken);
        RsaUtil rsaUtil = RsaUtil.instanceByPublicKey(publicKey);
        String encodedPassword = rsaUtil.encode(userLoginDTO.getPassword());
        //获取IAM token
        String token = getIamAuthToken(userLoginDTO.getUsername(), encodedPassword, accessToken);

        //构建 loginInfo
        String code = String.valueOf(IdGenrator.generate());
        IamLoginInfo iamLoginInfo = new IamLoginInfo();
        iamLoginInfo.setUserName(userLoginDTO.getUsername());
        iamLoginInfo.setIdmToken(token);
        iamLoginInfo.setKeyVersion(code);

        Map<String, Object> resultMap = new HashMap<>(50);
        resultMap.put("username", userLoginDTO.getUsername());
        log.info("获取到的登录人的登录信息=={}", JSONObject.toJSONString(loginAppUser));
        iamLoginInfo.setUserType(loginAppUser.getUserType());
        resultMap.put("userType", iamLoginInfo.getUserType());

        //前端获取Bearer Token的cookie
        Cookie keyToken = new Cookie("KeyToken", iamLoginInfo.getIdmToken());
        //前端获取Bearer Token的cookie
        Cookie iamVersion = new Cookie("iamVersion", iamLoginInfo.getKeyVersion());
        //单点登录标志, 前端识别用
        Cookie entrance = new Cookie("entrance", "singlePoint");

        String currentContextPath = srmGlobalProperties.getGlobalCookiePath();
        keyToken.setPath(currentContextPath);
        iamVersion.setPath(currentContextPath);
        entrance.setPath(currentContextPath);
        response.addCookie(keyToken);
        response.addCookie(iamVersion);
        response.addCookie(entrance);

        redisUtil.set(IDassClient.IAM_CODE_KEY + code, iamLoginInfo.getIdmToken(), iDassClient.getInitTime());
        redisUtil.set(IDassClient.IAM_TOKEN_KEY + iamLoginInfo.getIdmToken(), code, iDassClient.getInitTime());
        redisUtil.set(IDassClient.IAM_USERNAME_KEY + iamLoginInfo.getIdmToken(), iamLoginInfo.getUserName(), iDassClient.getInitTime());
        log.info("要写入的登录信息=={}", JSONObject.toJSONString(iamLoginInfo));
        loginLog.saveUserTrace(iamLoginInfo.getUserName(), iamLoginInfo.getUserType());

        return resultMap;
    }

    private void exchangeTokenDeal(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.SC_MOVED_TEMPORARILY);
        response.setHeader("location", srmUrl + "/#/noUser");
        response.getWriter().close();
    }

    public String getIamAccessToken() {
        JSONObject resultObject = null;
        try {
            String url = iamUrl + "/iam/auth/api/v1.2/accesstoken";
            Map params = new HashMap(50);
            params.put("appKey", clientId);
            params.put("appSecret", clientSecret);
            ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(new URI(url), params, JSONObject.class);
            resultObject = responseEntity.getBody();
            if (null != resultObject && ZERO.equals(resultObject.getString(CODE))) {
                String accessToken = resultObject.getJSONObject("data").getJSONObject("ticket").getString("value");
                return accessToken;
            }
        } catch (Exception e) {
            log.error("IAM获取accessToken异常", e);
            throw new BaseException("获取accessToken异常");
        }
        log.error("IAM获取accessToken失败, 返回结果: {}", resultObject);
        throw new BaseException("获取accessToken失败");
    }

    public String getIamPublicKey(String authorization) {
        JSONObject resultObject = null;
        try {
            String url = iamUrl + "/iam/auth/api/v1.2/publickey";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "bearer " + authorization);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(null, headers);
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, JSONObject.class);
            resultObject = responseEntity.getBody();
            if (null != resultObject && ZERO.equals(resultObject.getString(CODE))) {
                String publicKey = resultObject.getJSONObject("data").getString("publicKey");
                return publicKey;
            }
        } catch (Exception e) {
            log.error("IAM获取publicKey异常", e);
            throw new BaseException("获取publicKey异常");
        }
        log.error("IAM获取publicKey失败, 返回结果: {}", resultObject);
        throw new BaseException("获取publicKey失败");
    }

    public String getIamAuthToken(String username, String password, String authorization) {
        JSONObject resultObject = null;
        try {

            String url = iamUrl + "/iam/auth/api/v1.2/ticket/auth/password";
            Map params = new HashMap(50);
            params.put("userName", username);
            params.put("password", password);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "bearer " + authorization);
            org.springframework.http.HttpEntity<Map> entity = new org.springframework.http.HttpEntity<>(params, headers);
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, JSONObject.class);
            resultObject = responseEntity.getBody();
            if (null != resultObject && ZERO.equals(resultObject.getString(CODE))) {
                String token = resultObject.getJSONObject("data").getJSONObject("ticket").getString("value");
                return token;
            }
        } catch (Exception e) {
            log.error("IAM认证异常", e);
            throw new BaseException("IAM认证异常");
        }
        log.error("IAM认证失败, 返回结果: {}", resultObject);
        throw new BaseException("用户名或密码错误");
    }

}
