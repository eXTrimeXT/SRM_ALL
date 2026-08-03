package com.midea.cloud.srm.base.org.service;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.base.dto.OrgQueryDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;

import java.util.List;
import java.util.Map;

/**
 * @author zenghx2
 */
public interface OrgQueryService {
    /**
     * 备注
     * @param orgQueryDTO 参数
     * @return 返回
     */
    List<Organization> getSubOrgs(OrgQueryDTO orgQueryDTO);

    /**
     * 备注
     * @param orgId 参数
     * @return 返回
     */
    Organization getBuOrg(Long orgId);

    /**
     * 备注
     * @param companyId 参数
     * @return 返回
     */
    List<Organization> listAllForReviewForm(Long companyId);

    /**
     * 备注
     * @param organizationId 参数
     * @return 返回
     */
    List<OrganizationRelation> listChildrenOrganization(Long organizationId);

    /**
     * getOrgAddress
     * @param orgIds
     * @return
     */
    List<Record> getOrgAddress(List<Long> orgIds);

    /**
     * getOrgAddressBatch
     * @param orgIds
     * @param result
     * @param parentMap
     */
    void getOrgAddressBatch(List<Long> orgIds, Map<Long, List<Record>> result, Map<Long, Long> parentMap);
}
