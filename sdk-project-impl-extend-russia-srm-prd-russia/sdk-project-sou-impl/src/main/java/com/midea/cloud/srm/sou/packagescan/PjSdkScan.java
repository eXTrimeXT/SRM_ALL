package com.midea.cloud.srm.sou.packagescan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 备注
 * @author huangbf3
 */
@Configuration
@ComponentScan(basePackages = {"com.midea.cloud.srm.sies","com.meicloud.paas.ies"})
public class PjSdkScan {
}
