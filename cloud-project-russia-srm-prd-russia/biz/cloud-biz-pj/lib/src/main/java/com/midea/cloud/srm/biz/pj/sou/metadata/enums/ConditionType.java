package com.midea.cloud.srm.biz.pj.sou.metadata.enums;

/**
 * <pre>
 * 查询条件类型，与MyBatisPlus的QueryWrapper的条件逻辑一致
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/6/27 16:37
 *  修改内容:
 * </pre>
 */
public enum ConditionType {
    /**
     * 等于
     */
    EQ,
    /**
     * 非模糊
     */
    NOT_LIKE,
    /**
     * 模糊
     */
    LIKE,
    /**
     * 不等于
     */
    NE,
    /**
     * 大于
     */
    GT,
    /**
     * 大于等于
     */
    GE,
    /**
     * 小于
     */
    LT,
    /**
     * 小于等于
     */
    LE,
    /**
     * 在...之间
     */
    BETWEEN,
    /**
     * 包含
     */
    IN,
    /**
     * 不包含
     */
    NOT_IN,
    /**
     * 为空
     */
    IS_NULL,
    /**
     * 不为空
     */
    IS_NOT_NULL,
    /**
     * 正序排序
     */
    ASC,
    /**
     * 反序排序
     */
    DESC,
}
