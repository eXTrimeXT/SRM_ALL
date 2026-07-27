package com.midea.cloud.srm.sup.ext.pjorgcategory.service.impl;

import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.common.enums.CategoryStatus;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sup.orgcategory.entity.PjOrgCategory;
import com.midea.cloud.srm.model.sup.orgcategory.enums.OrgCategoryHeaderStatusEnum;
import com.midea.cloud.srm.sup.ext.pjorgcategory.mapper.PjOrgCategoryMapper;
import com.midea.cloud.srm.sup.ext.pjorgcategory.service.PjOrgCategoryService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author luxc18
 */
@Service
public class PjOrgCategoryServiceImpl extends BaseServiceImpl<PjOrgCategoryMapper, PjOrgCategory> implements PjOrgCategoryService {

    @Autowired
    private PjOrgCategoryMapper pjOrgCategoryMapper;

    @Autowired
    private BaseClient baseClient;

    @Override
    public List<PjOrgCategory> listPageHeader(PjOrgCategory pjOrgCategory) {
        List<PjOrgCategory> result = pjOrgCategoryMapper.listPageHeader(pjOrgCategory);
        if (CollectionUtils.isNotEmpty(result)) {
            List<PjOrgCategory> detailList = pjOrgCategoryMapper.listByHeaderKey(result,pjOrgCategory);

            Map<String, String> groupStatusMap = new HashMap<>(16);
            Map<String, List<PjOrgCategory>> groupMap = detailList.stream().collect(Collectors.groupingBy(item -> item.getCompanyId() + "-" + item.getCategoryId()));
            for (String key : groupMap.keySet()) {
                //失效：供应商是否黑名单=是，供应商是否时间受限=是，品类状态 = 失效，
                //单位状态 都是 失效 ，供应商品类状态 都是 失效
                //任何条件都是失效
                List<PjOrgCategory> tempList = groupMap.get(key);
                boolean serviceStatusFlag = tempList.stream().allMatch(p -> CategoryStatus.UNABLE.equals(p.getServiceStatus()));
                boolean pjOrgStatusFlag = tempList.stream().allMatch(p -> Enable.N.name().equals(p.getPjOrgStatus()));
                boolean pjCategoryStatusFlag = tempList.stream().allMatch(p -> Enable.N.name().equals(p.getPjCategoryStatus()));
                if (pjCategoryStatusFlag || serviceStatusFlag || pjOrgStatusFlag) {
                    groupStatusMap.put(key, OrgCategoryHeaderStatusEnum.UNABLE.name());
                    continue;
                }
                //供应商品类状态=退出/单位状态=失效
                //供应商品类状态=合格/供应商品类状态=验证中/单位状态=生效
                boolean partUnableFlag = tempList.stream().anyMatch(p -> CategoryStatus.UNABLE.equals(p.getServiceStatus())
                        || Enable.N.name().equals(p.getPjOrgStatus()));
                boolean partEnableFlag = tempList.stream().anyMatch(p -> CategoryStatus.QUALIFIED.equals(p.getServiceStatus())
                        || CategoryStatus.VERIFY.equals(p.getServiceStatus()) || Enable.Y.name().equals(p.getPjOrgStatus()));

                boolean partCategoryStatusFlagWithY = tempList.stream().anyMatch(p -> Enable.Y.name().equals(p.getPjCategoryStatus()));
                boolean partCategoryStatusFlagWithN = tempList.stream().anyMatch(p -> Enable.N.name().equals(p.getPjCategoryStatus()));

                if ((partUnableFlag && partEnableFlag) || (partCategoryStatusFlagWithY && partCategoryStatusFlagWithN)) {
                    groupStatusMap.put(key, OrgCategoryHeaderStatusEnum.PART_ENABLE.name());
                    continue;
                }
                // 生效：失效，部分生效以外的
                groupStatusMap.put(key, OrgCategoryHeaderStatusEnum.ENABLE.name());
            }

            for (PjOrgCategory orgCategory : result) {
                orgCategory.setPjOrgStatus(groupStatusMap.get(orgCategory.getCompanyId() + "-" + orgCategory.getCategoryId()));
            }
            // 补全全名称
            fillFullName(result);
        }
        return result;
    }

    @Override
    public List<PjOrgCategory> listPageDetailByHeader(PjOrgCategory pjOrgCategory) {
        List<PjOrgCategory> pjOrgCategories = pjOrgCategoryMapper.listByHeaderKey(Arrays.asList(pjOrgCategory),pjOrgCategory);
        fillFullName(pjOrgCategories);
        return pjOrgCategories;
    }


    private void fillFullName(List<PjOrgCategory> result){
        // 补全全名称
        if (CollectionUtils.isNotEmpty(result)) {
            List<Long> ids = result.stream().map(PjOrgCategory::getCategoryId).collect(Collectors.toList());
            Map<String, String> idMap = baseClient.queryCategoryFullNameByLevelIds(ids);
            for (PjOrgCategory orgCategory : result) {
                if (orgCategory.getCategoryId() != null) {
                    orgCategory.setCategoryFullName(idMap.get(orgCategory.getCategoryId().toString()));
                }
            }
        }
    }
}
