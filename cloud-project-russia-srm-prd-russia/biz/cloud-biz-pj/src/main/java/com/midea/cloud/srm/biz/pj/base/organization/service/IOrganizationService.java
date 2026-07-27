package com.midea.cloud.srm.biz.pj.base.organization.service;


import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.changchengapi.dto.OrganizationApiDTO;

import java.util.List;

/**
 * @author huangbf3
 */
public interface IOrganizationService {

    /**
     * 分页查询全部组织信息
     * @param organization
     * @return Organization
     */
    PageInfo<Organization> listAllOrganization(Organization organization);

    /**
     * 获取组织详情页
     * @param id 组织ID
     * @return 组织信息
     */
    OrganizationEditDto getOrganization(Long id);

    /**
     * 获取有开票信息的组织
     * @return 组织信息
     */
    List<Organization> findListFilterInvoiceInfo();

    /**
     * 新增或编辑组织
     * @param organizationEditDto 组织
     */
    void saveOrUpdate(OrganizationEditDto organizationEditDto);

    /**
     * 备注
     * @param organizationApiDtos
     */
    void saveOrganizationBatch(List<OrganizationApiDTO> organizationApiDtos);
}
