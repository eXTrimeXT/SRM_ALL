package com.midea.cloud.srm.base.industry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @date: 2023/9/27 9:02
 * @author huangbf3
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "gwm")
public class GwmProperties {

    private String appkey;

    private String secret;

    private String prdAppkey;

    private String prdSecret;

    private String sendEmailUrl;
    private String sendSmsUrl;
    private String srcSystem;
}
