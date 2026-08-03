package com.midea.cloud.srm.biz.pj.hruser.service.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.hruser.mapper.SccPjUserMapper;
import com.midea.cloud.srm.biz.pj.hruser.service.ISccPjUserService;
import com.midea.cloud.srm.model.base.organization.enums.OrganizationTypeCode;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.hrusertemps.entity.SccPjUserTemp;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class ISccPjUserServiceImpl extends ServiceImpl<SccPjUserMapper, SccPjUser> implements ISccPjUserService {
    @Autowired
    private QlOpenClient qlOpenClient;

    private static final int LENGTH= 6;

    private static final String DEP = "DEP";

    @Override
    public List<SccPjUser> toSccPjUser(List<SccPjUserTemp> sccPjUserTempList) {

        if(CollectionUtils.isEmpty(sccPjUserTempList)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SccPjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SccPjUser::getId, sccPjUserTempList.stream().map(SccPjUserTemp::getId).distinct().collect(Collectors.toList()));

        List<SccPjUser> sccPjUserList = this.list(queryWrapper);

        Map<Long, SccPjUser> sccPjUserMap = sccPjUserList.stream().collect(Collectors.toMap(k->k.getId(), Function.identity(), (k1, k2)->k2));

        List<SccPjUser> saveList = new ArrayList<>();

        List<SccPjUser> finalSaveList = saveList;
        sccPjUserTempList.stream().forEach(sccPjUserTemp -> {
            SccPjUser sccPjUser = new SccPjUser();
            BeanCopyUtil.copyProperties(sccPjUser, sccPjUserTemp);
            if(sccPjUserMap.containsKey(sccPjUser.getId())) {
                sccPjUser.setRowId(sccPjUserMap.get(sccPjUser.getId()).getRowId());
            } else {
                sccPjUser.setRowId(IdGenrator.generate());
            }
            finalSaveList.add(sccPjUser);
        });

        saveList = saveList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SccPjUser::getId))), ArrayList::new));

        this.saveOrUpdateBatch(saveList);

        return saveList;
    }

    @Override
    public HrUserOrgnizationDto getHrUserOrgnizationByUsername(String username) {
        Assert.notBlank(username,"账号不能为空");
        HrUserOrgnizationDto hrUserOrgnizationDto = new HrUserOrgnizationDto();

        SccPjUser sccPjUser =  this.getOne(Wrappers.lambdaQuery(SccPjUser.class).eq(SccPjUser::getPersonnelNo,username));
        if(sccPjUser!=null){
            QlOpenQueryWrapper wrapper = QlOpenWrappers.query("Organization");
            wrapper.contains("organizationCode",sccPjUser.getGroupId().toString());
            List<Organization> organizations = qlOpenClient.query(ContextPath.BASE, wrapper, Organization.class);

            if(organizations.size()>0){
                if (StringUtils.equals(organizations.get(0).getOrganizationTypeCode(), DEP)) {
                    com.midea.cloud.srm.model.base.organization.entity.Organization departmentOrganization = new com.midea.cloud.srm.model.base.organization.entity.Organization();
                    BeanUtils.copyProperties(organizations.get(0),departmentOrganization);
                    hrUserOrgnizationDto.setDepartmentOrganization(departmentOrganization);
                }

                int num = 0;
                while (organizations!=null&&organizations.size()>0&&num++<LENGTH){
                    if (StringUtils.equals(organizations.get(0).getOrganizationTypeCode(), "OU")){
                        com.midea.cloud.srm.model.base.organization.entity.Organization ouOrganization = new com.midea.cloud.srm.model.base.organization.entity.Organization();
                        BeanUtils.copyProperties(organizations.get(0),ouOrganization);
                        hrUserOrgnizationDto.setOuOrganization(ouOrganization);
                        break;
                    }
                    organizations = getParentOrgs(organizations.get(0).getOrganizationId());
                    if(organizations!=null&&organizations.size()>0&& StringUtils.equals(organizations.get(0).getOrganizationTypeCode(), OrganizationTypeCode.OU.name())){
                        com.midea.cloud.srm.model.base.organization.entity.Organization ouOrganization = new com.midea.cloud.srm.model.base.organization.entity.Organization();
                        BeanUtils.copyProperties(organizations.get(0),ouOrganization);
                        hrUserOrgnizationDto.setOuOrganization(ouOrganization);
                        break;
                    }

                }
                if(hrUserOrgnizationDto.getOuOrganization()!=null){
                    num = 0;
                    while (organizations!=null&&organizations.size()>0&&num++<LENGTH){
                        organizations = getParentOrgs(organizations.get(0).getOrganizationId());
                        if(organizations!=null&&organizations.size()>0&& StringUtils.equals(organizations.get(0).getOrganizationTypeCode(), OrganizationTypeCode.BU.name())){
                            com.midea.cloud.srm.model.base.organization.entity.Organization buOrganization = new com.midea.cloud.srm.model.base.organization.entity.Organization();
                            BeanUtils.copyProperties(organizations.get(0),buOrganization);
                            hrUserOrgnizationDto.setBuOrganization(buOrganization);
                            break;
                        }

                    }
                }


            }
        }

        return hrUserOrgnizationDto;
    }

    @Override
    public PageInfo<SccPjUser> listPage(SccPjUser sccPjUser) {
        PageUtil.startPage(sccPjUser.getPageNum(),sccPjUser.getPageSize());
        LambdaQueryWrapper<SccPjUser> queryWrapper = new LambdaQueryWrapper<>(sccPjUser);
        List<SccPjUser> list = this.list(queryWrapper);
        return new PageInfo<>(list);
    }

    @Override
    public Organization getBuOrganizationByOuOrgCode(String organizationCode) {
        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("Organization");
        wrapper.eq("organizationCode",organizationCode);
        List<Organization> organizations = qlOpenClient.query(ContextPath.BASE, wrapper, Organization.class);
        int num = 0;
        while (organizations!=null&&organizations.size()>0&&num++<LENGTH){
            organizations = getParentOrgs(organizations.get(0).getOrganizationId());
            if(organizations!=null&&organizations.size()>0&& StringUtils.equals(organizations.get(0).getOrganizationTypeCode(), OrganizationTypeCode.BU.name())){
                return organizations.get(0);
            }
        }
        return new Organization();
    }


    @Override
    public List<Organization> getParentOrgs(Long organizationId){

        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("OrganizationRelation");
        wrapper.eq("organizationId",organizationId);
        List<OrganizationRelation> organizationRelations = qlOpenClient.query(ContextPath.BASE, wrapper, OrganizationRelation.class);
        Assert.isTrue(organizationRelations!=null&&organizationRelations.size()>0,"找不到父组织");

        if (organizationRelations.get(0).getParentOrganizationId() == -1) {
            // 说明已经到根节点了
            return Collections.emptyList();
        }

        QlOpenQueryWrapper wrapper2 = QlOpenWrappers.query("Organization");
        wrapper2.eq("organizationId",organizationRelations.get(0).getParentOrganizationId());
        List<Organization> organizations = qlOpenClient.query(ContextPath.BASE, wrapper2, Organization.class);
        Assert.isTrue(organizations!=null&&organizations.size()>0,"找不到父组织");
        return organizations;
    }
}
