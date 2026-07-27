package com.midea.cloud.srm.biz.pj.sou.sourcing.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

/**
 * @description:
 * @author: huanglj50@meicloud.com
 * @date: 2022/9/14 16:05
 */
public class SouUtils {

    /** 便捷验证String null/超长 */
    public static String validateStringNullAndLength(boolean isTempSave, @Nullable String value,
                                               @Nullable String defaultValue, int maxLength,
                                               String nullMsg, String lengthMsg) {
        value = StringUtils.trimToNull(value);
        if (value == null) {
            if (defaultValue != null) {
                return defaultValue;
            } else {
                if (!isTempSave) {
                    throw new IllegalArgumentException(nullMsg);
                }
            }
        } else {
            if (value.length() > maxLength ) {
                throw new IllegalArgumentException(lengthMsg);
            }
        }
        return value;
    }

}
