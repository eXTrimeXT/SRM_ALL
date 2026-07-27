package com.midea.cloud.srm.supcooperate.config;

import com.midea.cloud.common.annotation.CacheAop;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 备注
 * @author huangbf3
 */
@Configuration
public class PjCacheConfig {

    @Bean
    @ConditionalOnMissingBean
    public CacheAop getCacheAop(){
        return new CacheAop();
    }

}