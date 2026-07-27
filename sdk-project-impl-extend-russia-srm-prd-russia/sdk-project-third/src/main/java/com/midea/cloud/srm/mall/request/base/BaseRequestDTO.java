package com.midea.cloud.srm.mall.request.base;

import lombok.Data;

import java.util.Map;

/**
 * 商城公共的父类对象，包含公用的信息，比如token信息，理论上所有请求参数类都继承此类
 */
@Data
public class BaseRequestDTO implements BaseRequest {

    private String token;

    private String mallType; // 商城类型，京东、淘宝 、其他

    private Map<String, Object> extras;

}
