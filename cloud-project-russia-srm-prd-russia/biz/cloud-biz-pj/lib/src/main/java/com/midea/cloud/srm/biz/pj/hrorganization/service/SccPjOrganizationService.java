package com.midea.cloud.srm.biz.pj.hrorganization.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hrorganizationtemp.SccPjOrganizationTemp;

import java.util.List;

/**
 * @author huangbf3
 */
public interface SccPjOrganizationService extends IService<SccPjOrganization> {
    /**
     * 备注
     * @param sccPjOrganizationTempList
     * @return
     */
    public List<SccPjOrganization> toSccPjOrganization(List<SccPjOrganizationTemp> sccPjOrganizationTempList);
}
