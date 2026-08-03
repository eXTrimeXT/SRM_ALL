package com.midea.cloud.srm.biz.pj.sou.metadata.interceptor;

import com.midea.cloud.srm.model.common.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Plugin;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Properties;

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
 *  修改日期: 2022/7/26 9:16
 *  修改内容:
 * </pre>
 */
public abstract class AbstractEntityExtendInterceptor implements Interceptor {

    protected Object getEntityId(Object row, String entityIdAttr) {
        if (StringUtils.isEmpty(entityIdAttr)) {
            return null;
        }
        if (row instanceof BaseEntity) {
            try {
                Field field = row.getClass().getDeclaredField(entityIdAttr);
                ReflectionUtils.makeAccessible(field);
                return ReflectionUtils.getField(field, row);
            } catch (Exception e) {
            }
        } else if (row instanceof Long || row instanceof String) {
            return row;
        }
        return null;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
