package com.midea.cloud.srm.supcooperate.mtmapping.service;


import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.supcooperate.ext.JdMsgPush;

/**
 * 京东推送信息表（内部商城）
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2024-03-12
 */
public interface JdMsgPushService extends BaseService<JdMsgPush> {

    /**
     * 拉取京东推送信息和删除已经拉取到的信息
     */
    void saveAndDeleteJdMsg();
}
