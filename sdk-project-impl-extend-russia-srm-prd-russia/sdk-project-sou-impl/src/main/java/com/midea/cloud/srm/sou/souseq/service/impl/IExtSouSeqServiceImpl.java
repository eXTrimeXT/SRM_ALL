package com.midea.cloud.srm.sou.souseq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.common.utils.redis.RedisLockUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.model.sou.souseq.entity.ExtSouSeq;
import com.midea.cloud.srm.sou.souseq.mapper.ExtSouSeqMapper;
import com.midea.cloud.srm.sou.souseq.service.IExtSouSeqService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.FutureResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description scc_npm_sou_seq
 * @author panmq
 * @date 2023-10-19
 */
@Slf4j
@Service
public class IExtSouSeqServiceImpl extends ServiceImpl<ExtSouSeqMapper, ExtSouSeq> implements IExtSouSeqService {
    @Autowired
    private RedisUtil redisUtil;

    private static final String SEQ_LOCK_SOU = "SEQ_LOCK_SOU";

    @SneakyThrows(value = {Exception.class})
    @Override
    public String getSerial(String prefix, String seqControl, String dateControl, Long digit) {

        return serial(prefix, seqControl, dateControl, digit).get();
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    protected Future<String> serial(String prefix, String seqControl, String dateControl, Long digit) throws Exception {
        if(Objects.isNull(seqControl)) {
            seqControl = "";
        }
        String lockKey = StringUtils.joinWith("_", SEQ_LOCK_SOU, prefix);
        AtomicInteger count = new AtomicInteger(1);
        while (redisUtil.hasLock(lockKey)) {
            Thread.sleep(1000);
            Integer times = count.getAndAdd(1);
            if(times > 5) {
                throw new BaseException("等待锁次数超出预设范围");
            }
        }
        long expireTime = 5000;
        if(redisUtil.tryLock(lockKey, expireTime, TimeUnit.MICROSECONDS)) {
            PageUtil.startPage(1, 1);
            LambdaQueryWrapper<ExtSouSeq> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ExtSouSeq::getSeqPrefix, prefix);
            queryWrapper.eq(ExtSouSeq::getSeqControl, seqControl);
            queryWrapper.eq(ExtSouSeq::getDigit, digit);

            List<ExtSouSeq> seqList = this.list(queryWrapper);
            ExtSouSeq seq = new ExtSouSeq();

            if(CollectionUtils.isNotEmpty(seqList)) {
                seq = seqList.get(0);
            } else {
                seq = new ExtSouSeq();
                seq.setSeqId(IdGenrator.generate());
                seq.setSeqControl(seqControl);
                seq.setDateControl(dateControl);
                seq.setSeqPrefix(prefix);
                seq.setDigit(digit);
                seq.setSerial(0L);
            }

            if(!StringUtils.defaultString(seq.getDateControl(), "").equals(dateControl)) {
                seq.setDateControl(dateControl);
                seq.setSerial(0L);
            }

            Long serial = seq.getSerial() + 1;

            seq.setSerial(serial);

            this.saveOrUpdate(seq);

            /** 自动修复精度， 当序号大于当前精度时，精度自动调整，防止重复单号 */
            digit = fixDigit(digit, serial);

            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            nf.setMinimumIntegerDigits(digit.intValue());

            return new AsyncResult<String>(nf.format(serial));

        } else {
            throw new BaseException("获取锁异常");
        }
    }

    /**
     * 自动修复精度， 当序号大于当前精度时，精度自动调整，防止重复单号
     * @param digit
     * @param serial
     * @return
     */
    protected Long fixDigit(Long digit, Long serial) {
        Double maxSerial = Math.pow(10, digit.intValue())-1;
        if(Long.compare(serial, maxSerial.longValue()) == 1) {
            return fixDigit(digit+1, serial);
        }
        return digit;
    }

}

