package com.midea.cloud.srm.perf.projectscorewarning.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.perf.projectscorewarning.entity.ProjectScoreWarning;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface ProjectScoreWarningService extends BaseService<ProjectScoreWarning> {
    /**
     * 备注
     * @param projectScoreWarning 参数
     * @return 返回
     */
    List<ProjectScoreWarning> listPage(ProjectScoreWarning projectScoreWarning);
}
