package com.midea.cloud.srm.sup.ext.pjorgcateform.controller;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.emun.PjSupplierControlType;
import com.midea.cloud.srm.model.common.enums.CategoryStatus;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.supplierauth.orgcategory.dto.OrgCatFormDTO;
import com.midea.cloud.srm.model.supplierauth.orgcategory.entity.OrgCatForm;
import com.midea.cloud.srm.model.supplierauth.orgcategory.entity.OrgCatFormCategory;
import com.midea.cloud.srm.model.sup.orgcategory.entity.PjOrgCategory;
import com.midea.cloud.srm.sup.info.service.IOrgCategoryService;
import com.midea.cloud.srm.utils.MqlType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合作终止二开
 * @author 100014323
 */
@Api(value = "PjOrgCatFormController", tags = {"合作终止二开"})
@RestController
@RequestMapping("/pj/orgCatForm")
public class PjOrgCatFormController {

    @Autowired
    private QlService qlService;

    @Autowired
    private IOrgCategoryService orgCategoryService;

    /**
     * 获取组织受限/品类受限的品类关系数据
     *
     * @return
     */
    @PostMapping("/listOrgCategoryInfoByVendorId")
    public OrgCatFormDTO listOrgCategoryInfoByVendorId(@RequestBody OrgCatForm orgCatForm) {
        OrgCatFormDTO result = new OrgCatFormDTO();

        List<Record> pjOrgStatusList = this.qlService.queryByWrapper(QlWrappers.query(MqlType.ORGCATEGORY)
                .eq(PjOrgCategory::getCompanyId, orgCatForm.getVendorId())
                .eq(PjOrgCategory::getPjOrgStatus, Enable.N.name()), Record.class);

        List<Record> pjCategoryStatusList = this.qlService.queryByWrapper(QlWrappers.query(MqlType.ORGCATEGORY)
                .eq(PjOrgCategory::getCompanyId, orgCatForm.getVendorId())
                .eq(PjOrgCategory::getPjCategoryStatus, Enable.N.name()), Record.class);

        List orgRangeList = new ArrayList<>();
        List categoryRangeList = new ArrayList<>();
        // 控制范围-组织
        if (CollectionUtils.isNotEmpty(pjOrgStatusList)) {
            Map<Long, Record> orgMap = pjOrgStatusList.stream().collect(Collectors.toMap(item -> item.getLong("orgId"), item -> item, (k1, k2) -> k2));
            for (Long key : orgMap.keySet()) {
                OrgCatFormCategory orgRange = new OrgCatFormCategory();
                orgRange.setOrgId(key);
                orgRange.setOrgCode(orgMap.get(key).getString("orgCode"));
                orgRange.setOrgName(orgMap.get(key).getString("orgName"));
                orgRangeList.add(orgRange);
            }
        }
        // 控制范围-品类
        if (CollectionUtils.isNotEmpty(pjCategoryStatusList)) {
            Map<Long, Record> categoryMap = pjCategoryStatusList.stream().collect(Collectors.toMap(item -> item.getLong("categoryId"), item -> item, (k1, k2) -> k2));
            for (Long key : categoryMap.keySet()) {
                OrgCatFormCategory categoryRange = new OrgCatFormCategory();
                categoryRange.setCategoryId(key);
                categoryRange.setCategoryCode(categoryMap.get(key).getString("categoryCode"));
                categoryRange.setCategoryName(categoryMap.get(key).getString("categoryName"));
                categoryRangeList.add(categoryRange);
            }
        }
        if(PjSupplierControlType.CATEGORY_LIMIT_FLAG_REMOVE.name().equals(orgCatForm.getSupplierControlType())){
            List<Record> orgCategoryRecordList = qlService.queryByWrapper(QlWrappers.query(MqlType.ORGCATEGORY)
                    .eq(PjOrgCategory::getCompanyId, orgCatForm.getVendorId())
                    .in(PjOrgCategory::getServiceStatus, Arrays.asList(CategoryStatus.QUALIFIED,CategoryStatus.VERIFY)),Record.class);
            List<PjOrgCategory> orgCategoryList = MeiQl.toListValue(orgCategoryRecordList, PjOrgCategory.class);
            //控制明细
            List<OrgCatFormCategory> detailList = new ArrayList<>();
            for (PjOrgCategory orgCategory : orgCategoryList) {
                OrgCatFormCategory category = new OrgCatFormCategory();
                category.setOrgId(orgCategory.getOrgId());
                category.setOrgCode(orgCategory.getOrgCode());
                category.setOrgName(orgCategory.getOrgName());
                category.setCategoryId(orgCategory.getCategoryId());
                category.setCategoryCode(orgCategory.getCategoryCode());
                category.setCategoryName(orgCategory.getCategoryName());
                detailList.add(category);
            }
            result.setDetailList(detailList);
        }

        result.setOrgRangeList(orgRangeList);
        result.setCategoryRangeList(categoryRangeList);
        return result;
    }

