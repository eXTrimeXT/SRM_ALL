package com.midea.cloud.srm.base.category.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.base.category.dto.PullQueryDto;
import com.midea.cloud.srm.base.category.service.PjPurchaseCategoryService;
import com.midea.cloud.srm.model.base.purchase.dto.PurchaseCategoryTreeNodeVO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangzh242
 * @date 2023/10/10 17:29
 */
@Api(value = "PjCategoryController", tags = "二开品类接口")
@RestController
@RequestMapping("/pj/category")
public class PjCategoryController {

    @Autowired
    private PjPurchaseCategoryService pjPurchaseCategoryService;


    @PostMapping("/listPageByParmForComponent")
    @ApiOperation(value = "分页条件查询采购分类信息-穿梭框组件", notes = "分页条件查询采购分类信息-穿梭框组件")
    public PageInfo<PurchaseCategory> listPageByParmForComponent(@RequestBody PurchaseCategory purchaseCategory) {
        if (purchaseCategory.getPageSize() == null || purchaseCategory.getPageNum() == null) {
            //如果没有传分页参数，添加默认分页参数
            purchaseCategory.setPageNum(1);
            purchaseCategory.setPageSize(10);
        }
        return pjPurchaseCategoryService.listPageByParmForComponent(purchaseCategory);
    }

    @PostMapping("/getLevelInfo")
    @ApiOperation(value = "获取层级", notes = "获取层级")
    public List<Long> getLevelInfo(@RequestBody List<PullQueryDto> pullQueryList) {
        return pjPurchaseCategoryService.getCategoryId(pullQueryList);
    }

    @PostMapping("/listLastLevelCategoryByCodes")
    @ApiOperation("根据所选品类ID集合获取对应的所有末级品类")
    public List<PurchaseCategory> listLastLevelCategoryByCodes(@RequestBody Set<Long> categoryIds) {
        return pjPurchaseCategoryService.listLastLevelCategoryByCodes(categoryIds);
    }

    @PostMapping("/purchaseCategoryPageByCh")
    @ApiOperation(value = "分页条件查询品类信息-提报策划方案", notes = "分页条件查询品类信息-提报策划方案")
    public PageInfo<PurchaseCategory> purchaseCategoryPageByCh(@RequestBody Map<String, Object> params) {
        return pjPurchaseCategoryService.purchaseCategoryPageByCh(params);
    }

    @PostMapping("/listLastLevelCategoryByIds")
    @ApiOperation("根据所选品类ID集合查询末级品类")
    public List<PurchaseCategory> listLastLevelCategoryByIds(@RequestBody Set<Long> categoryIds) {
        return pjPurchaseCategoryService.listLastLevelCategoryByIds(categoryIds);
    }

    /**
     * 查询末尾三级品类树结构
     */
    @GetMapping("/lastThreeLevelCategoryTree")
    @ApiOperation(value = "查询末尾三级品类树结构", notes = "查询末尾三级品类树结构")
    public List<PurchaseCategoryTreeNodeVO> getLastThreeLevelCategoryTree() {
        return pjPurchaseCategoryService.getLastThreeLevelCategoryTree();
    }
}
