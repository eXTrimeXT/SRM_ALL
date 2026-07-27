package com.midea.cloud.srm.sou.timertasks.service.impl;

import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.timertasks.entity.TimerTaskEntity;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiExtSouInitQueryHandler;
import com.midea.cloud.srm.sou.timertasks.service.TimerTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
@Service("OPEN_BUSINESS_BID")
@Slf4j
public class TimerTaskOpenBusinessBidImpl implements TimerTaskService {

    @Override
    public void startTask(TimerTaskEntity task) {
        log.info(MessageFormat.format("开始任务-{0}: {1}", task.getBusinessId(), task.getBusinessType()));

        String souType = SouTypeEnum.bid.name();
        Long projectId = task.getBusinessId();
        //修改单据截止态
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitQueryHandler.class).doHandlerBeforeGetProjectInfo(projectId, souType);
    }
}
