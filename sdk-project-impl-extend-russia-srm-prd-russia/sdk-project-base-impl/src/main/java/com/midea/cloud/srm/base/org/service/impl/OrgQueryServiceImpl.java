package com.midea.cloud.srm.base.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.base.org.service.OrgQueryService;
import com.midea.cloud.srm.base.organization.service.IOrganizationRelationService;
import com.midea.cloud.srm.base.organization.service.IOrganizationService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dto.OrgQueryDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.base.organization.entity.Site;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Slf4j
@Component
public class OrgQueryServiceImpl implements OrgQueryService {

    @Autowired
    private IOrganizationService organizationService;

    @Autowired
    private IOrganizationRelationService organizationRelationService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private QlService qlService;

    @Value("${base.orgSearch.max:1000}")
    private Integer max;

    /**
     * 查询所有下级组织列表
     */
    @Override
    public List<Organization> getSubOrgs(OrgQueryDTO orgQueryDTO) {
        List<Organization> orgs = new ArrayList<>();
        fetchByParent(Arrays.asList(orgQueryDTO.getParentId()), orgs, orgQueryDTO.getType());


        if(BooleanUtils.isTrue(orgQueryDTO.getExistsOwner())){
            Organization organization = organizationService.get(orgQueryDTO.getParentId());
            orgs.add(0, organization);
        }
        if (StringUtils.isNotBlank(orgQueryDTO.getOrganizationCode())) {
            orgs = orgs.stream().filter(e -> e.getOrganizationCode().contains(orgQueryDTO.getOrganizationCode())).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(orgQueryDTO.getOrganizationName())) {
            orgs =  orgs.stream().filter(e -> e.getOrganizationName().contains(orgQueryDTO.getOrganizationName())).collect(Collectors.toList());
        }

        return orgs;
    }

    /**
     * 查询父级板块
     */
    @Override
    public Organization getBuOrg(Long orgId) {
        Organization organization = organizationService.get(orgId);
        if (organization == null) {
            return null;
        }
        String bu = "BU";
        if (bu.equals(organization.getOrganizationTypeCode())) {
            return organization;
        }
        if (StringUtils.isNotBlank(organization.getParentOrganizationIds())) {
            String[] arr = organization.getParentOrganizationIds().split(",");
            Long parentOrgId = Long.valueOf(arr[arr.length - 1]);
            return getBuOrg(parentOrgId);
        }
        return null;
    }

    @Override
    public List<Organization> listAllForReviewForm(Long companyId) {
        Organization organization = new Organization();
        organization.setOrganizationTypeCode("OU");
        List<Organization> organizations = baseClient.listOrganizationByParam(organization);
        // 根据供应商id,获取组织状态为失效的品类关系,过滤掉这些组织
        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("OrgCategory")
                .eq("companyId",companyId)
                .eq("pjOrgStatus",Enable.N.name());

        List<Record> orgCategories = qlOpenClient.query(ContextPath.SUP, wrapper, Record.class);
        if(CollectionUtils.isNotEmpty(orgCategories)){
            Map<Long, Record> orgIdMap = orgCategories.stream().collect(Collectors.toMap(item -> item.getLong("orgId"), Function.identity(), (k1, k2) -> k2));
            List<Organization> collect = organizations.stream().filter(item -> !orgIdMap.containsKey(item.getOrganizationId())).collect(Collectors.toList());
            return collect;
        }else{
            return organizations;
        }
    }

    @Override
    public List<OrganizationRelation> listChildrenOrganization(Long organizationId) {
        QueryWrapper<OrganizationRelation> queryWrapper = new QueryWrapper<>(new OrganizationRelation()
                .setParentOrganizationId(organizationId));
        List<OrganizationRelation> childernOrganizationRelations = organizationRelationService.getBaseMapper().selectList(queryWrapper);
        Organization parentOrganization = organizationService.getBaseMapper().selectById(organizationId);
        Map<Long,Organization> childernOrganizationMap = new HashMap<>(50);
        if(childernOrganizationRelations.size()>0){
            List<Organization> childernOrganizations = organizationService.lambdaQuery()
                    .in(Organization::getOrganizationId,childernOrganizationRelations.stream().map(OrganizationRelation::getOrganizationId).collect(Collectors.toSet())).list();
            childernOrganizationMap = childernOrganizations.stream().collect(Collectors.toMap(Organization::getOrganizationId,t->t));
        }

        for (OrganizationRelation childrenOrganizationRelation : childernOrganizationRelations) {
            if (childrenOrganizationRelation == null) {
                continue;
            }
            Organization childrenOrganization = childernOrganizationMap.get(childrenOrganizationRelation.getOrganizationId());
            if (parentOrganization != null) {
                childrenOrganizationRelation.setParentOrganizationName(parentOrganization.getOrganizationName())
                        .setParentOrganizationCode(parentOrganization.getOrganizationCode())
                        .setOrganizationName(childrenOrganization.getOrganizationName())
                        .setOrganizationCode(childrenOrganization.getOrganizationCode());
            }else{
                childrenOrganizationRelation.setOrganizationName(childrenOrganization.getOrganizationName())
                        .setOrganizationCode(childrenOrganization.getOrganizationCode());
            }
        }
        return childernOrganizationRelations;
    }

