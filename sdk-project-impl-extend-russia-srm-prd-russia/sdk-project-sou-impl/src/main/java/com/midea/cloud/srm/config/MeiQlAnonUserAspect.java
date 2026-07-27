package com.midea.cloud.srm.config;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * @author huangyq154
 * @description 匿名接口访问切面，用来跳过com.midea.cloud.srm.ql.component.UserInfoExecInterceptor.beforeExecute
 * @date 2023/10/28 11:01
 */
@Aspect
@Component
@Slf4j
public class MeiQlAnonUserAspect {
    @Around("execution(* com.midea.cloud.srm.ql.component.UserInfoExecInterceptor.beforeExecute(..))")
    public void doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        LoginAppUser loginAppUser = tryGetUser();
        if (null == loginAppUser) {
            return;
        }
        joinPoint.proceed();
    }

    private static LoginAppUser tryGetUser() {
        try {
            return AppUserUtil.getLoginAppUser();
        } catch (Exception ex) {
            log.warn("无法获取用户信息:" + ex.getMessage());
            return null;
        }
    }
}
