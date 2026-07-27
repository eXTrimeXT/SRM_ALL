package com.midea.cloud.gateway.pj.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Lazy
@Slf4j
@Component
public class PjUserLockUtil extends AbstractPjLockUtil {
    @Override
    protected String businessType() {
        return PjCommon.USER_SECRET;
    }
    @Override
    protected long keyTimeout() {
        long second = 3600 * 24 * 30L;
        return second;
    }

}
