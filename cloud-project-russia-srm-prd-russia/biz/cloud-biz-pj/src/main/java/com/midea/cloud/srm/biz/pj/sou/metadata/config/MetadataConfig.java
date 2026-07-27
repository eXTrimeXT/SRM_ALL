package com.midea.cloud.srm.biz.pj.sou.metadata.config;

import com.midea.cloud.component.interceptor.MybatisInterceptorConfig;
import com.midea.cloud.srm.biz.pj.sou.metadata.interceptor.EntityExtendQueryInterceptor;
import com.midea.cloud.srm.biz.pj.sou.metadata.interceptor.EntityExtendUpdateInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 *
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/21 19:33
 *  修改内容:
 * </pre>
 */
@Configuration
@AutoConfigureAfter(MybatisInterceptorConfig.class)
public class MetadataConfig {

    @Bean
    public String metadataInterceptor(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        sqlSessionFactory.getConfiguration().addInterceptor(new EntityExtendQueryInterceptor());
        sqlSessionFactory.getConfiguration().addInterceptor(new EntityExtendUpdateInterceptor());
        return "metadataInterceptor";
    }
}
