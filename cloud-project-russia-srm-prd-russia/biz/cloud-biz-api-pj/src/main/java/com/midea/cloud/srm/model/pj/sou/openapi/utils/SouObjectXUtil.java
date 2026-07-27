package com.midea.cloud.srm.model.pj.sou.openapi.utils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.common.exception.ApiCheckException;
import com.midea.cloud.srm.model.common.util.mp.SrmLambdaUtil;
import com.mideacloud.common.objectx.BaseObjectX;
import com.mideacloud.common.objectx.ExtensionMap;
import com.mideacloud.common.objectx.ObjectX;
import com.mideacloud.common.objectx.ser.JacksonRegister;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 寻源 - 基于objectX的复制，将一个对象的字段复制到另一个对象中
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/20
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Slf4j
public class SouObjectXUtil {

    /** 合规扫描 Make "DATE_TIME_FORMAT" an instance variable. */
    /** 使用static声明会造成线程安全问题，可以将其声明为a final String并SimpleDateFormat在本地创建实例 */
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final ObjectMapper DEFAULT_OBJ;
    static{
        SimpleModule simpleModule = new SimpleModule();

        DateFormat df=new SimpleDateFormat(DATE_TIME_FORMAT);
        DEFAULT_OBJ = new ObjectMapper()
                .findAndRegisterModules()
                .setDateFormat(df)
                .registerModule(simpleModule)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .enable(JsonParser.Feature.ALLOW_COMMENTS);
        JacksonRegister.register(DEFAULT_OBJ);
    }

