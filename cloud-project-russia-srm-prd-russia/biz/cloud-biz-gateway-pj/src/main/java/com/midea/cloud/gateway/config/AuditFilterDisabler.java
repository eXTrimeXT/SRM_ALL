package com.midea.cloud.gateway.config;

import com.meicloud.paas.audit.filter.AuditLogFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

/**
 * Отключает регистрацию AuditLogFilter
 */
@Component
public class AuditFilterDisabler implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // Если находим FilterRegistrationBean для AuditLogFilter - отключаем его
        if (bean instanceof FilterRegistrationBean) {
            FilterRegistrationBean<?> registration = (FilterRegistrationBean<?>) bean;
            if (registration.getFilter() instanceof AuditLogFilter) {
                registration.setEnabled(false);
            }
        }

        // Если находим сам AuditLogFilter - заменяем на null
        if (bean instanceof AuditLogFilter) {
            return null; // Это заставит Spring не создавать этот бин
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}