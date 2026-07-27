package com.midea.cloud.common.pj.utils;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.StringUtil;
import com.midea.cloud.srm.model.annonations.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * <pre>
 *  描述
 * </pre>
 *
 * @author Sam
 * @version 1.0
 * @date 2022/10/19 16:27
 */
public class ObjectUtils {
    private ObjectUtils() {
    }

    public static  <T, B> boolean checkIsSame(T object1, B object2, Map<String, Class> map) throws InvocationTargetException, IllegalAccessException {
        Class<?> aClass1 = object1.getClass();
        Class<?> aClass2 = object2.getClass();

        Iterator<Map.Entry<String, Class>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Class> next = iterator.next();
            String fname = next.getKey();
            String methodname = "get" + Character.toUpperCase(fname.charAt(0)) + fname.substring(1);
//            获取定义的方法
            Method method1 = getMethod(aClass1, methodname, new Class[] {});
//            获取定义的方法
            Method method2 = getMethod(aClass2, methodname, new Class[] {});
            Object value1 = null;
            Object value2 = null;
            if(method1 != null){
                value1 = method1.invoke(object1);
            }
            if(method2 != null){
                value2 = method2.invoke(object2);
            }

            if(value1 == null && value2 == null ){
                continue;
            }else if(value1 == null ||  value2 == null){
                return false;
            }
            if(value1 instanceof BigDecimal){
                if(((BigDecimal) value1).compareTo((BigDecimal) value2) != 0){
                    return false;
                }
            }else if(value1 instanceof Date){
                if(((Date) value1).compareTo((Date) value2) != 0){
                    return false;
                }
            }else{
                if(!value1.equals(value2)){
                    return false;
                }
            }

        }
        return true;
    }
    /**
     * 获取反射调用的方法-包含父类的
     * @param clazz
     * @param methodName
     * @param classes
     * @return
     * @throws Exception
     */
    private static Method getMethod(Class clazz, String methodName,
                                    final Class[] classes){
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, classes);
            } catch (NoSuchMethodException ex) {
                if (clazz.getSuperclass() == null) {
                    return method;
                } else {
                    method = getMethod(clazz.getSuperclass(), methodName,
                            classes);
                }
            }
        }
        return method;
    }

    /**
     * 获取对象里面的某个值
     * @param object
     * @param fname
     * @param clazz
     * @param <T>
     * @param <V>
     * @return
     */
    public static  <T, V> T getValue(V object, String fname, Class<T> clazz){
        Class<?> aClass = object.getClass();
        String methodname = "get" + Character.toUpperCase(fname.charAt(0)) + fname.substring(1);
//        获取定义的方法
        Method method1 = getMethod(aClass, methodname, new Class[] {});
        Object value = null;
        try {
            if(method1 != null){
                value = method1.invoke(object);
            }
            if(clazz.isInstance(value)){
                return (T)value;
            }
        } catch (IllegalAccessException e) {
            throw new BaseException("方法getValue抛出异常：" + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new BaseException("方法getValue抛出异常：" + e.getMessage());
        }
        return null;
    }
    /**
     * 设置对象里面的某个值
     * @param object
     * @param fname
     * @param
     * @param <T>
     * @param <V>
     * @return
     */
    public static  <T, V> void setValue(V object, String fname, T value){
        Class<?> aClass = object.getClass();
        String methodname = "set" + Character.toUpperCase(fname.charAt(0)) + fname.substring(1);
//        获取定义的方法
        Method method1 = getMethod(aClass, methodname, new Class[] {value.getClass()});
        try {
            if(method1 != null){
                method1.invoke(object, value);
            }
        } catch (IllegalAccessException e) {
            throw new BaseException("方法setValue抛出异常：" + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new BaseException("方法setValue抛出异常：" + e.getMessage());
        }
    }

    /**
     * 校验是否有null
     * @param object
     * @return
     */
    public static  StringBuffer checkNull(Object object)  {
        StringBuffer builder = new StringBuffer();
        try {
            Class clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            Boolean isNull = false;
            for (Field field : fields) {
                /* 设置访问权限 */
                field.setAccessible(true);
                /*判断是否有注解 */
                if(field.isAnnotationPresent(NotNull.class)){
                    Object o = null;
                    o = field.get(object);
                    isNull = false;
                    /*字符串的判空 */
                    if(field.getType() == String.class){
                        if(StringUtil.isEmpty(o)){
                            isNull = true;
                        }
                    }else {
                        if(o == null){
                            isNull = true;
                        }
                    }
                    if(isNull){
                        NotNull annotation = field.getAnnotation(NotNull.class);
                        builder.append(annotation.value()).append(";");
                    }

                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return builder;
    }

}
