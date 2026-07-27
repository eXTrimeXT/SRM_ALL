package com.midea.cloud.srm.feign.pj.util;

import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.optionals.OptionalDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
 *  修改日期: 2022/10/28 11:52
 *  修改内容:
 * </pre>
 */
@Configuration
@Slf4j
public class OpenApiFeignClientConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public OpenApiInterceptor orderServiceInterceptor() {
        return new OpenApiInterceptor();
    }

    @Bean
    public Decoder feignDecoder() {
        return new OptionalDecoder(new StringDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters))));
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringFormEncoder(new SpringEncoder(this.messageConverters));
    }

    @Bean
    @ConditionalOnProperty(prefix = "debug", name = "feign", havingValue = "true")
    public Logger.Level logger() {
        return Logger.Level.FULL;
    }
}
