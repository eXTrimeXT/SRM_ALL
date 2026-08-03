package com.midea.cloud.srm.biz.pj.sou.comp.process.service.impl;

import com.midea.cloud.srm.biz.pj.sou.comp.process.service.CompSouProcessEventWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouProcessEventService;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.process.ApiCompSouProcessConfigEditDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 竞价 - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouProcessEventWebServiceImpl implements CompSouProcessEventWebService {

    @Autowired
    private SouProcessEventService souProcessEventService;

    /**
     * 采购商端: 编辑/提交流程配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public long/* processConfigId */ editProcessConfig(ApiCompSouProcessConfigEditDTO param, boolean isTempSave) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 保存流程配置
        souProcessEventService.editProcessConfig(param, isTempSave);

        return param.getProcessConfigId();
    }

    /**
     * 生效流程配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void validProcessConfig(long processConfigId) {
        souProcessEventService.validProcessConfig(processConfigId, SouTypeEnum.comp.name());
    }

    /**
     * 失效流程配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void invalidProcessConfig(long processConfigId) {
        souProcessEventService.invalidProcessConfig(processConfigId, SouTypeEnum.comp.name());
    }

    /**
     * 删除流程配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeProcessConfig(long processConfigId) {
        // 1: 删除流程配置
        souProcessEventService.removeProcessConfig(processConfigId, SouTypeEnum.comp.name());
    }

}
