package com.midea.cloud.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * @Author: panmq
 * @Date: 2024/03/26/ $
 * @Description:
 */
@Component
@WebFilter(urlPatterns = "/cloud-srm/api-pj/external/bpm/viewSrm")
@Slf4j
public class SrmViewFilter implements Filter {

    private final static String VIEW_SRM = "/cloud-srm/api-pj/external/bpm/viewSrm";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        log.info("SrmViewFilter filter in this method....");

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String uri = httpServletRequest.getRequestURI();
        log.info("SrmViewFilter filter url: " + uri);
        if(!VIEW_SRM.equals(uri)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Cookie[] cookies = httpServletRequest.getCookies();
        if(!Objects.isNull(cookies) && cookies.length>0) {
            for(Cookie cookie :cookies) {
                if("KeyToken".equals(cookie.getName())) {
                    cookie.setValue("");
                }
            }
        }
        HeaderMapRequestWrapper headerMapRequestWrapper = new HeaderMapRequestWrapper(httpServletRequest);
        headerMapRequestWrapper.addHeader("cookie", "");
        filterChain.doFilter(headerMapRequestWrapper, servletResponse);

    }

    @Override
    public void destroy() {

    }
}
