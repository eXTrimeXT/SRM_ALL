package com.midea.cloud.srm.sou.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreement;
import com.midea.cloud.srm.model.sou.agreement.enums.AgreementStatusEnums;
import com.midea.cloud.srm.sou.agreement.service.JcAgreementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 集采协议根据协议有效期修改状态
 * @author huangbf3
 */
@Job("agreementStatusJob")
@Slf4j
public class AgreementStatusJob implements ExecuteableJob {

    @Resource
    private JcAgreementService agreementService;

    /**
     * @param params
     * @return
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        try {
            Date date = new Date();
            LambdaQueryWrapper<SccSouJcAgreement> qw = new LambdaQueryWrapper<>();
            qw.in(SccSouJcAgreement::getAgreementStatus, AgreementStatusEnums.EXECUTE.getCode(), AgreementStatusEnums.EXECUTING.getCode());
            List<Long> startIds = new ArrayList<>();
            List<Long> endIds = new ArrayList<>();
            List<SccSouJcAgreement> list = agreementService.list(qw);
            list.forEach(e -> {
                if (date.after(e.getEffectiveEndDate())) {
                    endIds.add(e.getAgreementId());
                } else if (date.after(e.getEffectiveStartDate()) && date.before(e.getEffectiveEndDate())) {
                    startIds.add(e.getAgreementId());
                }
            });
            if (CollectionUtils.isNotEmpty(startIds)) {
                LambdaUpdateWrapper<SccSouJcAgreement> startUpdate = new LambdaUpdateWrapper<>();
                startUpdate.set(SccSouJcAgreement::getAgreementStatus, AgreementStatusEnums.EXECUTING.getCode());
                startUpdate.in(SccSouJcAgreement::getAgreementId, startIds);
                agreementService.update(startUpdate);
            }
            if (CollectionUtils.isNotEmpty(endIds)) {
                LambdaUpdateWrapper<SccSouJcAgreement> endUpdate = new LambdaUpdateWrapper<>();
                endUpdate.set(SccSouJcAgreement::getAgreementStatus, AgreementStatusEnums.STOP.getCode());
                endUpdate.in(SccSouJcAgreement::getAgreementId, endIds);
                agreementService.update(endUpdate);
            }
        } catch (Exception e) {
            log.error("执行定时任务【根据协议有效期修改集采协议状态】异常", e);
            return BaseResult.build(ResultCode.UNKNOWN_ERROR, e.getMessage());
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }
}
