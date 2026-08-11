//package com.midea.cloud.gateway.config;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//
//@Configuration
//public class GatewayFilterConfig {
//
//    @Bean
//    public FilterRegistrationBean disableAuditLogFilter() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setEnabled(false); // Отключаем фильтр
//        registration.setName("auditLogFilter");
//        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return registration;
//    }
//}