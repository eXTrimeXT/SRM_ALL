package com.midea.cloud.srm.perf.overallscore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.perf.scoreproject.ScoreItemsProjectStatusEnum;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.model.perf.overallscore.dto.PjPerfOverallScore;
import com.midea.cloud.srm.model.perf.scoring.PerfIndDimScoreDetail;
import com.midea.cloud.srm.model.perf.scoring.PerfIndicatorDimScore;
import com.midea.cloud.srm.model.perf.scoring.PerfOverallScore;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.perf.overallscore.mapper.PjPerfOverallScoreMapper;
import com.midea.cloud.srm.perf.overallscore.service.IPjPerfOverallScoreService;
import com.midea.cloud.srm.perf.scoring.mapper.PerfOverallScoreMapper;
import com.midea.cloud.srm.perf.scoring.service.IPerfIndDimScoreDetailService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <pre>
 *  绩效评分项目成绩查询 服务实现类
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-06-06 15:10:36
 *  修改内容:
 * </pre>
 */
@Service
public class PjPerfOverallScoreServiceImpl extends BaseServiceImpl<PjPerfOverallScoreMapper, PjPerfOverallScore> implements IPjPerfOverallScoreService {

    @Autowired
    private PerfOverallScoreMapper perfOverallScoreMapper;

    @Autowired
    private IPerfIndDimScoreDetailService perfIndDimScoreDetailService;

    @Override
    public List<PjPerfOverallScore> listPage(PjPerfOverallScore perfOverallScore) {
        List<PjPerfOverallScore> overallScoreList = new ArrayList<>();
        LambdaQueryWrapper<PjPerfOverallScore> wrapper = Wrappers.lambdaQuery(PjPerfOverallScore.class);
        wrapper.eq(Objects.nonNull(perfOverallScore.getScoreItemsId()), PjPerfOverallScore::getScoreItemsId, perfOverallScore.getScoreItemsId());
        wrapper.like(StringUtils.isNotEmpty(perfOverallScore.getProjectName()), PjPerfOverallScore::getProjectName, perfOverallScore.getProjectName());
        wrapper.eq(Objects.nonNull(perfOverallScore.getCompanyId()), PjPerfOverallScore::getCompanyId, perfOverallScore.getCompanyId());
        wrapper.like(StringUtils.isNotEmpty(perfOverallScore.getCompanyName()), PjPerfOverallScore::getCompanyName, perfOverallScore.getCompanyName());
        wrapper.eq(Objects.nonNull(perfOverallScore.getOrganizationId()), PjPerfOverallScore::getOrganizationId, perfOverallScore.getOrganizationId());
        wrapper.eq(StringUtils.isNotEmpty(perfOverallScore.getOrganizationName()), PjPerfOverallScore::getOrganizationName, perfOverallScore.getOrganizationName());
        wrapper.eq(Objects.nonNull(perfOverallScore.getCategoryId()), PjPerfOverallScore::getCategoryId, perfOverallScore.getCategoryId());
        wrapper.eq(StringUtils.isNotEmpty(perfOverallScore.getCategoryName()), PjPerfOverallScore::getCategoryName, perfOverallScore.getCategoryName());
        wrapper.in(CollectionUtils.isNotEmpty(perfOverallScore.getLevelNames()), PjPerfOverallScore::getLevelName, perfOverallScore.getLevelNames());
        wrapper.like(StringUtils.isNotEmpty(perfOverallScore.getTemplateName()), PjPerfOverallScore::getTemplateName, perfOverallScore.getTemplateName());
        wrapper.ge(perfOverallScore.getPerStartMonth() != null, PjPerfOverallScore::getPerStartMonth, perfOverallScore.getPerStartMonth());
        wrapper.le(perfOverallScore.getPerEndMonth() != null, PjPerfOverallScore::getPerEndMonth, perfOverallScore.getPerEndMonth());
        /** 根据用户类型查询(采购商能查询所有路，供应商只能查看自己且状态为结果已发布的) */
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        // 用户类型
        String userType = "";
        // 供应商ID
        Long userCompanyId = null;
        if (null != user) {
            userType = user.getUserType();
            userCompanyId = user.getCompanyId();
        }
        // 供应商只能查看自己的
        String vendor = "VENDOR";
        if (vendor.equals(userType)) {
            wrapper.eq(PjPerfOverallScore::getCompanyId, userCompanyId);
            wrapper.eq(PjPerfOverallScore::getStatus, ScoreItemsProjectStatusEnum.RESULT_PUBLISHED.getValue());
        }
        wrapper.orderByDesc(PjPerfOverallScore::getLastUpdateDate);
        overallScoreList = this.list(wrapper);
        return overallScoreList;
    }

    @Override
    public List<PerfOverallScore> findOverallScoreAndSonList(PerfOverallScore queryOverallScore) {
        try {
            List<PerfOverallScore> resultList = perfOverallScoreMapper.findOverallScorelList(queryOverallScore);
            // 补全evaluation
            if (CollectionUtils.isNotEmpty(resultList) && null != resultList.get(0)) {
                PerfOverallScore overallScore = resultList.get(0);
                List<PerfIndDimScoreDetail> dimScoreDetailList = perfIndDimScoreDetailService.list(Wrappers.lambdaQuery(PerfIndDimScoreDetail.class)
                        .eq(PerfIndDimScoreDetail::getScoreItemsId,overallScore.getScoreItemsId()));
                if(CollectionUtils.isNotEmpty(dimScoreDetailList)){
                    Map<Long, String> idMap = dimScoreDetailList.stream().collect(Collectors.toMap(PerfIndDimScoreDetail::getIndDimScoreDetailId, PerfIndDimScoreDetail::getEvaluation));
                    for (PerfIndicatorDimScore dimScore : overallScore.getPerfIndicatorDimScoreList()) {
                        for (PerfIndDimScoreDetail perfIndDimScoreDetail : dimScore.getPerfIndDimScoreDetailList()) {
                            perfIndDimScoreDetail.setEvaluation(idMap.get(perfIndDimScoreDetail.getIndDimScoreDetailId()));
                        }
                    }
                }
            }
            return resultList;
        } catch (Exception e) {
            log.error("根据条件获绩效指标绩效得分主表和子表集合(用于连表查询)时报错：", e);
            throw new BaseException(ResultCode.UNKNOWN_ERROR.getMessage());
        }
    }
}
