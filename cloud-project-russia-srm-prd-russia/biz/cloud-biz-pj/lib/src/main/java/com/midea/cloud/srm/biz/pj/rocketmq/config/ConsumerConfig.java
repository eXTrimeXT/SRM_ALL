package com.midea.cloud.srm.biz.pj.rocketmq.config;

import com.midea.cloud.srm.biz.pj.rocketmq.consumer.SrmProjectReviewConsumer;
import com.midea.cloud.srm.biz.pj.rocketmq.consumer.SrmRocketMqConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GW00086630
 */
@Configuration
@ConditionalOnProperty(value = "rocketmq.consumer.namesrvAddr")
public class ConsumerConfig {

    @Bean(initMethod = "consumer")
    public SrmRocketMqConsumer srmRocketMqConsumer(){
        return new SrmRocketMqConsumer();
    }
    @Bean(initMethod = "consumer")
    public SrmProjectReviewConsumer srmProjectReviewConsumer(){
        return new SrmProjectReviewConsumer();
    }

}
