package com.midea.cloud.srm.sou.timertasks.service.impl;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.timertasks.entity.TimerTaskEntity;
import com.midea.cloud.srm.sou.timertasks.service.SrmNpmSouTimerTaskService;
import com.midea.cloud.srm.sou.timertasks.service.TimerService;
import com.midea.cloud.srm.sou.timertasks.timer.TimerTaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
@Slf4j
@Service
public class SrmNpmSouTimerTaskServiceImpl implements SrmNpmSouTimerTaskService {

    @Autowired
    private QlService qlService;

    @Autowired
    private TimerService timerService;

    /**
     * 5分钟
     */
    private static final Long DELAY_LIMIT = 1000*60*5L;

    @Override
    public void listeningTask(Long businessId, String buniessType, Date startTaskTime) {
        log.info(MessageFormat.format("监听待执行任务 {0} {1}", businessId, buniessType));
        qlService.deleteByWrapper(QlWrappers.update(MqlType.TIMER_TASK).eq(TimerTaskEntity::getBusinessId,businessId).eq(TimerTaskEntity::getBusinessType, buniessType));

        TimerTaskEntity timerTaskEntity = new TimerTaskEntity();
        timerTaskEntity.setBusinessId(businessId);
        timerTaskEntity.setBusinessType(buniessType);
        timerTaskEntity.setTaskStartTime(startTaskTime);

        qlService.save(MqlType.TIMER_TASK, Collections.singletonList(timerTaskEntity));

        addTimerTask(queryTimerTask(businessId, buniessType));

    }

    @Override
    public void addTimerTask(List<TimerTaskEntity> taskEntityList) {
        if(CollectionUtils.isNotEmpty(taskEntityList)) {
            taskEntityList.stream().forEach(task -> {
                Long delayTimes = task.getTaskStartTime().getTime() - Calendar.getInstance().getTimeInMillis();
                if(delayTimes.compareTo(DELAY_LIMIT) <= 0) {
                    TimerTaskUtils.addTask(timerService, task.getTaskId(), task.getTaskStartTime());
                }
            });
        }
    }

    private List<TimerTaskEntity> queryTimerTask(Long businessId, String buniessType) {
        List<TimerTaskEntity> taskEntityList = qlService.queryByWrapper(QlWrappers.query(MqlType.TIMER_TASK).eq(TimerTaskEntity::getBusinessId,businessId).eq(TimerTaskEntity::getBusinessType, buniessType), TimerTaskEntity.class);

        return taskEntityList;
    }

    @Override
    public void listeningTaskJob() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, DELAY_LIMIT.intValue());

        List<TimerTaskEntity> taskEntityList = qlService.queryByWrapper(QlWrappers.query(MqlType.TIMER_TASK).le(TimerTaskEntity::getTaskStartTime, calendar.getTime()), TimerTaskEntity.class);

        if(CollectionUtils.isNotEmpty(taskEntityList)) {
            log.info(MessageFormat.format("定时监听到邻近定时任务：{0}", JSON.toJSONString(taskEntityList.stream().map(t->t.getTaskId()).collect(Collectors.toList()))));
            addTimerTask(taskEntityList);
        }
    }
}
