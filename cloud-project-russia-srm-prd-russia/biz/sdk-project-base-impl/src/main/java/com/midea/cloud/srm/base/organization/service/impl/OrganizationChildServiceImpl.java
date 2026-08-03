package com.midea.cloud.srm.base.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.common.utils.StringUtil;
import com.midea.cloud.srm.base.organization.mapper.OrganizationChildMapper;
import com.midea.cloud.srm.base.organization.service.OrganizationChildService;
import com.midea.cloud.srm.model.base.entity.Organization;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author ex_liuxy46
 */
@Slf4j
@Service
public class OrganizationChildServiceImpl extends BaseServiceImpl<OrganizationChildMapper, Organization> implements OrganizationChildService {


    @Resource
    private OrganizationChildMapper organizationChildMapper;
    /**
     * 分页
     *
     * @param organization 组织
     * @return page
     */
    @Override
    public PageInfo<Organization> listAllOrganization(Organization organization) {
        PageUtil.startPage(organization.getPageNum(), organization.getPageSize());
        if (YesOrNo.Y.name().equals(organization.getChildAll())) {
            if (StringUtils.isBlank(organization.getParentOrganizationIds())) {
                throw new BaseException("必须选定一个父组织");
            }
            List<Organization> list = organizationChildMapper.getOrganizationChildList(organization);
            return new PageInfo<>(list);
        } else {
            LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(organization.getOrganizationTypeCode())) {
                queryWrapper.eq(Organization::getOrganizationTypeCode, organization.getOrganizationTypeCode());
            }
            if (organization.getOrganizationId() != null) {
                queryWrapper.ne(Organization::getOrganizationId, organization.getOrganizationId());
            }
            if (!StringUtil.isEmpty(organization.getOrganizationName())) {
                queryWrapper.like(Organization::getOrganizationName, organization.getOrganizationName());
            }
            if (!StringUtil.isEmpty(organization.getParentOrganizationIds())) {
                queryWrapper.eq(Organization::getParentOrganizationIds, organization.getParentOrganizationIds());
            }
            if (!StringUtil.isEmpty(organization.getEnabled()) && organization.getEnabled().equals(Enable.Y.name())) {
                queryWrapper.le(Organization::getStartDate, LocalDate.now());
                queryWrapper.and(queryWrapperItem -> queryWrapperItem.gt(Organization::getEndDate, LocalDate.now()).or(true, null).isNull(Organization::getEndDate));
            } else if (!StringUtil.isEmpty(organization.getEnabled()) && organization.getEnabled().equals(Enable.N.name())) {
                queryWrapper.le(Organization::getEndDate, LocalDate.now());
            }
            if (!StringUtil.isEmpty(organization.getOrganizationRegion())) {
                queryWrapper.eq(Organization::getOrganizationRegion, organization.getOrganizationRegion());
            }
            queryWrapper.like(StringUtils.isNotEmpty(organization.getOrganizationCode()), Organization::getOrganizationCode, organization.getOrganizationCode());
            queryWrapper.orderByDesc(Organization::getLastUpdateDate);
            List<Organization> page = this.list(queryWrapper);
            for (Organization e : page) {
                if (StringUtils.isNotBlank(e.getParentOrganizationIds()) && !Objects.equals(e.getParentOrganizationIds(), "-1")) {
                    try {
                        Organization p = this.getById(e.getParentOrganizationIds());
                        if (p != null) {
                            e.setParentOrganizationNames(p.getOrganizationName());
                        }
                    } catch (Exception ignored) {
                        log.info("查找错误");
                    }
                }
            }
            return new PageInfo<>(page);
        }
    }
}
