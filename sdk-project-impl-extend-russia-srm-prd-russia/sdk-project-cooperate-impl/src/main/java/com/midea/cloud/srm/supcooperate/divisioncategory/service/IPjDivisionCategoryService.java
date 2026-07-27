package com.midea.cloud.srm.supcooperate.divisioncategory.service;

import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 品类分工接口定义类
 * @author huangbf3
 */
public interface IPjDivisionCategoryService {
    /**
     * 通过用户ID获取品类分工的品类ID
     * @param personInChargeUserId
     * @return
     */
    @ApiOperation("通过用户ID获取品类分工的品类ID")
    List<Long> getUserDivisionCategoryId(Long personInChargeUserId);
}
