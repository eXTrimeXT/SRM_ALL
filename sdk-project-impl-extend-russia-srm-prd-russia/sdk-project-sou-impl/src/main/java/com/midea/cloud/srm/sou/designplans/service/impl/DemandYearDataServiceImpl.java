package com.midea.cloud.srm.sou.designplans.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.SouCommonUtil;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.SouExtRbacClient;
import com.midea.cloud.srm.feign.SupplierCooperateClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.sou.designplans.dto.PullQueryDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;
import com.midea.cloud.srm.sou.designplans.mapper.DemandYearDataMapper;
import com.midea.cloud.srm.sou.designplans.service.DemandYearDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Service
public class DemandYearDataServiceImpl extends BaseServiceImpl<DemandYearDataMapper, SccSouChDemandYearData> implements DemandYearDataService {

    @Resource
    private DemandYearDataMapper demandYearDataMapper;

    @Resource
    private BaseExtClient baseExtClient;

    @Resource
    private SupplierCooperateClient supplierCooperateClient;

    @Resource
    private SouExtRbacClient souExtRbacClient;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private SouCommonUtil souCommonUtil;
    @Autowired
    private BaseClient baseClient;
    /**
     * 拉取数据
     * @param pullQuery 入参
     * @param type 类型
     * @param designId 提报策划方案
     * @return 返回数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SccSouChDemandYearData> pullOrder(PullQueryDto pullQuery, Integer type, Long designId) {
        
        LambdaQueryWrapper<SccSouChDemandYearData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SccSouChDemandYearData::getType, type).eq(SccSouChDemandYearData::getDesignId, designId);
        demandYearDataMapper.delete(queryWrapper);
//        Assert.isTrue(ObjectUtil.isNotEmpty(pullQuery.getCategoryIds()), "入参品类ID数组不能为空");
        //第四层级品类id
        if (ObjectUtil.isNotEmpty(pullQuery.getCategoryIds())) {
            List<PurchaseCategory> categoryList = baseMapper.queryCategoryByIds(pullQuery.getCategoryIds());
//            List<PurchaseCategory> categoryList = baseExtClient.listLastLevelCategoryByIds(pullQuery.getCategoryIds());
            Set<Long> categoryIds = categoryList.stream().map(PurchaseCategory::getCategoryId).collect(Collectors.toSet());
            pullQuery.setCategoryIds(categoryIds);
        } else {
            pullQuery.setCategoryIds(null);
        }
        HrUserOrgnizationDto userOrgnizationDto = pjProjectExtClient.getHrUserOrgnizationByUsername(AppUserUtil.getLoginAppUser().getUsername());
        Assert.isTrue(userOrgnizationDto != null && userOrgnizationDto.getOuOrganization() != null, "操作失败：用户公司信息为空");
        //订单信息
        List<SccSouChDemandYearData> dataList = supplierCooperateClient.getOrderLineHeadList(pullQuery);
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<Long> CategoryIds = dataList.stream().map(SccSouChDemandYearData::getCategoryId).collect(Collectors.toList());
        Map<Long, PurchaseCategory> level1PurchaseCategoryMap = this.processCategories(1, CategoryIds);
        Map<Long, PurchaseCategory> level2PurchaseCategoryMap = this.processCategories(2, CategoryIds);
        dataList.forEach(e -> {
            e.setType(type);
            e.setDesignId(designId);
            e.setDataSource("系统");
            e.setCreateUnitId(userOrgnizationDto.getOuOrganization().getOrganizationId());//当前用户所在公司id
            e.setCreateUnitCode(userOrgnizationDto.getOuOrganization().getOrganizationCode());
            e.setCreateUnitName(userOrgnizationDto.getOuOrganization().getOrganizationName());
            //获取一二级品类
            if (ObjectUtil.isNotEmpty(e.getCategoryId())) {
                //Map<String, PurchaseCategory> categoryMap = souCommonUtil.getSecondCategory(e.getCategoryId());
                PurchaseCategory categoryMapLevel1 = level1PurchaseCategoryMap.get(e.getCategoryId());
                Assert.isTrue(categoryMapLevel1 != null, "获取一级品类数据失败，品类ID=" + e.getCategoryId());
                PurchaseCategory categoryMapLevel2 = level2PurchaseCategoryMap.get(e.getCategoryId());
                Assert.isTrue(categoryMapLevel2 != null, "获取二级品类数据失败，品类ID=" + e.getCategoryId());
                //赋值1级品类
                //PurchaseCategory level1 = categoryMap.get("1");
                e.setOneTypeId(categoryMapLevel1.getParentId());
                e.setOneTypeCode(categoryMapLevel1.getCategoryCode());
                e.setOneTypeName(categoryMapLevel1.getCategoryName());
                //赋值2级品类
                //PurchaseCategory level2 = categoryMap.get("2");
                e.setTwoTypeId(categoryMapLevel2.getParentId());
                e.setTwoTypeCode(categoryMapLevel2.getCategoryCode());
                e.setTwoTypeName(categoryMapLevel2.getCategoryName());
            }
            //获取税率
            e.setRatePrice(NumberUtil.mul(e.getPriceTax(), NumberUtil.add(1, NumberUtil.div(e.getTaxRate(), 100))));//含税单价=未税单价x(1+税率)
            e.setMoneyAmount(NumberUtil.mul(e.getPriceTax(), e.getOrderNum()));//金额=未税单价*数量
            e.setPriceTotal(NumberUtil.mul(e.getRatePrice(), e.getOrderNum()));//价税合计=含税单价*数量
        });
        //创建单位
        this.saveBatch(dataList);
        return dataList;
    }

    private Map<Long, PurchaseCategory> processCategories(int level, List<Long> categoryIds) {
        int batchSize = 1000;
        List<PurchaseCategory> purchaseCategoryList = new ArrayList<>();
        for (int i = 0; i < categoryIds.size(); i += batchSize) {
            List<Long> batchIds = categoryIds.subList(i, Math.min(i + batchSize, categoryIds.size()));
            // 这里可以调用处理每个批次ID的方法
            if (level == 1) {
                purchaseCategoryList.addAll(baseMapper.queryLevel1purchaseCategory(batchIds));
            } else {
                purchaseCategoryList.addAll(baseMapper.queryLevel2purchaseCategory(batchIds));
            }

        }
        return purchaseCategoryList.stream().collect(Collectors.toMap(PurchaseCategory::getCategoryId, category -> category));
    }
}
