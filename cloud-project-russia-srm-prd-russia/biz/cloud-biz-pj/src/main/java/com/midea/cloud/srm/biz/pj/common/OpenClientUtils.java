package com.midea.cloud.srm.biz.pj.common;

import com.alibaba.fastjson.JSON;
import com.gwm.open.sdk.OpenClient;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author huangbf3
 */
@Component
@Slf4j
public class OpenClientUtils {

    public enum TYPE {
        /**
         *
         */
        ORG_LIST, EMPLOYEE_LIST, CREATE_SUPPLIER, EDIT_SUPPLIER,FIND_SUPPLIERNAME,FIND_SUPPLIERTAXCODE, USER_INFO, USER_INFO_BATCH, SYN_SUPPLIER_VIEW,SYN_BLACK_COMPANY;
    }

    @Getter
    private class ClientUri {
        private TYPE type;
        private String url;
        protected ClientUri(TYPE type, String url) {
            this.type = type;
            this.url = url;
        }
    }

    @Value("${gwm.appkey}")
    private String appKey;
    @Value("${gwm.secret}")
    private String secret;
    @Value("${gwm.preappkey}")
    private String preappKey;
    @Value("${gwm.presecret}")
    private String presecret;
    @ApiModelProperty("组织列表")
    @Value("${gwm.url.org-list}")
    private String orgListUrl;

    @ApiModelProperty("人员列表")
    @Value("${gwm.url.employee-list}")
    private String employeeListUrl;

    @ApiModelProperty("员工信息")
    @Value("${gwm.url.user-info}")
    private String userInfoUrl;

    @ApiModelProperty("员工信息批量")
    @Value("${gwm.url.user-info-batch}")
    private String userInfoBatchUrl;

    @ApiModelProperty("申请供应商编码")
    @Value("${gwm.url.create-supplier}")
    private String createupplierUrl;

    @ApiModelProperty("根据名称查询供应商信息")
    @Value("${gwm.url.find-supplierByName}")
    private String findSupplierByName;

    @ApiModelProperty("根据社会信用码查询供应商信息")
    @Value("${gwm.url.find-supplierByTaxcodes}")
    private String findSupplierByTaxcodes;


    @ApiModelProperty("修改供应商信息")
    @Value("${gwm.url.edit-supplier}")
    private String editSupplierUrl;

    @ApiModelProperty("供应商同步到SAP")
    @Value("${gwm.url.create-supplierview}")
    private String createSupplierViewUrl;
    @ApiModelProperty("接收MDM失信名单数据")
    @Value("${gwm.url.receive-blackcompany}")
    private String receiveBlackCompanyUrl;

    private static OpenClientUtils openClientUtils;

    @PostConstruct
    private void inite() {
        openClientUtils = this;
    }

    public static String sendHttpGetParam(Map<String, Object> param) {
        if(MapUtils.isEmpty(param)) {
            return "";
        }
        StringBuffer paramStr = new StringBuffer();

        for(String key: param.keySet()) {
            if(paramStr.length() <= 0) {
                paramStr.append("?");
            } else {
                paramStr.append("&");
            }
            paramStr.append(key).append("=").append(MapUtils.getString(param, key));
        }
        return paramStr.toString();
    }

    private String getUrl(TYPE type) {
        switch (type) {
            case ORG_LIST:
                return new ClientUri(type, orgListUrl).getUrl();
            case EMPLOYEE_LIST:
                return new ClientUri(type, employeeListUrl).getUrl();
            case CREATE_SUPPLIER:
                return new ClientUri(type, createupplierUrl).getUrl();
            case EDIT_SUPPLIER:
                return new ClientUri(type, editSupplierUrl).getUrl();
            case USER_INFO:
                return new ClientUri(type, userInfoUrl).getUrl();
            case USER_INFO_BATCH:
                return new ClientUri(type, userInfoBatchUrl).getUrl();
            case SYN_SUPPLIER_VIEW:
                return new ClientUri(type, createSupplierViewUrl).getUrl();
            case SYN_BLACK_COMPANY:
                return new ClientUri(type, receiveBlackCompanyUrl).getUrl();
            case FIND_SUPPLIERNAME:
                return new ClientUri(type, findSupplierByName).getUrl();
            case FIND_SUPPLIERTAXCODE:
                return new ClientUri(type, findSupplierByTaxcodes).getUrl();
            default:;
        }
        return null;
    }

