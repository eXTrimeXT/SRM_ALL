package com.midea.cloud.srm.sou.timertasks.timer;

import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.sou.timertasks.service.impl.TimerServiceImpl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
public class TimerTest {

    private static final int NUM5 = 5;

    public static void main(String[] args) throws Exception {
        Date date = DateUtil.parseDate("2024-03-20 02:19:00");
        for(Long taskId = 0L; taskId < NUM5; taskId++) {
            TimerTaskUtils.addTask(new TimerServiceImpl(), taskId, date);
        }
        TimerTaskUtils.addTask(new TimerServiceImpl(), -1L, DateUtil.parseDate("2024-03-20 02:19:10"));
        TimerTaskUtils.addTask(new TimerServiceImpl(), 100L, DateUtil.parseDate("2024-03-20 02:19:20"));

        TimerTaskUtils.addTask(new TimerServiceImpl(), -1L, DateUtil.parseDate("2024-03-20 02:15:30"));
        TimerTaskUtils.addTask(new TimerServiceImpl(), 100L, DateUtil.parseDate("2024-03-20 02:15:20"));

        TimeUnit.MILLISECONDS.sleep(15000);
        TimerTaskUtils.addTask(new TimerServiceImpl(), -1L, DateUtil.parseDate("2024-03-20 02:20:00"));
        TimerTaskUtils.addTask(new TimerServiceImpl(), 200L, DateUtil.parseDate("2024-03-20 02:16:20"));

        System.out.println(121213);
    }
}
