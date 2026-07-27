package com.midea.cloud.component.aop.verify;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.constants.SysConstant;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.IPUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.component.service.BasePermissionService;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
@Aspect
@Component
@Slf4j
public class RepeatSubmitHandler {

    @Resource
    private RedisUtil redisUtil;

    @Value("${verify.repeat.submit:false}")
    private Boolean verifySubmit;

    @Value("${srm.file.upload.whitelist:${file.upload.whitelist:}}")
    private String fileTypeWhitelist;

    private static final int TIMEOUT = 2;

    private static final String GET_METHOD = "GET";

    private static final List<String> PERMIT_ALL_URL = Arrays.asList("");

    private static final String DEFAULT_FILETYPE_WHITELIST = "jpg/jpeg/png/xls/xlsx/doc/docx/ppt/pptx/pdf/cad/ofd/wps/rar/zip/gzip/ico/svg/md/tif/tiff";

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String POINT = ".";

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controller() {
        // arround 方法中使用
    }

    private String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    private String getBody(Object[] args) {
        //增、删、改、查只有增加不允许重复提交，其它方法进行多次不会造成影响
        try {
            return JSON.toJSONString(args);
        } catch (Exception e) {
            //do nothing;
        }

        return "";
    }

    private String getHashKey(String method, String url, String body) {
        String source = method + url + body;
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        if (loginAppUser != null) {
            source = source + loginAppUser.getUserId();
        }

        String digest = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            digest = Base64.getEncoder().encodeToString(md.digest(source.getBytes()));
        } catch (Exception e) {
            //do nothing
            log.error("", e);
        }

        return digest;
    }

    @Around("controller()")
    public Object arround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();
            if (Objects.nonNull(request) && Objects.nonNull(response)) {
                String contentType = request.getContentType();
                if (StringUtils.isNotBlank(contentType)) {
                    if (contentType.contains(MULTIPART_FORM_DATA)) {
                        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                        // 获取注解
                        Method method = signature.getMethod();
                        // 获取方法上的参数
                        Parameter[] parameters = method.getParameters();

                        String whitelist = "";
                        if (StringUtils.isBlank(fileTypeWhitelist)) {
                            whitelist = DEFAULT_FILETYPE_WHITELIST;
                        } else {
                            whitelist = fileTypeWhitelist;
                        }

                        for (Parameter parameter : parameters) {
                            // 参数名
                            String name = parameter.getName();
                            Type parameterizedType = parameter.getParameterizedType();
                            String typeName = parameterizedType.getTypeName();
                            if (StringUtils.equals(typeName, "org.springframework.web.multipart.MultipartFile")) {
                                MultipartResolver resolver = new StandardServletMultipartResolver();
                                MultipartHttpServletRequest multiRequest = resolver.resolveMultipart(request);
                                MultipartFile file = multiRequest.getFile(name);
                                if (Objects.nonNull(file)) {
                                    // 校验文件类型
                                    checkFileType(whitelist, file);
                                }
                            } else if (StringUtils.equals(typeName, "org.springframework.web.multipart.MultipartFile[]")) {
                                MultipartResolver resolver = new StandardServletMultipartResolver();
                                MultipartHttpServletRequest multiRequest = resolver.resolveMultipart(request);
                                List<MultipartFile> files = multiRequest.getFiles(name);
                                if (CollectionUtils.isNotEmpty(files)) {
                                    for (MultipartFile file : files) {
                                        // 校验文件类型
                                        checkFileType(whitelist, file);
                                    }
                                }
                            }
                        }
                    }
                }

                String uri = request.getRequestURI().replace(request.getContextPath(), "");
                BasePermissionService basePermissionService = SpringContextHolder.getBean(BasePermissionService.class);

                String mybatisSqlId = basePermissionService.listDataPermissionSqlId(uri, "ALL");
                response.setHeader(SysConstant.DataPermissionHeader.DATA_PERMISSION_MAPPER_ID, mybatisSqlId);

                String appId = request.getHeader("Internal-Anon-AppId");
                if (!verifySubmit || StringUtils.isNotBlank(appId)) {
                    return joinPoint.proceed();
                }
                if (GET_METHOD.equals(request.getMethod())) {
                    return joinPoint.proceed();
                }
                if (PERMIT_ALL_URL.contains(request.getRequestURI())) {
                    return joinPoint.proceed();
                }
                String url = getFullURL(request);
                Object[] args = joinPoint.getArgs();
                String body = getBody(args);
                String hashKey = getHashKey(request.getMethod().toUpperCase(), url, body);

                String key = IPUtil.getRemoteIpAddr(request) + "-" + hashKey;
                // 如果缓存中有这个url视为重复提交
                if (redisUtil.get(key) == null) {
                    redisUtil.set(key, 0, TIMEOUT);
                    return joinPoint.proceed();
                } else {
                    //log.info("RepeatSubmitHandler- url:{},body:{},key{}", url, body, key);
                    throw new BaseException("请勿频繁请求");
                }
            }
        }
        return joinPoint.proceed();
    }

    /**
     * 检查文件类型
     *
     * @param whitelist 白名单
     * @param file      文件
     */
    private void checkFileType(String whitelist, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isNotBlank(originalFilename)) {
            String message = "当前仅可上传 " + whitelist + " 类型文件！";
            if (!originalFilename.contains(POINT)) {
                throw new BaseException(message);
            }
            String[] split = originalFilename.split("\\.");
            String fileType = split[split.length - 1];
            if (!whitelist.contains(fileType.toLowerCase())) {
throw new BaseException(MessageFormat.format(LocaleHandler.getLocaleMsg("{0}当前文件类型为：{1} 不允许上传！"),message,fileType));
            }
        }
    }

}
