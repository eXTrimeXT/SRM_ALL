package com.midea.cloud.srm.base.extusers.service.impl;

import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.base.extusers.service.ExtUserService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
@Api("用户接口实现类-二开")
public class ExtUserServiceImpl implements ExtUserService {

    @Autowired
    private BaseClient baseClient;

    @Override
    public List<OrganizationUser> initOrgnizationUser(List<OrganizationUser> organizationUserList) {
        List<OrganizationRelation> organizationRelationList = baseClient.allTree();
        Map<Long, OrganizationRelation> fullPathIdMap = new HashMap<>(50);
        getFullPathId(organizationRelationList, fullPathIdMap);

        organizationUserList.stream().forEach(o -> {
            o.setOrganizationUserRelId(IdGenrator.generate());
            OrganizationRelation relation = fullPathIdMap.getOrDefault(o.getOrganizationId(), new OrganizationRelation());
            o.setFullPathId(relation.getFullPathId());
            o.setParentOrganizationId(relation.getParentOrganizationId());
            o.setStartDate(LocalDate.now());

            o.setCreatedBy("1");
            o.setCreatedByIp("127.0.0.1");
            o.setCreatedFullName("系统自动生成");
            o.setCreatedId(-1L);
            o.setCreationDate(new Date());
            o.setCreatedUserName("-1");

            o.setLastUpdatedBy("1");
            o.setLastUpdatedByIp("127.0.0.1");
            o.setLastUpdatedFullName("系统自动生成");
            o.setLastUpdatedId(-1L);
            o.setLastUpdateDate(new Date());
            o.setLastUpdatedUserName("-1");

            o.setVersion(0L);
            o.setTenantId("-1");
        });

        return organizationUserList;
    }

    protected void getFullPathId(List<OrganizationRelation> organizationRelationList, Map<Long, OrganizationRelation> fullPathIdMap) {
        organizationRelationList.stream().forEach(organizationRelation -> {
            fullPathIdMap.put(organizationRelation.getOrganizationId(), organizationRelation);
            if(CollectionUtils.isNotEmpty(organizationRelation.getChildOrganRelation())) {
                getFullPathId(organizationRelation.getChildOrganRelation(), fullPathIdMap);
            }
        });
    }
}
