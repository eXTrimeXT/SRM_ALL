package com.midea.cloud.srm.sou.timertasks.service.impl;

import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.timertasks.entity.TimerTaskEntity;
import com.midea.cloud.srm.sou.timertasks.service.TimerService;
import com.midea.cloud.srm.sou.timertasks.service.TimerTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
@Service
@Slf4j
public class TimerServiceImpl implements TimerService {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private QlService qlService;

    @Autowired
    private Map<String, TimerTaskService> taskMap;

    private static final String SRM_TIMER_TASK_LOCK = "SRM_TIMER_TASK_LOCK";

    private static final Long TIMER_TASK_LOCK_EXPIRES = 60L;

    @Override
    public void excuteTask(Long taskId) {
        log.info(MessageFormat.format("启动任务 {0}", taskId));

        String lockKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, SRM_TIMER_TASK_LOCK, taskId);
        Boolean lock = redisUtil.hasLock(lockKey);
        if(lock) {
            log.info(MessageFormat.format("竞争锁失败-{0}", taskId));
            return;
        }
        if(!redisUtil.tryLock(lockKey, TIMER_TASK_LOCK_EXPIRES, TimeUnit.SECONDS)) {
            log.info(MessageFormat.format("获取锁失败-{0}", taskId));
            return;
        }

        log.info(MessageFormat.format("竞争锁成功-{0}", taskId));

        TimerTaskEntity timerTaskEntity = qlService.readByKey(MqlType.TIMER_TASK, taskId, TimerTaskEntity.class);
        if(Objects.isNull(timerTaskEntity)) {
            log.info(MessageFormat.format("任务已失效-{0}", taskId));
        } else {
            TimerTaskService timerTaskService = taskMap.get(timerTaskEntity.getBusinessType());
            if(Objects.isNull(timerTaskService)) {
                log.info(MessageFormat.format("任务无业务逻辑实现类-{0}", taskId));
            } else {
                timerTaskService.startTask(timerTaskEntity);
            }
            qlService.deleteByKeys(MqlType.TIMER_TASK, Collections.singletonList(taskId));
        }
        redisUtil.unLock(lockKey);
        log.info(MessageFormat.format("释放锁成功-{0}", taskId));
        log.info(MessageFormat.format("结束任务 {0}", taskId));
    }
}
