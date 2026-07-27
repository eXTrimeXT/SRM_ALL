package com.midea.cloud.srm.biz.pj.sou.metadata.aspect;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.sou.metadata.context.MetadataDataContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * <pre>
 * 可扩展字段切面，用于根据表名读取配置
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/6/25 11:55
 *  修改内容:
 * </pre>
 */
@Aspect
@Component
@Slf4j
public class MetadataDataAspect {
    private static String TABLE_NAME_GETTER = "getTableName";

    @Pointcut("execution(* com.midea.cloud.srm.biz.pj.sou.metadata.service.impl.MetadataDataServiceImpl.*(..))")
    public void entrypoint() {
        //切点为可扩展数据查询的serviceImpl
    }

    @Before("entrypoint()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        String tableName = getTableName(joinPoint);
        if (StringUtils.isEmpty(tableName)) {
            throw new BaseException("表名不能为空");
        }
        MetadataDataContext.load(tableName);
    }

    @After("entrypoint()")
    public void doAfter(JoinPoint joinPoint) throws Throwable {
        MetadataDataContext.destroy();
    }

    private String getTableName(JoinPoint joinPoint) throws Exception {
        String tableName = null;
        Object[] args = joinPoint.getArgs();
        if (null != args && args.length > 0) {
            tableName = getDtoTableName(args[0]);
        }
        return tableName;
    }

    private String getDtoTableName(Object bodyDto) {
        try {
            Method getter = bodyDto.getClass().getMethod(TABLE_NAME_GETTER);
            Object value = getter.invoke(bodyDto);
            if (null != value) {
                return value.toString();
            }
        } catch (Exception e) {
            throw new BaseException("获取tableName参数失败");
        }
        return null;
    }
}
