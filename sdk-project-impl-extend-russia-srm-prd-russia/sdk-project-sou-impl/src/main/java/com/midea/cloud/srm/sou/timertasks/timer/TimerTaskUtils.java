package com.midea.cloud.srm.sou.timertasks.timer;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.sou.timertasks.service.TimerService;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
@Slf4j
public class TimerTaskUtils {

    private static final Set<Long> CURRENT_TASK = new HashSet<>();

    public static void addTask(TimerService timerService, Long taskId, Date startTaskTime) {

        if(CURRENT_TASK.contains(taskId)) {
            log.info(MessageFormat.format("当前已存在任务：{0}", taskId));
            return;
        }

        CURRENT_TASK.add(taskId);

        log.info(MessageFormat.format("添加任务成功，当前任务列表：{0}", JSON.toJSONString(CURRENT_TASK)));

        Long currentTimes = Calendar.getInstance().getTimeInMillis();
        Long startTaskTimes = startTaskTime.getTime();
        if(startTaskTimes.compareTo(currentTimes) <= 0) {
            timerService.excuteTask(taskId);
            log.info(MessageFormat.format("已执行任务：{0}", taskId));
            CURRENT_TASK.remove(taskId);
        } else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    Long delayTimes = startTaskTimes - Calendar.getInstance().getTimeInMillis();
                    TimeUnit.MILLISECONDS.sleep(delayTimes);
                    timerService.excuteTask(taskId);
                    CURRENT_TASK.remove(taskId);
                    log.info(MessageFormat.format("已执行任务：{0}", taskId));
                } catch (Exception e) {
                    log.error("addTask Exception", e);
                }
            });
            executorService.shutdown();
        }
    }

}
