package com.midea.cloud.gateway;

import com.midea.cloud.component.config.ConfigServletWebServerApplicationContextFactory;
import com.mideacloud.common.objectx.EnableObjectX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 网关，统一入口
 *
 * @author artifact
 */
@EnableFeignClients("com.midea.cloud.srm.feign")
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
        "com.mideacloud.common.util.keystore", "com.mideacloud.common.user", "com.mideacloud.common.impl.user",
        "com.midea.cloud.gateway",
        "com.midea.cloud.common.autoconfigure",
        "com.midea.cloud.component",
        "com.midea.cloud.srm.webservice",
        "com.midea.cloud.srm.websocket",
        "com.midea.cloud.project"
})
@Slf4j
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(GatewayApplication.class);
        springApplication.setAllowCircularReferences(true);
        springApplication.run(args);
    }

}