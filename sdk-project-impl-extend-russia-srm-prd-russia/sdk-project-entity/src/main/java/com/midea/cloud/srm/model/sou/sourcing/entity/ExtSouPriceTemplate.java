package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@TableName("scc_npm_sou_price_template")
public class ExtSouPriceTemplate extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId
    private Long templateId;
    /**
     * 关联招标基本信息主键ID, -1为所有字段
     */
    private Long projectId;
    /**
     * 属性名
     */
    private String columnCode;
    /**
     * 属性名描述
     */
    private String columnName;
    /**
     * 默认属性名，Y或N
     */
    private String columnDefault;

    /**
     * 字段类型
     */
    private String columnType;
    /**
     * 字段输入来源
     */
    private String columnSource;
    /**
     * 是否可输入，Y或N
     */
    private String colnmnInput;
    /**
     * 排序
     */
    private Integer colnmnSort;

}
