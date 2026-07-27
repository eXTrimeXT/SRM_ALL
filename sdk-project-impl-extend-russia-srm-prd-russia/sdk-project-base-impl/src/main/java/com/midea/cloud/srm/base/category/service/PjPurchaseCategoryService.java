package com.midea.cloud.srm.base.category.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.base.category.dto.PullQueryDto;
import com.midea.cloud.srm.model.base.purchase.dto.PurchaseCategoryTreeNodeVO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 备注
 *
 * @author huangbf3
 */
public interface PjPurchaseCategoryService {
    /**
     * 备注
     * @param purchaseCategory 参数
     * @return 返回
     */
    PageInfo<PurchaseCategory> listPageByParmForComponent(PurchaseCategory purchaseCategory);

    /**
     * 备注
     * @param pullQueryList 参数
     * @return 返回
     */
    List<Long> getCategoryId(List<PullQueryDto> pullQueryList);

    /**
     * 根据所选品类ID集合获取对应的所有末级品类
     * @param categoryIds 参数
     * @return 返回
     */
    List<PurchaseCategory> listLastLevelCategoryByCodes(Set<Long> categoryIds);

    /**
     * 提报策划方案-查询一二级品类
     *
     * @param params
     * @return
     */
    PageInfo<PurchaseCategory> purchaseCategoryPageByCh(Map<String, Object> params);

    /**
     * 根据所选品类ID集合查询末级品类
     *
     * @param categoryIds
     * @return
     */
    List<PurchaseCategory> listLastLevelCategoryByIds(Set<Long> categoryIds);

    /**
     * 查询末尾三级品类树结构
     * @return
     */
    List<PurchaseCategoryTreeNodeVO> getLastThreeLevelCategoryTree();
}
