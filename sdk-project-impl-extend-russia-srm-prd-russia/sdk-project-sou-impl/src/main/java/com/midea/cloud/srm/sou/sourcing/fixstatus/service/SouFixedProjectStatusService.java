package com.midea.cloud.srm.sou.sourcing.fixstatus.service;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
/**
 * 备注
 * @author huangbf3
 */
public interface SouFixedProjectStatusService {

    /**
     * 修正单据状态
     * @param project
     * @param souType
     * @return
     */
    public ExtSouProject fixedProjectStatus(ExtSouProject project, String souType);

    /**
     * 修正所有单据状态
     * @param souType
     */
    public void fixedProjectStatusAll(String souType);
}
