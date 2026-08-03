package com.midea.cloud.srm.base.common;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/9
 */
@Component
public class RemovePluginConfig implements BeanDefinitionRegistryPostProcessor {

//    private static final String COMMON_KEY_CONTROLLER = "commonKeyController";
    private static final String SYSTEM_THEME_CONTROLLER = "systemThemeController";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
//        if (beanDefinitionRegistry.containsBeanDefinition(COMMON_KEY_CONTROLLER)) {
//            beanDefinitionRegistry.removeBeanDefinition(COMMON_KEY_CONTROLLER);
//        }

        if (beanDefinitionRegistry.containsBeanDefinition(SYSTEM_THEME_CONTROLLER)) {
            beanDefinitionRegistry.removeBeanDefinition(SYSTEM_THEME_CONTROLLER);
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

}