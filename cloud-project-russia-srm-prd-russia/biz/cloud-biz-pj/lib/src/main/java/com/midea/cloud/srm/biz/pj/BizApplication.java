package com.midea.cloud.srm.biz.pj;

import com.meicloud.paas.audit.AuditAutoConfiguration;
import com.midea.cloud.component.config.LoadBalanceConfiguration;
import com.midea.cloud.quartz.bind.EnableJobFeignSupport;
import com.mideacloud.common.objectx.EnableObjectX;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <pre>
 *  合同管理模块
 * </pre>
 *
 * @author huanghb14@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-5-13 10:34
 *  修改内容:
 * </pre>
 */
@EnableObjectX(basePackages = {"com.mideacloud", "com.midea"})
@EnableFeignClients({"com.midea.cloud.srm.feign","com.mideacloud"})
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.midea.cloud.srm.biz","com.midea.cloud.flyway", "com.midea.cloud.component", "com.midea.cloud.srm.feign.base.service", "com.midea.cloud.common",
        "com.midea.cloud.srm.business","com.midea.cloud.srm.ql", "com.midea.cloud.srm.dynamic_formilyjs", "com.midea.cloud.srm.saudit"})
@MapperScan(basePackages = {"com.midea.cloud.srm.biz.**.mapper", "com.midea.cloud.srm.biz.**.dao", "com.midea.cloud.srm.business.dao"})
@EnableJobFeignSupport
@LoadBalancerClients(defaultConfiguration = LoadBalanceConfiguration.class)
@Slf4j
public class BizApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(BizApplication.class);
        springApplication.setAllowCircularReferences(true);
        springApplication.run(args);
    }

}