    @Override
    public List<Record> getOrgAddress(List<Long> orgIds) {
        List<Record> addrList = qlService.queryByWrapper(QlWrappers.query("Site")
                .in(Site::getOrganizationId, orgIds), Record.class);
        if (CollectionUtils.isNotEmpty(addrList)) {
            return addrList;
        }

        List<Long> parentIds = organizationRelationService.list(new LambdaQueryWrapper<OrganizationRelation>()
                        .in(OrganizationRelation::getOrganizationId, orgIds))
                .stream().map(e -> e.getParentOrganizationId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(parentIds)) {
            return new ArrayList<>();
        }

        return getOrgAddress(parentIds);
    }

    @Override
    public void getOrgAddressBatch(List<Long> orgIds, Map<Long, List<Record>> result, Map<Long, Long> parentMap) {
        //查询组织地点表
        List<Record> addrList = qlService.queryByWrapper(QlWrappers.query("Site")
                .in(Site::getOrganizationId, orgIds), Record.class);
        //第一次查询时，，初始化组织ID
        if(MapUtils.isEmpty(parentMap)) {
            orgIds.stream().forEach(id -> parentMap.put(id, id));
        }
        if (CollectionUtils.isNotEmpty(addrList)) {
            Map<Long, List<Record>> addrGroup = addrList.stream().collect(Collectors.groupingBy(r -> r.get(Site::getOrganizationId)));
            //父子关系反转
            Map<Long, Set<Long>> mappingMap = new HashMap<>(15);
            //将组织ID和父类ID映射关系反转
            parentMap.keySet().forEach(k -> {
                if(!mappingMap.containsKey(parentMap.get(k))) {
                    mappingMap.put(parentMap.get(k), new HashSet<>(15));
                }
                mappingMap.get(parentMap.get(k)).add(k);
            });
            //查询到地址时，移除组织ID，防止无限递归
            addrGroup.keySet().forEach(id -> {
                if(mappingMap.containsKey(id)) {
                    mappingMap.get(id).stream().forEach(orgId -> {
                        result.put(orgId,  addrGroup.get(id));
                        parentMap.remove(orgId);
                    });
                }
            });
        }

        //全部都查询到地址时，终止程序
        if(MapUtils.isEmpty(parentMap)) {
            return;
        }

        //查询父类
        Map<Long, Long> nextParentIdMap = organizationRelationService.list(new LambdaQueryWrapper<OrganizationRelation>()
                .in(OrganizationRelation::getOrganizationId, parentMap.values().stream().distinct().collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(k -> k.getOrganizationId(), v -> v.getParentOrganizationId(), (k1, k2) -> k2));

        //没有父类时终止程序
        if(MapUtils.isEmpty(nextParentIdMap)) {
            return;
        }

        //替换下一个父类ID
        for(Long orgId : parentMap.keySet()) {
            Long parentId = parentMap.get(orgId);
            if(nextParentIdMap.containsKey(parentId)) {
                parentMap.put(orgId, nextParentIdMap.get(parentId));
            } else {
                parentMap.remove(orgId);
            }
        }

        //递归调用
        getOrgAddressBatch(parentMap.values().stream().distinct().collect(Collectors.toList()), result, parentMap);

    }

    private void fetchByParent(List<Long> parentIds, List<Organization> list, String type) {
        if (list.size() > max) {
            log.info("查询组织数据量太大");
            return;
        }

        List<OrganizationRelation> organizationRelations = organizationRelationService.list(new LambdaQueryWrapper<OrganizationRelation>()
                .in(OrganizationRelation::getParentOrganizationId, parentIds));
        if (CollectionUtils.isNotEmpty(organizationRelations)) {
            List<Long> orgIds = organizationRelations.stream().map(e -> e.getOrganizationId()).collect(Collectors.toList());
            List<Organization> orgs = organizationService.listByIds(orgIds);

            // 过滤类型
//            if (StringUtils.isNotBlank(type)) {
//                orgs = orgs.stream().filter(e -> type.equals(e.getOrganizationTypeCode())).collect(Collectors.toList());
//                orgIds = orgs.stream().map(e -> e.getOrganizationId()).collect(Collectors.toList());
//            }

            if (CollectionUtils.isNotEmpty(orgs)) {
                list.addAll(orgs);
                fetchByParent(orgIds, list, type);
            }
        }
    }
}
