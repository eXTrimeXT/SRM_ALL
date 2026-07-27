package com.midea.cloud.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StopWatch;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MimeHeaders;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author artifact
 */
@Slf4j
public class PjRemoveCookieAccessFilter extends ZuulFilter {


    @SneakyThrows(value = {Exception.class})
    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        log.info("PjRemoveCookieAccessFilter HttpServletRequest head cookie: " + request.getHeader("cookie"));

        Cookie[] cookies = request.getCookies();
        if(!Objects.isNull(cookies) && cookies.length>0) {
            for(Cookie cookie :cookies) {
                if("KeyToken".equals(cookie.getName())) {
                    cookie.setValue("");
                }
            }
        }

        return null;
    }

    @SuppressWarnings("AlibabaMethodReturnWrapperType")
    @Override
    public boolean shouldFilter() {

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        String serviceId = (String) requestContext.get("serviceId");

        log.info("PjRemoveCookieAccessFilter shouldFilter URI: " + request.getRequestURI());

        Boolean matcher = PatternMatchUtils.simpleMatch("/cloud-srm/api-pj/external/bpm/viewSrm", request.getRequestURI());

        log.info("PjRemoveCookieAccessFilter shouldFilter matcher: " + matcher);
        return matcher;
    }


    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

}