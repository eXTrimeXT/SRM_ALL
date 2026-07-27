package com.midea.cloud.srm.perf.projectscoreman.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.perf.projectscoreman.dto.ProjectScoreManDTO;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManDetail;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManRejectInfo;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateIndsLine;
import com.midea.cloud.srm.perf.projectscoreman.mapper.ProjectScoreManMapper;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManDetailService;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManRejectInfoService;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManService;
import com.midea.cloud.srm.perf.template.service.IPerfTemplateIndsLineService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class ProjectScoreManServiceImpl extends BaseServiceImpl<ProjectScoreManMapper, ProjectScoreMan> implements ProjectScoreManService {

    @Autowired
    private ProjectScoreManDetailService projectScoreManDetailService;

    @Autowired
    private ProjectScoreManRejectInfoService projectScoreManRejectInfoService;

    @Autowired
    private IPerfTemplateIndsLineService perfTemplateIndsLineService;

    @Override
    public List<ProjectScoreMan> listPage(ProjectScoreMan projectScoreMan) {
        LambdaQueryWrapper<ProjectScoreMan> wrapper = Wrappers.lambdaQuery(ProjectScoreMan.class);
        if (projectScoreMan != null) {
            wrapper.like(StringUtils.isNotEmpty(projectScoreMan.getProjectName()), ProjectScoreMan::getProjectName, projectScoreMan.getProjectName());
            wrapper.like(StringUtils.isNotEmpty(projectScoreMan.getContractName()), ProjectScoreMan::getContractName, projectScoreMan.getContractName());
            wrapper.eq(StringUtils.isNotEmpty(projectScoreMan.getCompanyName()), ProjectScoreMan::getCompanyName, projectScoreMan.getCompanyName());
            wrapper.eq(projectScoreMan.getCompanyId() != null, ProjectScoreMan::getCompanyId, projectScoreMan.getCompanyId());
            wrapper.eq(StringUtils.isNotEmpty(projectScoreMan.getPerformanceCode()), ProjectScoreMan::getPerformanceCode, projectScoreMan.getPerformanceCode());
            wrapper.eq(StringUtils.isNotEmpty(projectScoreMan.getApproveStatus()), ProjectScoreMan::getApproveStatus, projectScoreMan.getApproveStatus());
            wrapper.eq(projectScoreMan.getProjectScoreItemsId() != null, ProjectScoreMan::getProjectScoreItemsId, projectScoreMan.getProjectScoreItemsId());
            wrapper.eq(projectScoreMan.getCategoryId() != null, ProjectScoreMan::getCategoryId, projectScoreMan.getCategoryId());
            wrapper.eq(projectScoreMan.getScoreManAccount() != null, ProjectScoreMan::getScoreManAccount, projectScoreMan.getScoreManAccount());
        }
        wrapper.orderByDesc(ProjectScoreMan::getLastUpdateDate);
        return this.list(wrapper);
    }

    @Override
    public ProjectScoreManDTO getDetailById(Long id) {
        ProjectScoreManDTO result = new ProjectScoreManDTO();
        ProjectScoreMan projectScoreMan = this.getById(id);
        BeanUtils.copyProperties(projectScoreMan,result);
        List<ProjectScoreManDetail> detailList = projectScoreManDetailService.list(ProjectScoreManDetail::getProjectScoreManId, id);
        if(CollectionUtils.isNotEmpty(detailList)){
            List<Long> templateLineIdList = detailList.stream().map(ProjectScoreManDetail::getTemplateLineId).collect(Collectors.toList());
            List<PerfTemplateIndsLine> perfTemplateIndsLines = perfTemplateIndsLineService.listIn(PerfTemplateIndsLine::getTemplateLineId, templateLineIdList);
            Map<Long, List<PerfTemplateIndsLine>> groupMap = perfTemplateIndsLines.stream().collect(Collectors.groupingBy(PerfTemplateIndsLine::getTemplateLineId));
            detailList.stream().forEach(item -> item.setIndicatorsLines(groupMap.get(item.getTemplateLineId())));
        }
        List<ProjectScoreManRejectInfo> rejectInfoList = projectScoreManRejectInfoService.list(ProjectScoreManRejectInfo::getProjectScoreManId, id);
        return result.setDetailList(detailList).setRejectInfoList(rejectInfoList);
    }

    @Override
    public Long saveOrUpdateDetail(ProjectScoreManDTO dto) {
        Long projectScoreManId = dto.getProjectScoreManId();
        List<ProjectScoreManDetail> detailList = dto.getDetailList();
        Date date = new Date();
        detailList.stream().forEach(item -> {
            if (item.getScore() != null) {
                item.setScoreDate(date);
            }
        });
        this.update(Wrappers.lambdaUpdate(ProjectScoreMan.class)
                .eq(ProjectScoreMan::getProjectScoreManId, projectScoreManId)
                .set(ProjectScoreMan::getScoreDate, LocalDate.now()));
        projectScoreManDetailService.saveOrUpdate(projectScoreManId, detailList, ProjectScoreManDetail::getProjectScoreManId);
        return projectScoreManId;
    }

}
