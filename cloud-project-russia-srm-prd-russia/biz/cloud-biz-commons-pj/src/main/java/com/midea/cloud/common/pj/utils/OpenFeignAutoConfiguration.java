package com.midea.cloud.common.pj.utils;

import com.midea.cloud.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author zhengjj38
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/27 21:44
 *  修改内容:
 * </pre>
 */
@Configuration
public class OpenFeignAutoConfiguration {


    @Autowired
    private FeignAutoConfiguration feignAutoConfiguration;

    @Bean
    @Primary
    public FeignContext srmFeignContext() {
        FeignContext feignContext = new OpenFeignContext();
        try {
            Map<String, NamedContextFactory.Specification> map = new ConcurrentHashMap<>(50);
            Field field = FeignAutoConfiguration.class.getDeclaredField("configurations");
            ReflectionUtils.makeAccessible(field);
            List<NamedContextFactory.Specification>  configurations = (List<NamedContextFactory.Specification>) field.get(feignAutoConfiguration);
            for (NamedContextFactory.Specification client : configurations) {
                map.put(client.getName(), client);
            }
            Field content = FeignContext.class.getSuperclass().getDeclaredField("configurations");
            ReflectionUtils.makeAccessible(content);
            content.set(feignContext, map);
        } catch (NoSuchFieldException e) {
            throw new BaseException("方法SrmFeignContext抛出异常：" + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new BaseException("方法SrmFeignContext抛出异常：" + e.getMessage());
        }

        return feignContext;
    }

}
