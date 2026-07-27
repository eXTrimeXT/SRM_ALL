package com.midea.cloud.srm.mall.commons;

import javax.annotation.Nullable;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
public interface ResultCode {
    /**
     * getCode
     * @return
     */
    @Nullable
    String getCode();

    /**
     * getMessage
     * @return
     */
    @Nullable
    String getMessage();
}
