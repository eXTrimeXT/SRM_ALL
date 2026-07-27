package com.midea.cloud.srm.perf.projectscoreman.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.perf.projectscoreman.dto.ProjectScoreManDTO;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface ProjectScoreManService extends BaseService<ProjectScoreMan> {
    /**
     * 备注
     * @param projectScoreMan 参数
     * @return 返回
     */
    List<ProjectScoreMan> listPage(ProjectScoreMan projectScoreMan);

    /**
     * 备注
     * @param id 参数
     * @return 返回
     */
    ProjectScoreManDTO getDetailById(Long id);

    /**
     * 备注
     * @param dto 参数
     * @return 返回
     */
    Long saveOrUpdateDetail(ProjectScoreManDTO dto);

}