    /**
     * 获取指定key的值
     */
    @SuppressWarnings("unchecked")
    public static <T, V> V getXbyLambda(ObjectX entity, SFunction<? super T, V> function) {
        Object value;
        String fieldName = null;
        try {
            SerializedLambda fieldLambda = SrmLambdaUtil.getSerializedLambda(function);
            fieldName = PropertyNamer.methodToProperty(fieldLambda.getImplMethodName());
            Field field = entity.getClass().getDeclaredField(fieldName);
            ReflectionUtils.makeAccessible(field);
            value = field.get(entity);
        } catch (NoSuchFieldException e) {
            value = entity.getExtensions().get(fieldName);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        if (value == null) {
            return null;
        } else {
            return (V) value;
        }
    }

    /**
     * 给指定实体添加指定值
     */
    public static <T, V> void putXbyLambda(ObjectX entity, SFunction<? super T, V> function, @Nullable V value) {
        String fieldName = null;
        try {
            SerializedLambda fieldLambda = SrmLambdaUtil.getSerializedLambda(function);
            fieldName = PropertyNamer.methodToProperty(fieldLambda.getImplMethodName());
            Field field = entity.getClass().getDeclaredField(fieldName);
            ReflectionUtils.makeAccessible(field);
            field.set(entity, value);
        } catch (NoSuchFieldException e) {
            entity.putX(fieldName, value);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static <T> T convertTargetObj(Object from, Class<T> clazz) {
        try {
            return DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(from), clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static <T> T convertTargetObj(Object from, Class<T> clazz, boolean clearExts) {
        T t = convertTargetObj(from, clazz);
        if (clearExts && t instanceof ObjectX) {
            if (((ObjectX)t).getExtensions() != null) {
                ((ObjectX)t).getExtensions().clear();
            }
        }
        return t;
    }

    public static <T> T convertTargetObj(Object from, TypeReference<T> type) {
        try {
            return DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(from), type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * PS: 建议使用基于jackson的 typeReference，这个是用于兼容现有的项目，以免现有项目更新版本后出现报错
     */
    public static <T> T convertTargetObj(Object from, com.alibaba.fastjson.TypeReference<T> type) {
        return JSON.parseObject(JSON.toJSONString(from), type);
    }

    public static <T> T convertTargetObj(Object from, TypeReference<T> type, boolean clearExts) {
        T t = convertTargetObj(from, type);
        if (clearExts && t instanceof ObjectX) {
            if (((ObjectX)t).getExtensions() != null) {
                ((ObjectX)t).getExtensions().clear();
            }
        }
        return t;
    }

    /**
     * PS: 建议使用基于jackson的 typeReference，这个是用于兼容现有的项目，以免现有项目更新版本后出现报错
     */
    public static <T> T convertTargetObj(Object from, com.alibaba.fastjson.TypeReference<T> type, boolean clearExts) {
        T t = convertTargetObj(from, type);
        if (clearExts && t instanceof ObjectX) {
            if (((ObjectX)t).getExtensions() != null) {
                ((ObjectX)t).getExtensions().clear();
            }
        }
        return t;
    }

    public static <T> T convertTargetObj(Collection<? extends Object> from, TypeReference<T> type) {
        try {
            return DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(from), type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * PS: 建议使用基于jackson的 typeReference，这个是用于兼容现有的项目，以免现有项目更新版本后出现报错
     */
    public static <T> T convertTargetObj(Collection<? extends Object> from, com.alibaba.fastjson.TypeReference<T> type) {
        return JSON.parseObject(JSON.toJSONString(from), type);
    }

    public static <T> T convertTargetObj(Collection<? extends Object> from, TypeReference<T> type, boolean clearExts) {
        T t = convertTargetObj(from, type);
        if (clearExts && t instanceof ObjectX) {
            if (((ObjectX)t).getExtensions() != null) {
                ((ObjectX)t).getExtensions().clear();
            }
        }
        return t;
    }

    /**
     * PS: 建议使用基于jackson的 typeReference，这个是用于兼容现有的项目，以免现有项目更新版本后出现报错
     */
    public static <T> T convertTargetObj(Collection<? extends Object> from, com.alibaba.fastjson.TypeReference<T> type, boolean clearExts) {
        T t = convertTargetObj(from, type);
        if (clearExts && t instanceof ObjectX) {
            if (((ObjectX)t).getExtensions() != null) {
                ((ObjectX)t).getExtensions().clear();
            }
        }
        return t;
    }

    /**
     * 把 param1 的数据 压到 param2
     *
     * @param from 数据从
     * @param to   数据到
     */
    public static void mergeProperties(Object from, ObjectX to) {
        Class<? extends ObjectX> toClass = to.getClass();

        BaseObjectX mergeVO = new BaseObjectX();

        // 目标
        try {
            DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(to), BaseObjectX.class)
                    .getExtensions().forEach(mergeVO::putX);
            DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(from), BaseObjectX.class)
                    .getExtensions().forEach(mergeVO::putX);

            ObjectX toVO = DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(mergeVO), toClass);
            BeanUtils.copyProperties(toVO, to);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static <T> void mergePropertiesOnlyInSpecifiedClassWithoutExts(Object from, ObjectX to, Class<T> specifiedClass) {
        // 1: 获取指定类及其父类的字段
        Set<String> specifiedFieldNames = new HashSet<>(64); {
            Class<?> clazz = specifiedClass;
            while (clazz != null) {
                for (Field declaredField : specifiedClass.getDeclaredFields()) {
                    specifiedFieldNames.add(declaredField.getName());
                }

                // 追溯父类
                clazz = clazz.getSuperclass();
            }
        }

        Class<? extends ObjectX> toClass = to.getClass();

        BaseObjectX mergeVO = new BaseObjectX();

        // 目标
        try {
            DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(to), BaseObjectX.class)
                    .getExtensions().forEach(mergeVO::putX);
            DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(from), BaseObjectX.class)
                    .getExtensions().forEach((k, v) -> {
                if (specifiedFieldNames.contains(k)) {
                    mergeVO.putX(k, v);
                }
            });

            ObjectX toVO = DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(mergeVO), toClass);
            BeanUtils.copyProperties(toVO, to);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static void mergeProperties(Object from, ObjectX to, boolean clearExts) {
        mergeProperties(from, to);
        if (clearExts && to.getExtensions() != null) {
            to.getExtensions().clear();
        }
    }

    public static <T> void mergePropertiesIgnoreFields(Object from, ObjectX to, SFunction<? super T, ?>... ignoreFields) {
        // 拿到不需要set null的字段
        Set<String> ignoreFieldNames = new HashSet<>(ignoreFields.length); {
            SerializedLambda fieldLambda;
            String fieldName;
            Field field;
            try {
                for (SFunction<? super T, ?> notNullField : ignoreFields) {
                    fieldLambda = SrmLambdaUtil.getSerializedLambda(notNullField);
                    fieldName = PropertyNamer.methodToProperty(fieldLambda.getImplMethodName());
                    field = Class.forName(SrmLambdaUtil.normalizedName(fieldLambda.getImplClass())).getDeclaredField(fieldName);
                    ignoreFieldNames.add(field.getName());
                }
            } catch (Exception e) {
                throw new ApiCheckException("方法调用错误，字段不存在:{0}", e.getMessage());
            }
        }

        Class<? extends ObjectX> toClass = to.getClass();

        BaseObjectX mergeVO = new BaseObjectX();

        // 目标
        try {
            DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(to), BaseObjectX.class)
                    .getExtensions().forEach(mergeVO::putX);
            // 来源(相同key时，来源会覆盖目标)
            DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(from), BaseObjectX.class)
                    .getExtensions().forEach((k, v) -> {
                if (!ignoreFieldNames.contains(k)) {
                    mergeVO.putX(k, v);
                }
            });
            ObjectX toVO = DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(mergeVO), toClass);
            BeanUtils.copyProperties(toVO, to);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static <T> void mergePropertiesIgnoreFieldsWithoutExts(Object from, ObjectX to, SFunction<? super T, ?>... ignoreFields) {
        // 拿到不需要set null的字段
        Set<String> ignoreFieldNames = new HashSet<>(ignoreFields.length); {
            SerializedLambda fieldLambda;
            String fieldName;
            Field field;
            try {
                for (SFunction<? super T, ?> notNullField : ignoreFields) {
                    fieldLambda = SrmLambdaUtil.getSerializedLambda(notNullField);
                    fieldName = PropertyNamer.methodToProperty(fieldLambda.getImplMethodName());
                    field = Class.forName(SrmLambdaUtil.normalizedName(fieldLambda.getImplClass())).getDeclaredField(fieldName);
                    ignoreFieldNames.add(field.getName());
                }
            } catch (Exception e) {
                throw new ApiCheckException("方法调用错误，字段不存在:{0}", e.getMessage());
            }
        }

        Class<? extends ObjectX> toClass = to.getClass();

        BaseObjectX mergeVO = new BaseObjectX();

        // 目标
        try {
            DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(to), BaseObjectX.class)
                    .getExtensions().forEach(mergeVO::putX);
            // 来源(相同key时，来源会覆盖目标)
            Map<String, Object> from2;
            if (from instanceof ObjectX) {
                ExtensionMap extMap = ((ObjectX)from).getExtensions();
                ((ObjectX)from).setExtensions(new ExtensionMap());
                from2 = SouObjectXUtil.convertTargetObj(from, HashMap.class);
                ((ObjectX)from).setExtensions(extMap);
            } else {
                from2 = SouObjectXUtil.convertTargetObj(from, HashMap.class);
            }
            DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(from2), BaseObjectX.class)
                    .getExtensions().forEach((k, v) -> {
                if (!ignoreFieldNames.contains(k)) {
                    mergeVO.putX(k, v);
                }
            });
            ObjectX toVO = DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(mergeVO), toClass);
            BeanUtils.copyProperties(toVO, to);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static <T> void mergePropertiesOnlySpecified(Object from, ObjectX to, SFunction<? super T, ?>... specifiedFields) {
        String text = "非法的调用";
        if (specifiedFields.length <= 0) { throw new IllegalArgumentException(text); }
        // 拿到需要set的字段
        Set<String> fieldNames = new HashSet<>(specifiedFields.length); {
            SerializedLambda fieldLambda;
            String fieldName;
            Field field;
            try {
                for (SFunction<? super T, ?> notNullField : specifiedFields) {
                    fieldLambda = SrmLambdaUtil.getSerializedLambda(notNullField);
                    fieldName = PropertyNamer.methodToProperty(fieldLambda.getImplMethodName());
                    field = Class.forName(SrmLambdaUtil.normalizedName(fieldLambda.getImplClass())).getDeclaredField(fieldName);
                    fieldNames.add(field.getName());
                }
            } catch (Exception e) {
                throw new ApiCheckException("方法调用错误，字段不存在:{0}", e.getMessage());
            }
        }

        Class<? extends ObjectX> toClass = to.getClass();

        BaseObjectX mergeVO = new BaseObjectX();

        // 目标
        try {
            DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(to), BaseObjectX.class)
                    .getExtensions().forEach(mergeVO::putX);
            // 来源(相同key时，来源会覆盖目标)
            DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(from), BaseObjectX.class)
                    .getExtensions().forEach((k, v) -> {
                if (fieldNames.contains(k)) {
                    mergeVO.putX(k, v);
                }
            });
            ObjectX toVO = DEFAULT_OBJ.readValue(DEFAULT_OBJ.writeValueAsString(mergeVO), toClass);
            SouObjectXUtil.mergeProperties(toVO, to);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static <T> String getFieldByLambda(SFunction<? super T, ?> field) {
        SerializedLambda fieldLambda = null;
        try {
            fieldLambda = SrmLambdaUtil.getSerializedLambda(field);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return PropertyNamer.methodToProperty(fieldLambda.getImplMethodName());
    }

    @SuppressWarnings("rawtypes")
    public static <F, T> List<T> convertList(Collection<F> fromList, Class<T> targetClass) {
        List<T> targetList;
        if (fromList instanceof Page) {
            targetList = new Page<>();
            ((Page)targetList).setTotal(((Page)fromList).getTotal());
            ((Page)targetList).setPageSize(((Page)fromList).getPageSize());
            ((Page)targetList).setPageNum(((Page)fromList).getPageNum());
        } else {
            targetList = new ArrayList<>(fromList.size());
        }
        fromList.forEach(from -> targetList.add(SouObjectXUtil.convertTargetObj(from, targetClass)));
        return targetList;
    }

    @SuppressWarnings("rawtypes")
    public static <F, T> List<T> convertListEmpty(Collection<F> fromList, Class<T> targetClass) {
        List<T> targetList;
        if (fromList instanceof Page) {
            targetList = new Page<>();
            ((Page)targetList).setTotal(((Page)fromList).getTotal());
            ((Page)targetList).setPageSize(((Page)fromList).getPageSize());
            ((Page)targetList).setPageNum(((Page)fromList).getPageNum());
        } else {
            targetList = new ArrayList<>(fromList.size());
        }
        return targetList;
    }

    @SuppressWarnings("rawtypes")
    public static <F> List<Map<String, Object>> convertMapList(Collection<F> fromList) {
        if (fromList.isEmpty()) { return Collections.emptyList(); }
        List<Map<String, Object>> targetList;
        if (fromList instanceof Page) {
            targetList = new Page<>();
            ((Page)targetList).setTotal(((Page)fromList).getTotal());
            ((Page)targetList).setPageSize(((Page)fromList).getPageSize());
            ((Page)targetList).setPageNum(((Page)fromList).getPageNum());
        } else {
            targetList = new ArrayList<>(fromList.size());
        }
        fromList.forEach(from -> targetList.add(SouObjectXUtil.convertTargetObj(from, new TypeReference<Map<String, Object>>() {})));
        return targetList;
    }

}
