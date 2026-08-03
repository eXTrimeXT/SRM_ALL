package com.midea.cloud.srm.biz.pj.scoreconfigdetail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.pj.scoreconfigdetails.entity.SccPjSouScoreConfigDetail;

import java.util.List;

/**
 * @description
 * @author panmq
 * @date 2023-09-21
 */
public interface ISccPjSouScoreConfigDetailService extends IService<SccPjSouScoreConfigDetail> {

    /**
     * 删除评分项
     * @param configDetailIdList
     */
    public void delScoreConfigDetail(List<Long> configDetailIdList);

    /**
     * 查询评分项
     * @param scoreConfigId
     * @return
     */
    public List<SccPjSouScoreConfigDetail> listDetail(Long scoreConfigId);
}

