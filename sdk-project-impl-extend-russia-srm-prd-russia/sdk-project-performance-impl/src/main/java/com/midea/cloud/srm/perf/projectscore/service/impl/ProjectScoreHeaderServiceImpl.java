package com.midea.cloud.srm.perf.projectscore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.srm.model.perf.projectscore.dto.ProjectScoreHeaderDTO;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreDim;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreInd;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.perf.projectscore.mapper.ProjectScoreHeaderMapper;
import com.midea.cloud.srm.perf.projectscore.service.ProjectScoreDimService;
import com.midea.cloud.srm.perf.projectscore.service.ProjectScoreHeaderService;
import com.midea.cloud.srm.perf.projectscore.service.ProjectScoreIndService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class ProjectScoreHeaderServiceImpl extends BaseServiceImpl<ProjectScoreHeaderMapper, ProjectScoreHeader> implements ProjectScoreHeaderService {

    @Autowired
    private ProjectScoreDimService projectScoreDimService;

    @Autowired
    private ProjectScoreIndService projectScoreIndService;

    @Override
    public List<ProjectScoreHeader> listPage(ProjectScoreHeader queryDTO) {
        LambdaQueryWrapper<ProjectScoreHeader> wrapper = Wrappers.lambdaQuery(ProjectScoreHeader.class);
        if (queryDTO != null) {
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getProjectName()), ProjectScoreHeader::getProjectName, queryDTO.getProjectName());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getContractName()), ProjectScoreHeader::getContractName, queryDTO.getContractName());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getCompanyName()), ProjectScoreHeader::getCompanyName, queryDTO.getCompanyName());
            wrapper.eq(StringUtils.isNotEmpty(queryDTO.getPerformanceCode()), ProjectScoreHeader::getPerformanceCode, queryDTO.getPerformanceCode());
            wrapper.eq(StringUtils.isNotEmpty(queryDTO.getPerformanceType()), ProjectScoreHeader::getPerformanceType, queryDTO.getPerformanceType());
            wrapper.eq(StringUtils.isNotEmpty(queryDTO.getProjectStatus()), ProjectScoreHeader::getProjectStatus, queryDTO.getProjectStatus());
            wrapper.eq(queryDTO.getCompanyId() != null, ProjectScoreHeader::getCompanyId, queryDTO.getCompanyId());
            wrapper.eq(queryDTO.getCategoryId() != null, ProjectScoreHeader::getCategoryId, queryDTO.getCategoryId());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getCategoryName()), ProjectScoreHeader::getCategoryName, queryDTO.getCategoryName());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getBidManagerFullPath()), ProjectScoreHeader::getBidManagerFullPath, queryDTO.getBidManagerFullPath());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getContractManagerFullPath()), ProjectScoreHeader::getContractManagerFullPath, queryDTO.getContractManagerFullPath());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getOuOrganizationName()), ProjectScoreHeader::getOuOrganizationName, queryDTO.getOuOrganizationName());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getBuOrganizationName()), ProjectScoreHeader::getBuOrganizationName, queryDTO.getBuOrganizationName());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getBidCode()), ProjectScoreHeader::getBidCode, queryDTO.getBidCode());
            wrapper.in(CollectionUtils.isNotEmpty(queryDTO.getLevelNames()), ProjectScoreHeader::getLevelName, queryDTO.getLevelNames());
            // 增加评分大于和小于查询条件
            wrapper.ge(queryDTO.getScoreStart() != null , ProjectScoreHeader::getScore, queryDTO.getScoreStart());
            wrapper.le(queryDTO.getScoreEnd()!= null, ProjectScoreHeader::getScore, queryDTO.getScoreEnd());
            // 增加评分时间大于和小于查询条件
            wrapper.ge(queryDTO.getCalcDateStart()!= null, ProjectScoreHeader::getLastUpdateDate, queryDTO.getCalcDateStart());
            wrapper.le(queryDTO.getCalcDateEnd()!= null, ProjectScoreHeader::getLastUpdateDate, queryDTO.getCalcDateEnd());
            //增加供应商列表 查询条件
            wrapper.in(CollectionUtils.isNotEmpty(queryDTO.getCompanyIdList()), ProjectScoreHeader::getCompanyId, queryDTO.getCompanyIdList());

        }
        wrapper.orderByDesc(ProjectScoreHeader::getLastUpdateDate);
        return this.list(wrapper);

    }

    @Override
    public ProjectScoreHeaderDTO getDetailById(Long scoreHeaderId) {
        ProjectScoreHeader projectScoreHeader = this.getById(scoreHeaderId);
        ProjectScoreHeaderDTO result = new ProjectScoreHeaderDTO();
        BeanCopyUtil.copyProperties(result, projectScoreHeader);

        List<ProjectScoreDim> dimList = projectScoreDimService.list(ProjectScoreDim::getScoreHeaderId, scoreHeaderId);
        List<ProjectScoreInd> indList = projectScoreIndService.list(ProjectScoreInd::getScoreHeaderId, scoreHeaderId);
        Map<Long, List<ProjectScoreInd>> dimIdMap = indList.stream().collect(Collectors.groupingBy(ProjectScoreInd::getScoreDimId));

        for (ProjectScoreDim projectScoreDim : dimList) {
            List<ProjectScoreInd> indTempList = dimIdMap.get(projectScoreDim.getScoreDimId());
            projectScoreDim.getIndList().addAll(indTempList);
        }
        result.setDimList(dimList);
        return result;
    }

}
