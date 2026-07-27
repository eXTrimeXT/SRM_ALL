package com.midea.cloud.srm.base.organization.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.base.entity.Organization;

/**
 * @author ex_liuxy46
 */
public interface OrganizationChildService extends IService<Organization> {

    /**
     * 分页
     * @param organization 组织
     * @return page
     */
    PageInfo<Organization>  listAllOrganization(Organization organization);
}