    @ApiOperation(value = "获取-采购组织和品类信息(合格,认证中)-根据供应商ID", notes = "获取-采购组织和品类信息(合格,认证中)-根据供应商ID", httpMethod = "POST")
    @PostMapping("/listOrgCategoryInfoByVendorIdForLimt")
    public OrgCatFormDTO listOrgCategoryInfoByVendorIdForLimt(@RequestBody OrgCatForm orgCatForm) {
        Assert.isTrue(orgCatForm.getVendorId()!= null,"供应商ID不能为空");

        OrgCatFormDTO result = new OrgCatFormDTO();

        List<Record> orgCategoryRecordList = qlService.queryByWrapper(QlWrappers.query(MqlType.ORGCATEGORY)
                .eq(PjOrgCategory::getCompanyId, orgCatForm.getVendorId())
                .in(PjOrgCategory::getServiceStatus, Arrays.asList(CategoryStatus.QUALIFIED,CategoryStatus.VERIFY)),Record.class);

        List<PjOrgCategory> orgCategoryList = MeiQl.toListValue(orgCategoryRecordList, PjOrgCategory.class);

        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(orgCategoryList)) {
            //控制范围-组织
            Map<Long, PjOrgCategory> orgMap = orgCategoryList.stream().filter(item -> Enable.Y.name().equals(item.getPjOrgStatus())).collect(Collectors.toMap(PjOrgCategory::getOrgId, item -> item, (k1, k2) -> k2));
            List<OrgCatFormCategory> orgRangeList = new ArrayList<>();
            for (Long key : orgMap.keySet()) {
                OrgCatFormCategory orgRange = new OrgCatFormCategory();
                orgRange.setOrgId(orgMap.get(key).getOrgId());
                orgRange.setOrgCode(orgMap.get(key).getOrgCode());
                orgRange.setOrgName(orgMap.get(key).getOrgName());
                orgRangeList.add(orgRange);
            }
            //控制范围-品类
            Map<Long, PjOrgCategory> categoryMap = orgCategoryList.stream().filter(item -> Enable.Y.name().equals(item.getPjCategoryStatus())).collect(Collectors.toMap(PjOrgCategory::getCategoryId, item -> item, (k1, k2) -> k2));
            List<OrgCatFormCategory> categoryRangeList = new ArrayList<>();
            for (Long key : categoryMap.keySet()) {
                OrgCatFormCategory categoryRange = new OrgCatFormCategory();
                categoryRange.setCategoryId(categoryMap.get(key).getCategoryId());
                categoryRange.setCategoryCode(categoryMap.get(key).getCategoryCode());
                categoryRange.setCategoryName(categoryMap.get(key).getCategoryName());
                categoryRangeList.add(categoryRange);
            }
            //控制明细
            List<OrgCatFormCategory> detailList = new ArrayList<>();
            for (PjOrgCategory orgCategory : orgCategoryList) {
                OrgCatFormCategory category = new OrgCatFormCategory();
                category.setOrgId(orgCategory.getOrgId());
                category.setOrgCode(orgCategory.getOrgCode());
                category.setOrgName(orgCategory.getOrgName());
                category.setCategoryId(orgCategory.getCategoryId());
                category.setCategoryCode(orgCategory.getCategoryCode());
                category.setCategoryName(orgCategory.getCategoryName());
                detailList.add(category);
            }
            result.setDetailList(detailList);
            result.setOrgRangeList(orgRangeList);
            result.setCategoryRangeList(categoryRangeList);
        }
        return result;
    }

}
