package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import java.lang.annotation.*;
/**
 * 备注
 * @author huangbf3
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExtInqExcelPropertyValues {

    /**
     * 用于指定具体某列的可选值(用于导出excel的下拉框)
     * PS: 与{@link #dictCode()} 二选一，如果都存在，仅使用values的值
     */
    String[] values() default {};

    /**
     * 用于指定具体某列的可选值(用于导出excel的下拉框)
     * PS: 该值应填写字典值，会自动远程访问base服务去获取dictItemName集合
     */
    String dictCode() default "";

    /**
     * 不使用上面的 values()、dictCode()，而是从单位表中拿数据
     * PS: 专门用于导出字段为单位
     */
    boolean useUnit() default false;

    boolean useTax() default false;

    boolean forSpinner() default true;

    boolean forOutput() default true;

}
