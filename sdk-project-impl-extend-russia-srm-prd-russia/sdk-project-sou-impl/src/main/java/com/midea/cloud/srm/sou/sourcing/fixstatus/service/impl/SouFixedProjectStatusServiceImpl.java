package com.midea.cloud.srm.sou.sourcing.fixstatus.service.impl;

import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.sourcing.fixstatus.service.SouFixedProjectStatusService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtNpmSouOpenTodoService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.init.fixedprojectstatuss.ApiExtSouFixedProjectStatusHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class SouFixedProjectStatusServiceImpl implements SouFixedProjectStatusService {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ExtNpmSouOpenTodoService extNpmSouOpenTodoService;

    @Autowired
    private RedisUtil redisUtil;

    private static final String FIXED_PROJECT_STATUS = "FIXED_PROJECT_STATUS";

    @Override
    @SneakyThrows(value = {Exception.class})
    public void fixedProjectStatusAll(String souType) {
        Integer fixCount = fixedAll(souType).get();
        log.info("已修复状态数{}", fixCount);
    }

    @SneakyThrows(value = {Exception.class})
    @Async
    @Transactional(rollbackFor = Exception.class)
    protected Future<Integer> fixedAll(String souType) {
        AtomicInteger count = new AtomicInteger(0);
        String lockKey = StringUtils.joinWith("_", FIXED_PROJECT_STATUS, "ALL");
        AtomicInteger tryTimes = new AtomicInteger(0);
        while (redisUtil.hasLock(lockKey)) {
            Thread.sleep(1000);
            if(Integer.compare(tryTimes.getAndAdd(1), 5) >= 0) {
                return new AsyncResult<>(count.get());
            }
        }
        long num2000 = 2000;
        if(redisUtil.tryLock(lockKey, num2000, TimeUnit.MICROSECONDS)) {
            try {
                List<ExtSouProject> techList = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouFixedProjectStatusHandler.class).judgeNeedFixedProjectStatusAsTechBid(souType);
                if(CollectionUtils.isNotEmpty(techList)) {
                    count.getAndAdd(techList.size());
                    projectService.updateBatchById(techList);
                    extNpmSouOpenTodoService.sendTodo(techList);
                }

                List<ExtSouProject> busList = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouFixedProjectStatusHandler.class).judgeNeedFixedProjectStatusAsBusBid(souType);
                if(CollectionUtils.isNotEmpty(busList)) {
                    count.getAndAdd(busList.size());
                    projectService.updateBatchById(busList);
                    extNpmSouOpenTodoService.sendTodo(busList);
                }
            } catch (Exception e) {
                log.error("修复单据状态异常", e);
            } finally {
                redisUtil.unLock(lockKey);
            }
        }
        return new AsyncResult<>(count.get());
    }

    @SneakyThrows(value = {Exception.class})
    @Override
    public ExtSouProject fixedProjectStatus(ExtSouProject project, String souType) {

        Boolean fixedFlag = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouFixedProjectStatusHandler.class).judgeNeedFixedProjectStatus(project, souType);
        if(fixedFlag) {
            SouBiddingProStatusEnum proStatusEnum = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouFixedProjectStatusHandler.class).getFixedProjectStatus(project, souType);
            return fixed(project, proStatusEnum, souType).get();
        }
        return project;
    }

    @SneakyThrows(value = {Exception.class})
    @Async
    @Transactional(rollbackFor = Exception.class)
    protected Future<ExtSouProject> fixed(ExtSouProject project, SouBiddingProStatusEnum proStatusEnum, String souType) {
        if(Objects.isNull(proStatusEnum)) {
            return new AsyncResult<>(project);
        }

        String lockKey = StringUtils.joinWith("_", FIXED_PROJECT_STATUS, project.getProjectId());
        AtomicInteger cound = new AtomicInteger(0);
        while (redisUtil.hasLock(lockKey)) {
            Thread.sleep(1000);
            if(Integer.compare(cound.getAndAdd(1), 5) >= 0) {
                return new AsyncResult<>(project);
            }
        }
        long num2000 = 2000;
        if(redisUtil.tryLock(lockKey, num2000, TimeUnit.MICROSECONDS)) {
            try {
                project.setProjectStatus(proStatusEnum.getCode());
                projectService.updateById(project);
                extNpmSouOpenTodoService.sendTodo(Collections.singletonList(project));
            } catch (Exception e) {
                log.error("修复单据状态异常", e);
            } finally {
                redisUtil.unLock(lockKey);
            }
        }
        return new AsyncResult<>(project);
    }
}
