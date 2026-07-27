package com.midea.cloud.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseTax;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/12/20 18:52
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class SouCommonUtil {

    private static final int NUM3 = 3;


    @Autowired
    private BaseClient baseClient;
    public static void cleanupStandardFields(BaseEntity entity) {
        entity.setCreatedId(null);
        entity.setCreatedBy(null);
        entity.setCreationDate(null);
        entity.setCreatedByIp(null);
        entity.setCreatedFullName(null);
        entity.setLastUpdatedId(null);
        entity.setLastUpdatedBy(null);
        entity.setLastUpdateDate(null);
        entity.setLastUpdatedByIp(null);
        entity.setLastUpdatedFullName(null);
    }

    /**
     * 获取二级品类
     *
     * @param categoryId 末级品类id
     * @return
     */
    public Map<String, PurchaseCategory> getSecondCategory(Long categoryId) {
        Assert.notNull(categoryId, "品类ID不能为空");
        Map<String, PurchaseCategory> map = new HashMap<>(15);
        log.info("入参品类ID：{}", categoryId);
        List<PurchaseCategory> purchaseCategoryList = baseClient.listCategoryByIds(new ArrayList<>(Collections.singletonList(categoryId)));
        log.info("获取末级品类：" + purchaseCategoryList);
        Assert.isTrue(ObjectUtil.isNotEmpty(purchaseCategoryList), "操作失败：获取品类数据为空");
        //i=0时获得3级品类数据，=1时获取到2级品类数据，=2时获取到1级品类数据
        for (int i = 0; i < NUM3; i++) {
            purchaseCategoryList = baseClient.listCategoryByIds(new ArrayList<>(Collections.singletonList(purchaseCategoryList.get(0).getParentId())));
            if (ObjectUtil.isNotEmpty(purchaseCategoryList) && purchaseCategoryList.get(0).getLevel() == 2) {
                map.put("2", purchaseCategoryList.get(0));
            }
            if (ObjectUtil.isNotEmpty(purchaseCategoryList) && purchaseCategoryList.get(0).getLevel() == 1) {
                map.put("1", purchaseCategoryList.get(0));
                break;
            }
        }
        return map;
    }

    public static PurchaseTax getTax(SccSouChDemandYearData yearData, List<PurchaseTax> purchaseTaxList) {
        Optional<PurchaseTax> purchaseTaxOptional = purchaseTaxList.stream()
                .filter(tax -> yearData.getTaxRateCode().equals(tax.getTaxKey()))
                .findFirst();
        Assert.isTrue(purchaseTaxOptional.isPresent(), "税率编码：" + yearData.getTaxRateCode() + " 无法匹配到数据，请检查");
        return purchaseTaxOptional.get();
    }
}
