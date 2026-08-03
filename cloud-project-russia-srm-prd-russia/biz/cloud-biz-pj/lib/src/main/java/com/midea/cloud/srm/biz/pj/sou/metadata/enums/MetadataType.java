package com.midea.cloud.srm.biz.pj.sou.metadata.enums;

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
 *  修改日期: 2022/7/21 14:31
 *  修改内容:
 * </pre>
 */
public enum MetadataType {
    /**
     * 元数据
     */
    METADATA,
    /**
     * 动态表
     */
    DYNAMIC,
    /**
     * 扩展表
     */
    EXT;

    public static MetadataType get(String value) {
        for (MetadataType o : MetadataType.values()) {
            if (o.toString().equals(value)) {
                return o;
            }
        }
        return null;
    }
}
