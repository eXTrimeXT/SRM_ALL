package com.midea.cloud.srm.supcooperate.divisioncategory.service.impl;

import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.pr.division.service.IDivisionCategoryService;
import com.midea.cloud.srm.supcooperate.divisioncategory.service.IPjDivisionCategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 品类分工接口实现类
 * @author huangbf3
 */
@Slf4j
@Service
public class PjDivisionCategoryImpl implements IPjDivisionCategoryService {
    @Autowired
    private IDivisionCategoryService iDivisionCategoryService;

    /**
     * 通过用户ID获取品类分工的品类ID
     * @param personInChargeUserId
     * @return
     */
    @ApiOperation("通过用户ID获取品类分工的品类ID")
    @Override
    public List<Long> getUserDivisionCategoryId(Long personInChargeUserId) {
        return iDivisionCategoryService.lambdaQuery()
                .groupBy(DivisionCategory::getCategoryId)
                .select(DivisionCategory::getCategoryId)
                .eq(DivisionCategory::getPersonInChargeUserId,personInChargeUserId)
                .list().stream().map(item->item.getCategoryId()).collect(Collectors.toList());
    }
}
