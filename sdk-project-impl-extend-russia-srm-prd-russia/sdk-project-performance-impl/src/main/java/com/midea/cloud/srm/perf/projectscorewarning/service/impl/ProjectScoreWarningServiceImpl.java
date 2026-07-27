package com.midea.cloud.srm.perf.projectscorewarning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.model.perf.projectscorewarning.entity.ProjectScoreWarning;
import com.midea.cloud.srm.perf.projectscorewarning.mapper.ProjectScoreWarningMapper;
import com.midea.cloud.srm.perf.projectscorewarning.service.ProjectScoreWarningService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class ProjectScoreWarningServiceImpl extends BaseServiceImpl<ProjectScoreWarningMapper, ProjectScoreWarning> implements ProjectScoreWarningService {
    @Override
    public List<ProjectScoreWarning> listPage(ProjectScoreWarning projectScoreWarning) {
        LambdaQueryWrapper<ProjectScoreWarning> wrapper = Wrappers.lambdaQuery(ProjectScoreWarning.class);
        if (projectScoreWarning != null) {
            wrapper.like(StringUtils.isNotEmpty(projectScoreWarning.getWarningCode()), ProjectScoreWarning::getWarningCode, projectScoreWarning.getWarningCode());
            wrapper.like(StringUtils.isNotEmpty(projectScoreWarning.getPerfModelType()), ProjectScoreWarning::getPerfModelType, projectScoreWarning.getPerfModelType());
            wrapper.like(StringUtils.isNotEmpty(projectScoreWarning.getProjectName()), ProjectScoreWarning::getProjectName, projectScoreWarning.getProjectName());
            wrapper.like(StringUtils.isNotEmpty(projectScoreWarning.getCompanyName()), ProjectScoreWarning::getCompanyName, projectScoreWarning.getCompanyName());
            wrapper.eq(projectScoreWarning.getCompanyId() != null, ProjectScoreWarning::getCompanyId, projectScoreWarning.getCompanyId());
            wrapper.like(StringUtils.isNotEmpty(projectScoreWarning.getBidManagerFullPath()), ProjectScoreWarning::getBidManagerFullPath, projectScoreWarning.getBidManagerFullPath());
            wrapper.like(StringUtils.isNotEmpty(projectScoreWarning.getContractManagerFullPath()), ProjectScoreWarning::getContractManagerFullPath, projectScoreWarning.getContractManagerFullPath());
            wrapper.like(StringUtils.isNotEmpty(projectScoreWarning.getOuOrganizationName()), ProjectScoreWarning::getOuOrganizationName, projectScoreWarning.getOuOrganizationName());
            wrapper.like(StringUtils.isNotEmpty(projectScoreWarning.getBuOrganizationName()), ProjectScoreWarning::getBuOrganizationName, projectScoreWarning.getBuOrganizationName());
        }
        wrapper.orderByDesc(ProjectScoreWarning::getLastUpdateDate);
        return this.list(wrapper);
    }
}
