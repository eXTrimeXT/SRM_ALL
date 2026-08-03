package com.midea.cloud.srm.biz.pj.sou.comp.process.service;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.process.ApiCompSouProcessConfigEditDTO;

/**
 * 竞价 - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
public interface CompSouProcessEventWebService {

    /**
     * 采购商端: 编辑/提交流程配置
     * @param param 编辑信息
     * @param isTempSave true-暂存/false-提交
     * @return
     */
    long/* processConfigId */ editProcessConfig(ApiCompSouProcessConfigEditDTO param, boolean isTempSave);

    /**
     * 采购商端: 生效流程配置
     * @param processConfigId 流程配置ID
     */
    void validProcessConfig(long processConfigId);

    /**
     * 采购商端: 失效流程配置
     * @param processConfigId 流程配置ID
     */
    void invalidProcessConfig(long processConfigId);

    /**
     * 采购商端: 删除流程配置
     * @param processConfigId 流程配置ID
     */
    void removeProcessConfig(long processConfigId);

}
