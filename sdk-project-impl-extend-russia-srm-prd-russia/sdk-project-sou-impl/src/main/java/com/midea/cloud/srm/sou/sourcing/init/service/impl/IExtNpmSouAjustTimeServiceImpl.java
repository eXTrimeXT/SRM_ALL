package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouEndTimeDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtNpmSouAjustTime;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtNpmSouAjustTimeMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtNpmSouAjustTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @description scc_npm_sou_ajust_time
 * @author panmq
 * @date 2023-12-13
 */
@Slf4j
@Service
public class IExtNpmSouAjustTimeServiceImpl extends ServiceImpl<ExtNpmSouAjustTimeMapper, ExtNpmSouAjustTime> implements IExtNpmSouAjustTimeService {
    @Override
    public ExtNpmSouAjustTime recordAjustEndTime(ApiExtSouEndTimeDto souEndTimeDto) {
        ExtNpmSouAjustTime extNpmSouAjustTime = new ExtNpmSouAjustTime();
        BeanCopyUtil.copyProperties(extNpmSouAjustTime, souEndTimeDto);
        extNpmSouAjustTime.setAjustTimeId(IdGenrator.generate());
        this.save(extNpmSouAjustTime);
        return extNpmSouAjustTime;
    }
}

