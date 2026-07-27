package com.midea.cloud.srm.sou.timertasks.service;

import com.midea.cloud.srm.model.sou.timertasks.entity.TimerTaskEntity;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
public interface TimerTaskService {

    /**
     * startTask
     * @param task
     */
    public void startTask(TimerTaskEntity task);
}
