package com.midea.cloud.common.pj.utils;

import com.midea.cloud.srm.feign.pj.util.OpenApiInterceptor;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author zhengjj38
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/27 17:20
 *  修改内容:
 * </pre>
 */

public class OpenFeignContext extends FeignContext {

    private static final String INTERCEPTOR_KEY = "orderServiceInterceptor";

    @Override
    public <T> Map<String, T> getInstances(String name, Class<T> type) {
        AnnotationConfigApplicationContext context = this.getContext(name);
        Map<String, T> result = context.getBeansOfType(type);
        if(result.get(INTERCEPTOR_KEY)!=null&&result.get(INTERCEPTOR_KEY).getClass()== OpenApiInterceptor.class){
            return result;
        }
        return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, type).length > 0 ? BeanFactoryUtils.beansOfTypeIncludingAncestors(context, type) : null;

    }


}