    private String sendHttpGet2(TYPE type, String param) {
        OpenClient openClient = new OpenClient(appKey,secret);
        String result = null;
        log.info("请求OPEN CLIENT GET开始，请求类型：{}, 请求参数：{}", type.name(), param);
        result = openClient.sendHttpGet(StringUtils.joinWith("", getUrl(type), param));
        log.info("请求OPEN CLIENT GET开始，请求类型：{}, 请求结果：{}", type.name(), result);
        return result;
    }

    private String hengSendHttpPost(TYPE type, String body) {
        OpenClient openClient = new OpenClient(appKey,secret);
        String result = null;
        log.info("请求OPEN CLIENT GET开始，请求类型：{}, 请求参数：{}", type.name(), body);
        result = openClient.sendHttpPost(getUrl(type), body);
        log.info("请求OPEN CLIENT GET开始，请求类型：{}, 请求结果：{}", type.name(), result);
        return result;
    }

    private String hengSendHttpPost(TYPE type, Map<String, String> body) {
        OpenClient openClient = new OpenClient(appKey,secret);
        String result = null;
        log.info("请求OPEN CLIENT GET开始，请求类型：{}, 请求参数：{}", type.name(), body);
        result = openClient.sendHttpPost(getUrl(type), body);
        log.info("请求OPEN CLIENT GET开始，请求类型：{}, 请求结果：{}", type.name(), result);
        return result;
    }

    private String hengSendHttpPost(TYPE type, String body, String contentType) {
        OpenClient openClient = new OpenClient(appKey,secret);
        String result = null;
        log.info("请求OPEN CLIENT GET开始，请求类型：{}, 请求参数：{}", type.name(), body);
        result = openClient.sendHttpPost(getUrl(type), body, contentType);
        log.info("请求OPEN CLIENT GET开始，请求类型：{}, 请求结果：{}", type.name(), result);
        return result;
    }

    private String hengSendHttpPost(String url, Map<String, String> body) {
        OpenClient openClient = new OpenClient(appKey,secret);
        String result = null;
        log.info(MessageFormat.format("请求OPEN CLIENT POST开始，请求URL：{0}, 请求参数：{1}", url, JSON.toJSONString(body)));
        result = openClient.sendHttpPost(url, body);
        log.info(MessageFormat.format("请求OPEN CLIENT POST结束，请求URL：{0}, 请求结果：{1}", url, result));
        return result;
    }

    private String hengSendHttpPost(String url, String body, String contentType) {
        OpenClient openClient = new OpenClient(appKey,secret);
        String result = null;
        log.info(MessageFormat.format("请求OPEN CLIENT POST开始，请求URL：{0}, 请求参数：{1}", url, body));
        result = openClient.sendHttpPost(url, body, contentType);
        log.info(MessageFormat.format("请求OPEN CLIENT POST结束，请求URL：{0}, 请求结果：{1}", url, result));
        return result;
    }

    public static String sendHttpPost(String url, String body, String contentType) {
        return openClientUtils.hengSendHttpPost(url, body, contentType);
    }

    public static String sendHttpPost(String url, Map<String, String> body) {
        return openClientUtils.hengSendHttpPost(url, body);
    }

    public static String sendHttpGet(TYPE type, String param) {
        return openClientUtils.sendHttpGet2(type, param);
    }

    public static String sendHttpPost(TYPE type, String body) {
        return openClientUtils.hengSendHttpPost(type, body);
    }

    public static String sendHttpPost(TYPE type, Map<String, String> body) {
        return openClientUtils.hengSendHttpPost(type, body);
    }

    public static String sendHttpPost(TYPE type, String body, String contentType) {
        return openClientUtils.hengSendHttpPost(type, body, contentType);
    }

}
