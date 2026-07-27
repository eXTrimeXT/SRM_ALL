package com.midea.cloud.srm.cm.component;

import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.ql.component.DataPermissionListener;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jooq.DSLContext;
import org.jooq.ExecuteListenerProvider;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huangyq154
 * @description
 * @TODO 临时办法，UAT已修复，下次升级要去掉本切面
 * @date 2023/10/16 20:04
 */
@Aspect
@Component
public class MultiDataSourcesAspect {
    @Around("execution(* com.midea.cloud.meiql.core.repository.jooq.source.MultiDataSources.getDSLContext(..))")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        DSLContext dsl = (DSLContext) joinPoint.proceed();
        HttpServletRequest request = HttpServletHolder.getRequest();
        if (null == request) {
            //request为空去除数据权限监听器
            List<ExecuteListenerProvider> curExeListeners = Arrays.stream(dsl.configuration().executeListenerProviders())
                    .filter(p -> !(p.provide() instanceof DataPermissionListener)).collect(Collectors.toList());
            //执行临时查询配置，只对当次查询生效，这样就不会改变其他请求，哪怕是同一个事务的其他请求也不会改变(其他查询也会走一次这个拦截)
            return DSL.using(dsl.configuration().derive(curExeListeners.toArray(new ExecuteListenerProvider[curExeListeners.size()])));
        }
        //有request则不用动
        return dsl;
    }
}
