package com.midea.cloud.component;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: panmq
 * @Date: 2024/03/21/ $
 * @Description: 扩展扫描路径配置
 */
@Configuration
@ComponentScan(basePackages = {"com.midea.cloud.srm.file"})
@MapperScan(basePackages = {"com.midea.cloud.srm.file.**.mapper"})
public class ExtThirdScan {
}
