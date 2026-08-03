package com.midea.cloud.srm.base.org.service;

import com.midea.cloud.srm.model.base.organization.dto.OrganizationEditDto;

/**
 * @author 100014336
 *
 */
public interface OrgCompanyReService {

    /**
     * 重写获取公司信息 通过 ouid
     * @param organizationId
     * @return
     */
    OrganizationEditDto getByOuId(Long organizationId);

}
