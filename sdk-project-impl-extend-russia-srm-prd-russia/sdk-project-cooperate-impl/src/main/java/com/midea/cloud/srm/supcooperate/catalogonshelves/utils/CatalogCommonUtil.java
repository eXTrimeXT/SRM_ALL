package com.midea.cloud.srm.supcooperate.catalogonshelves.utils;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import io.swagger.annotations.ApiModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 *  内部商城公共方法
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/18 12:37
 *  修改内容:
 * </pre>
 */
@ApiModel(description = "内部商城公共方法")
@Component
public class CatalogCommonUtil {
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private BaseExtClient baseExtClient;

    private static final int NUM2 = 2;

    /**
     * 获取二级品类
     *
     * @param record
     * @return
     */
    public PurchaseCategory getSecondCategory(Record record) {
        try {
            List<PurchaseCategory> purchaseCategoryList = baseClient.listCategoryByIds(new ArrayList<>(Collections.singletonList(record.get(CatalogOnShelves::getCategoryId))));
            Assert.isTrue(ObjectUtil.isNotEmpty(purchaseCategoryList), "操作失败：请维护物料与品类关系");
            int i = 0;
            while (purchaseCategoryList.get(0).getLevel() != NUM2) {
                purchaseCategoryList = baseClient.listCategoryByIds(new ArrayList<>(Collections.singletonList(purchaseCategoryList.get(0).getParentId())));
                i++;
                Assert.isTrue(i <= 5, "操作失败：获取品类主数据异常");
            }
            return purchaseCategoryList.get(0);
        } catch (Exception e) {
            throw new RuntimeException("操作失败：获取品类主数据异常");
        }
    }

    /**
     * 获取默认收获地址
     *
     * @param loginAppUser
     * @return
     */
    public Record getAddr(LoginAppUser loginAppUser) {
        HrUserOrgnizationDto hrUserOrgnizationByUsername;
        //根据部门id找到业务实体id
        try {
            hrUserOrgnizationByUsername = pjProjectExtClient.getHrUserOrgnizationByUsername(loginAppUser.getUsername());
            Assert.isTrue(ObjectUtil.isNotEmpty(hrUserOrgnizationByUsername) && ObjectUtil.isNotEmpty(hrUserOrgnizationByUsername.getOuOrganization()), "获取用户所在业务实体数据为空");
        } catch (Exception e) {
            throw new BaseException("调用根据用户获取组织信息接口失败,请重试");
        }
        List<Record> addrs = baseExtClient.getOrgAddress(hrUserOrgnizationByUsername.getDepartmentOrganization().getOrganizationId());
        Assert.isTrue(ObjectUtil.isNotEmpty(addrs), "获取默认地址数据为空");
        List<Record> defaults = addrs.stream().filter(e1 -> com.midea.cloud.common.enums.YesOrNo.YES.getValue().equals(e1.get("isDefault"))).collect(Collectors.toList());
        Record result = CollectionUtils.isNotEmpty(defaults) ? defaults.get(0) : addrs.get(0);
        //组织
        result.put("organizationId", hrUserOrgnizationByUsername.getOuOrganization().getOrganizationId());
        result.put("organizationCode", hrUserOrgnizationByUsername.getOuOrganization().getOrganizationCode());
        result.put("organizationName", hrUserOrgnizationByUsername.getOuOrganization().getOrganizationName());
        //当前人部门
        result.put("departmentOrganizationId", hrUserOrgnizationByUsername.getDepartmentOrganization().getOrganizationId());
        result.put("departmentOrganizationCode", hrUserOrgnizationByUsername.getDepartmentOrganization().getOrganizationCode());
        result.put("departmentOrganizationName", hrUserOrgnizationByUsername.getDepartmentOrganization().getOrganizationName());
        return result;
    }
}
