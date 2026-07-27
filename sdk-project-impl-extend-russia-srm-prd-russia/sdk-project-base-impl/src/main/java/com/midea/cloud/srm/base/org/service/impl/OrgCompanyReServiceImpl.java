package com.midea.cloud.srm.base.org.service.impl;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.base.iam.IamOrganizationService;
import com.midea.cloud.srm.base.org.service.OrgCompanyReService;
import com.midea.cloud.srm.base.organization.service.IOrganizationService;
import com.midea.cloud.srm.model.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.base.organization.dto.OrganizationExtDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.enums.OrganizationTypeCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 100014336
 * 重写company 相关接口
 */
@Service
public class OrgCompanyReServiceImpl implements OrgCompanyReService {

    @Autowired
    private IOrganizationService iOrganizationService;
    @Autowired
    private IamOrganizationService iamOrganizationService;

    @Override
    public OrganizationEditDto getByOuId(Long organizationId) {
        Assert.notNull(organizationId, "缺少参数: organizationId");
        Organization organization = (Organization)this.iOrganizationService.selectOne(Organization::getOrganizationId, organizationId);
        if (organization == null) {
            throw new BaseException("业务实体不存在：" + organizationId);
        } else {
            List<Organization> companyOrganizationList = new ArrayList();
            if (StringUtils.isNotBlank(organization.getParentOrganizationIds())) {
                List<Long> parentOrgIds = new ArrayList();
                List<String> parentOrgIdStrList = Arrays.asList(organization.getParentOrganizationIds().split(","));
                for (String parentId:parentOrgIdStrList){
                    if (StringUtils.isNotBlank(parentId)) {
                        parentOrgIds.add(Long.parseLong(parentId));
                    }
                }
                if (CollectionUtils.isNotEmpty(parentOrgIds)) {
                    companyOrganizationList = this.iOrganizationService.listOrganization(parentOrgIds, OrganizationTypeCode.COMPANY.toString());
                    if (((List)companyOrganizationList).size() > 1) {
                        throw new BaseException("业务实体" + organization.getOrganizationName() + "只能有一个公司上级");
                    }
                }
            }
            OrganizationEditDto organizationEditDto = getCompanyByOuId(organizationId);
            organizationEditDto.setOuInfo(organization);
            if(CollectionUtils.isNotEmpty(companyOrganizationList)){
                organizationEditDto.setOrganization(companyOrganizationList.get(0));
            }
            return organizationEditDto;
        }
    }

    private OrganizationEditDto getCompanyByOuId(Long organizationId) {
        Assert.notNull(organizationId, "缺少参数: organizationId");
        Organization organization = (Organization)this.iOrganizationService.getById(organizationId);
        OrganizationEditDto result = new OrganizationEditDto();

        return result;
    }


}
