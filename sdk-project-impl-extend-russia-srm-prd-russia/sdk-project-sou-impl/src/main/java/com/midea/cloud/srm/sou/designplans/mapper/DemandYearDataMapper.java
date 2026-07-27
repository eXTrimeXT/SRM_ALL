package com.midea.cloud.srm.sou.designplans.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * 备注
 * @author huangbf3
 */
@Mapper
public interface DemandYearDataMapper extends BaseMapper<SccSouChDemandYearData> {
    /**
     * queryCategoryByIds
     * @param categoryIds
     * @return
     */
    List<PurchaseCategory> queryCategoryByIds(Set<Long> categoryIds);

    /**
     * queryLevel1purchaseCategory
     * @param categoryIds
     * @return
     */
    List<PurchaseCategory> queryLevel1purchaseCategory(List<Long> categoryIds);

    /**
     * queryLevel2purchaseCategory
     * @param categoryIds
     * @return
     */
    List<PurchaseCategory> queryLevel2purchaseCategory(List<Long> categoryIds);
}
