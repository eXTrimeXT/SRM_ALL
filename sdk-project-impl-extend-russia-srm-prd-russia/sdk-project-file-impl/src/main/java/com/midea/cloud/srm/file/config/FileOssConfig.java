package com.midea.cloud.srm.file.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.meicloud.paas.osca.configure.OscaConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;


/**
 * @description:
 * @date: 2023/9/27 9:02
 * @author huangbf3
 */

@Configuration
public class FileOssConfig {
    @Resource
    private OscaConfigProperties oscaConfigProperties;

    @Bean
    @Primary
    public OSS ossConfig() {
        OSS ossClient = new OSSClientBuilder().build(oscaConfigProperties.getEndPoint(), oscaConfigProperties.getAccessKeyId(), oscaConfigProperties.getAccessKeySecret());
        return ossClient;
    }
}
