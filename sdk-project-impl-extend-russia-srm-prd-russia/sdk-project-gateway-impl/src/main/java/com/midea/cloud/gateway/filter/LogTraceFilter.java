package com.midea.cloud.gateway.filter;

import com.mideacloud.common.log.EncryptUtil;
import com.mideacloud.common.log.LogClientIpUtils;
import com.mideacloud.common.log.LogTraceUtils;
import com.mideacloud.common.log.LogUserIdUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 日志链路ID等信息透传至下游微服务，确保日志打印完整
 *
 * @author artifact
 */
@Component
@Slf4j
public class LogTraceFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        String traceId = LogTraceUtils.getTraceId();
        if (StringUtils.hasText(traceId)) {
            context.addZuulRequestHeader("X-TID", traceId);
        }

        String userId = LogUserIdUtils.getUserId();
        if (StringUtils.hasText(userId)) {
            context.addZuulRequestHeader("X-UID", EncryptUtil.encrypt(userId));
        }

        String clientIp = LogClientIpUtils.getClientIp();
        if (StringUtils.hasText(clientIp)) {
            context.addZuulRequestHeader("X-CLIENT-IP", EncryptUtil.encrypt(clientIp));
        }
        return null;
    }
}
