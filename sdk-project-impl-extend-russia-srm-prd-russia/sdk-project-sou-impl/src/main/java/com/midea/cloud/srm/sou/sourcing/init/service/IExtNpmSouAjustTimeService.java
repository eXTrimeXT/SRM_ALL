package com.midea.cloud.srm.sou.sourcing.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouEndTimeDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtNpmSouAjustTime;

/**
 * @description scc_npm_sou_ajust_time
 * @author panmq
 * @date 2023-12-13
 */
public interface IExtNpmSouAjustTimeService extends IService<ExtNpmSouAjustTime> {

    /**
     * 记录调整时间
     * @param souEndTimeDto
     * @return
     */
    public ExtNpmSouAjustTime recordAjustEndTime(ApiExtSouEndTimeDto souEndTimeDto);
}

