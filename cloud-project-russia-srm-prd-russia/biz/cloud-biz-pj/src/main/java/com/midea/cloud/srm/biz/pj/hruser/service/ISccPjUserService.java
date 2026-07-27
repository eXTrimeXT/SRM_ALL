package com.midea.cloud.srm.biz.pj.hruser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.hrusertemps.entity.SccPjUserTemp;

import java.util.List;

/**
 * @author huangbf3
 */
public interface ISccPjUserService extends IService<SccPjUser> {
    /**
     * 备注
     * @param sccPjUserTempList
     * @return
     */
    public List<SccPjUser> toSccPjUser(List<SccPjUserTemp> sccPjUserTempList);

    /**
     * 备注
     * @param username
     * @return
     */
    HrUserOrgnizationDto getHrUserOrgnizationByUsername(String username);

    /**
     * 备注
     * @param sccPjUser
     * @return
     */
    PageInfo<SccPjUser> listPage(SccPjUser sccPjUser);

    /**
     * 备注
     * @param organizationCode
     * @return
     */
    Organization getBuOrganizationByOuOrgCode(String organizationCode);

    /**
     * 备注
     * @param organizationId
     * @return
     */
    List<Organization> getParentOrgs(Long organizationId);
}
