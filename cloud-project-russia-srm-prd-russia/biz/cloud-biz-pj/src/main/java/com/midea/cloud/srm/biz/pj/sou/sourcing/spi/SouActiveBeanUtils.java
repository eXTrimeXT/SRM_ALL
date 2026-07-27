package com.midea.cloud.srm.biz.pj.sou.sourcing.spi;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * 寻源核心 - spi定义
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/10
 */
@Component
public class SouActiveBeanUtils implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    @Override
    public void destroy() throws Exception {
        applicationContext = null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SouActiveBeanUtils.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 查找指定模块下最高优先级的实现类(只找一个)
     *
     * @param souType 指定寻源模块 {@link SouTypeEnum}
     * @param clazz   类型
     */
    public static <T extends ISouSpiBean> T getActiveBean(String souType, Class<T> clazz) {
        AssertUtils.notNull(applicationContext, "applicationContext上下文为空");
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "请选择souType");
        // 0: 使用MQL的工具类，尝试获取外部的可用最新实现类
        // 1: 查询模块的自定义实现
        T defaultImpl = null;
        List<T> impls;
        {
            Collection<T> list = applicationContext.getBeansOfType(clazz).values();
            impls = new ArrayList<>(list.size());
            for (T t : list) {
                if (t.getClass().getName().startsWith("com.midea.cloud.srm.biz.pj.sou.sourcing.") || t.getClass().getName().startsWith("com.midea.cloud.srm.sou.meiql.sourcing")) {
                    // 实现类位于核心包下
                    AssertUtils.isTrue(SouTypeEnum.DEFAULT.name().equals(t.matchModule()), "{0}" + LocaleHandler.getLocaleMsg("定义错误") + ": " + LocaleHandler.getLocaleMsg("核心包下必须定义为SouTypeEnum.DEFAULT"),
                            t.getClass());
                    defaultImpl = t;
                } else {
                    // 实现类位于寻源子模块下
                    AssertUtils.isFalse(SouTypeEnum.DEFAULT.name().equals(t.matchModule()), "{0}" + LocaleHandler.getLocaleMsg("定义错误") + ": " + LocaleHandler.getLocaleMsg("非核心包(sourcing)下的类不能定义为SouTypeEnum.DEFAULT"),
                            t.getClass());
                    if (t.matchModule().equals(souType)) {
                        impls.add(t);
                    }
                }
            }
            // 按照优先级降序排列
            impls.sort(Comparator.comparing(T::getOrder).reversed());
        }
        // 3: 找到最高优先级的实现类
        if (!impls.isEmpty()) {
            return impls.get(0);
        } else {
            if (defaultImpl != null) {
                return defaultImpl;
            } else {
                throw new IllegalArgumentException(MessageFormat.format("寻源类型{0}下没有可用的{1}实现类", souType, clazz));
            }
        }
    }

    /**
     * 查找指定模块下的实现类(返回一个集合)
     *
     * @param souType 指定寻源模块{@link SouTypeEnum}
     * @param clazz   类型
     */
    public static <T extends ISouSpiBean> List<T> getActiveBeans(String souType, Class<T> clazz) {
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType");
        AssertUtils.notNull(applicationContext, "applicationContext上下文为空");
        List<T> impls = new ArrayList<>();
        // 1: 查询模块的自定义实现
        Collection<T> list = applicationContext.getBeansOfType(clazz).values();
        for (T t : list) {
            if (t.getClass().getName().startsWith("com.midea.cloud.srm.sou.sourcing.")) {
                // 实现类位于核心包下
                AssertUtils.isTrue(SouTypeEnum.DEFAULT.name().equals(t.matchModule()), "{0}" + LocaleHandler.getLocaleMsg("定义错误") + ": " + LocaleHandler.getLocaleMsg("核心包下必须定义为SouTypeEnum.DEFAULT"),
                        t.getClass());
                impls.add(t);
            } else {
                // 实现类位于寻源子模块下
                AssertUtils.isFalse(SouTypeEnum.DEFAULT.name().equals(t.matchModule()), "{0}" + LocaleHandler.getLocaleMsg("定义错误") + ": " + LocaleHandler.getLocaleMsg("非核心包(sourcing)下的类不能定义为SouTypeEnum.DEFAULT"),
                        t.getClass());
                if (t.matchModule().equals(souType)) {
                    impls.add(t);
                }
            }
        }
        // 按照优先级降序排列
        impls.sort(Comparator.comparing(T::getOrder).reversed());
        return impls;
    }

}
