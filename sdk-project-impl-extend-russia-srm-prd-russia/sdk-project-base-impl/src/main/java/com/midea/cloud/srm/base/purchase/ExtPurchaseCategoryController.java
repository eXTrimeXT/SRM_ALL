package com.midea.cloud.srm.base.purchase;

import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.base.purchase.service.IPurchaseCategoryService;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
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
import java.util.List;

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
 *  修改日期: 2023/12/6 11:04
 *  修改内容:
 * </pre>
 */
@RestController
@RequestMapping("/purchase/ext/purchaseCategory")
@Api(value = "PurchaseCategoryController", tags = "采购分类二开")
public class ExtPurchaseCategoryController {
    @Autowired
    private IPurchaseCategoryService iPurchaseCategoryService;

    /**
     * 查找物料大类
     *
     * @param purchaseCategory
     * @return
     */
    @PostMapping("/queryMaxLevelCategory")
    @ApiOperation(value = "查找物料大类", notes = "查找物料大类")
    public PurchaseCategory queryMaxLevelCategory(@RequestBody PurchaseCategory purchaseCategory) {
        PurchaseCategory item = iPurchaseCategoryService.getById(purchaseCategory.getCategoryId());
        checkIfCorrect(item);
        while (item.getLevel() != 1) {
            item = iPurchaseCategoryService.getById(item.getParentId());
            checkIfCorrect(item);
        }
        return item;
    }

    /**
     * 批量查找物料大类(商城-购物车专用)
     *
     * @param categoryIds
     * @return
     */
    @PostMapping("/queryMaxLevelCategoryList")
    @ApiOperation(value = "批量查找物料大类(商城-购物车专用)", notes = "查找物料大类(批量)")
    public List<PurchaseCategory> queryMaxLevelCategoryList(@RequestBody List<Long> categoryIds) {
        List<PurchaseCategory> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(categoryIds)) {
            for (Long categoryId : categoryIds) {
                PurchaseCategory item = iPurchaseCategoryService.getById(categoryId);
                checkIfCorrect(item);
                while (item.getLevel() != 1) {
                    item = iPurchaseCategoryService.getById(item.getParentId());
                    checkIfCorrect(item);
                }
                item.setCategoryId(categoryId);
                list.add(item);
            }
        }
        return list;
    }

    private void checkIfCorrect(PurchaseCategory item) {
        Assert.notNull(item, LocaleHandler.getLocaleMsg("查询不到采购分类,分类id为：" + item.getCategoryId()));
        if (item.getLevel() != 1 &&
                item.getLevel() != 2 &&
                item.getLevel() != 3 &&
                item.getLevel() != 4
        ) {
            Assert.notNull(null, LocaleHandler.getLocaleMsg("采购分类数据格式不正确"));
        }
    }
}
