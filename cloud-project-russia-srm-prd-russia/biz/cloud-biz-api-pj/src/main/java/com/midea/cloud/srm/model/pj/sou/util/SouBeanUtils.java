package com.midea.cloud.srm.model.pj.sou.util;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @description:
 * @author: huanglj50@meicloud.com
 * @date: 2022/9/13 8:56
 */
public class SouBeanUtils {

    public static <T, K extends List> List<T> copyListProperties(K origs, Class<T> tClazz) {
        List<T> result = new ArrayList();
        if (CollectionUtils.isNotEmpty(origs)) {
            Iterator var3 = origs.iterator();

            while (var3.hasNext()) {
                Object orig = var3.next();

                try {
                    T instance = tClazz.newInstance();
                    org.springframework.beans.BeanUtils.copyProperties(orig, instance);
                    result.add(instance);
                } catch (IllegalAccessException | InstantiationException var6) {
                    Assert.isTrue(false, var6.getMessage());
                }
            }
        }

        return result;
    }

}
