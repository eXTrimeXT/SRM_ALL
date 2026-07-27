package com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/2 11:24
 *  修改内容:
 * </pre>
 */
public enum ExtCatalogOnShelvesStatusEnum {
    /**
     * 待上架
     */
    TO_BE_ON_SHELVES,
    /**
     * 已上架
     */
    ON_SHELVES,
    /**
     * 已下架
     */
    OFF_SHELVES,
    /**
     * 已定时待上架
     */
    SCHEDULED_SHELVES,
    /**
     * 未定时待上架
     */
    UNTIMED_SHELVES
    ;
}
