package com.midea.cloud.srm.biz.pj.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.component.proxy.DefaultExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 线程池配置、启用异步
 *
 * @author artifact
 */
@EnableAsync(proxyTargetClass = true)
@Configuration
public class AsycTaskExecutorConfig {

    @Autowired
    private DefaultExecutor defaultExecutor;

    @Bean
    public Executor taskExecutor() {
        defaultExecutor.setCorePoolSize(50);
        defaultExecutor.setMaxPoolSize(100);
        defaultExecutor.initialize();
        Executor executor = TtlExecutors.getTtlExecutor(defaultExecutor);
        try {
            Field field = CompletableFuture.class.getDeclaredField("asyncPool");
            ReflectionUtils.makeAccessible(field);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            ReflectionUtils.makeAccessible(modifiers);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(null,executor);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (NoSuchFieldException e) {
            throw new BaseException("方法taskExecutor抛出异常：" + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new BaseException("方法taskExecutor抛出异常：" + e.getMessage());
        }
        return executor;
    }

}
