package com.midea.cloud.srm.biz.pj.sou.sourcing.spi;

import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;

/**
 * 寻源核心 - 定义SPI bean
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/09
 */
public interface ISouSpiBean {

    /**
     * 说明该实现类是给寻源中哪个服务使用的
     * @see SouTypeEnum
     * @return
     */
    String matchModule();

    /**
     * 获取优先级，优先级越高的先被使用
     * @return
     */
    int getOrder();

}
