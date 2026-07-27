package com.midea.cloud.srm.biz.pj.scoreconfigdetail.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.scoreconfig.mapper.SccPjSouScoreConfigMapper;
import com.midea.cloud.srm.biz.pj.scoreconfigdetail.mapper.SccPjSouScoreConfigDetailMapper;
import com.midea.cloud.srm.biz.pj.scoreconfigdetail.service.ISccPjSouScoreConfigDetailService;
import com.midea.cloud.srm.model.pj.enums.SourcePubconfigStatusEnum;
import com.midea.cloud.srm.model.pj.scoreconfig.entity.SccPjSouScoreConfig;
import com.midea.cloud.srm.model.pj.scoreconfigdetails.entity.SccPjSouScoreConfigDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description
 * @author panmq
 * @date 2023-09-21
 */
@Slf4j
@Service
public class ISccPjSouScoreConfigDetailServiceImpl extends ServiceImpl<SccPjSouScoreConfigDetailMapper, SccPjSouScoreConfigDetail> implements ISccPjSouScoreConfigDetailService {

    @Resource
    private SccPjSouScoreConfigMapper sccPjSouScoreConfigMapper;

    @Override
    public void delScoreConfigDetail(List<Long> configDetailIdList) {
        if(CollectionUtils.isEmpty(configDetailIdList)) {
            return;
        }

        LambdaQueryWrapper<SccPjSouScoreConfigDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SccPjSouScoreConfigDetail::getConfigDetailId, configDetailIdList);

        List<SccPjSouScoreConfigDetail> detailList = this.list(queryWrapper);

        if(CollectionUtils.isEmpty(detailList)) {
            return;
        }

        LambdaQueryWrapper<SccPjSouScoreConfig> queryHeadWrapper = new LambdaQueryWrapper<>();
        queryHeadWrapper.in(SccPjSouScoreConfig::getScoreConfigId, detailList.stream().map(SccPjSouScoreConfigDetail::getScoreConfigId).distinct().collect(Collectors.toList()));
        queryHeadWrapper.eq(SccPjSouScoreConfig::getStatus, SourcePubconfigStatusEnum.VALID.getCode());

        Integer count = Math.toIntExact(sccPjSouScoreConfigMapper.selectCount(queryHeadWrapper));

        if(Integer.compare(count, 0) == 1) {
            throw new BaseException("评分项中存在生效的模板，不允许删除");
        }

        this.removeByIds(detailList.stream().map(SccPjSouScoreConfigDetail::getConfigDetailId).collect(Collectors.toList()));
    }

    @Override
    public List<SccPjSouScoreConfigDetail> listDetail(Long scoreConfigId) {
        return this.list(new LambdaQueryWrapper<SccPjSouScoreConfigDetail>().eq(SccPjSouScoreConfigDetail::getScoreConfigId, scoreConfigId));
    }
}

