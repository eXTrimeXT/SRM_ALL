package com.midea.cloud.srm.sou.timertasks.service;

import com.midea.cloud.srm.model.sou.timertasks.entity.TimerTaskEntity;

import java.util.Date;
import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
public interface SrmNpmSouTimerTaskService {

    /**
     * 监听任务
     * @param businessId
     * @param buniessType
     * @param startTaskTime
     */
    public void listeningTask(Long businessId, String buniessType, Date startTaskTime);

    /**
     * 增加任务执行器
     * @param taskEntityList
     */
    public void addTimerTask(List<TimerTaskEntity> taskEntityList);

    /**
     * 定时任务监听任务
     */
    public void listeningTaskJob();
}
