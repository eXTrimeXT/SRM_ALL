package com.midea.cloud.srm.perf.projectscore.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.perf.projectscore.dto.ProjectScoreHeaderDTO;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface ProjectScoreHeaderService extends BaseService<ProjectScoreHeader> {
    /**
     * 备注
     * @param queryDTO 参数
     * @return 返回
     */
    List<ProjectScoreHeader> listPage(ProjectScoreHeader queryDTO);

    /**
     * 备注
     * @param scoreHeaderId 参数
     * @return 返回
     */
    ProjectScoreHeaderDTO getDetailById(Long scoreHeaderId);
}
