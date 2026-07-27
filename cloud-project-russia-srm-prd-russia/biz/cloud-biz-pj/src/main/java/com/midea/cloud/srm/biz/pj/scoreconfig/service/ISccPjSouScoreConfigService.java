package com.midea.cloud.srm.biz.pj.scoreconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.pj.scoreconfig.dto.SccPjSouScoreConfigDto;
import com.midea.cloud.srm.model.pj.scoreconfig.entity.SccPjSouScoreConfig;

import java.util.List;

/**
 * @description
 * @author panmq
 * @date 2023-09-21
 */
public interface ISccPjSouScoreConfigService extends IService<SccPjSouScoreConfig> {

    /**
     * 分页查询
     * @param sccPjSouScoreConfig
     * @return
     */
    public PageInfo<SccPjSouScoreConfig> queryPage(SccPjSouScoreConfig sccPjSouScoreConfig);

    /**
     * 保存数据
     * @param sccPjSouScoreConfigDto
     * @return
     */
    public SccPjSouScoreConfigDto saveScoreConfig(SccPjSouScoreConfigDto sccPjSouScoreConfigDto);

    /**
     * 查询明细
     * @param scoreConfigId
     * @return
     */
    public SccPjSouScoreConfigDto queryScoreConfig(Long scoreConfigId);

    /**
     * 失效
     * @param scoreConfigIdList
     */
    public void invalidScoreConfig(List<Long> scoreConfigIdList);

    /**
     * 删除
     * @param scoreConfigIdList
     */
    public void delScoreConfig(List<Long> scoreConfigIdList);

    /**
     * 查询生效的评分模板配置
     * @return
     */
    public List<SccPjSouScoreConfig> listValidScoreConfig();
}

