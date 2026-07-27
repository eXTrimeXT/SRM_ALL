package com.midea.cloud.srm.base.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.base.purchase.mapper.PurchaseCategoryMapper;
import com.midea.cloud.srm.base.purchase.service.IPurchaseCategoryService;
import com.midea.cloud.srm.model.base.category.dto.CategoryQuickSearchParam;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.quicksearch.common.InputSearchParam;
import com.midea.cloud.srm.model.constant.SrmConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/5/29
 */
@Component
@Slf4j
public class CategoryForQuickSearchComponent {

    @Autowired
    private PurchaseCategoryMapper purchaseCategoryMapper;

    /**
     * 品类信息快查--弹出框
     * @param param
     * @return
     */
    public PageInfo<PurchaseCategory> listPageByCondition(CategoryQuickSearchParam param) {
        //默认查询末级品类数据
        if(param.getLastLevelFlag() == null) {
            param.setLastLevelFlag(YesOrNo.YES.getValue());
        }
        //查询启用的品类
        param.setEnabled(YesOrNo.YES.getValue());
        //MyBatis查询条件解析---快查传参
        LambdaQueryWrapper<PurchaseCategory> queryWrapper = new LambdaQueryWrapper<>();
        //默认的请求参数
        queryWrapper.eq(StringUtils.isNotBlank(param.getLastLevelFlag()), PurchaseCategory::getLastLevelFlag, param.getLastLevelFlag());
        queryWrapper.eq(StringUtils.isNotBlank(param.getEnabled()), PurchaseCategory::getEnabled, param.getEnabled());

        //判断是否下拉选择模式
        if(StringUtils.isNotBlank(param.getInputValue())) {
            queryWrapper.and(a -> a.like(PurchaseCategory::getCategoryCode, param.getInputValue()).or(o -> o.like(PurchaseCategory::getCategoryName, param.getInputValue())));
        } else {
            //弹出窗口模式
            queryWrapper.like(StringUtils.isNotBlank(param.getCategoryCode()), PurchaseCategory::getCategoryCode, param.getCategoryCode());
            queryWrapper.like(StringUtils.isNotBlank(param.getCategoryName()), PurchaseCategory::getCategoryName, param.getCategoryName());
            queryWrapper.eq(!Objects.isNull(param.getLevel()), PurchaseCategory::getLevel, param.getLevel());
        }

        //设置分页查询参数，默认15每页
        PageUtil.startPage(ObjectUtils.defaultIfNull(param.getPageNum(), 1), ObjectUtils.defaultIfNull(param.getPageSize(), 15));

        //访问数据库，查询品类数据
        List<PurchaseCategory> categoryList = purchaseCategoryMapper.selectList(queryWrapper);

        //构造返回分页结果对象
        PageInfo<PurchaseCategory> purchaseCategoryPageInfo = new PageInfo<>(categoryList);

        //递归查询父类品类信息，构造品类全路径
        Map<Long, PurchaseCategory> purchaseCategoryMap = categoryList.stream().collect(Collectors.toMap(k -> k.getCategoryId(), Function.identity(), (k1, k2) -> k2));
        parentCategory(categoryList.stream().map(k -> k.getParentId()).filter(id -> ObjectUtils.allNotNull(id)).collect(Collectors.toList()), purchaseCategoryMap);

        //构造品类全路径信息
        categoryList.stream().forEach(category -> {
            //存放每一个品类名称，用于构造全路径名称，第一个元素为本品类，第二个元素为第一个品类的父类，以此类推
            List<String> fullNameList = new ArrayList<>(15);
            //存放每一个品类ID，用于构建全路径ID,第一个元素为本品类，第二个元素为第一个品类的父类，以此类推
            List<String> fullIdList = new ArrayList<>(15);

            //拿到父类品类
            PurchaseCategory purchaseCategory = purchaseCategoryMap.get(category.getCategoryId());
            //向上递归父类品类信息
            while (ObjectUtils.allNotNull(purchaseCategory)) {
                fullNameList.add(purchaseCategory.getCategoryName());
                fullIdList.add(Objects.toString(purchaseCategory.getCategoryId()));

                purchaseCategory = purchaseCategoryMap.get(purchaseCategory.getParentId());
            }

            //构造品类全路径时，父类在前面，于是对列表所有元素进行反转
            Collections.reverse(fullNameList);
            Collections.reverse(fullIdList);

            //构造全路径信息，以-分割
            category.setCategoryFullName(fullNameList.stream().collect(Collectors.joining(SrmConstant.SHORT_LINE)));
            category.setStruct(fullIdList.stream().collect(Collectors.joining(SrmConstant.SHORT_LINE)));

        });

        return purchaseCategoryPageInfo;
    }

    /**
     * 递归父类品类信息
     * @param parentCategroyIdList
     * @param categoryMap
     */
    private void parentCategory(List<Long> parentCategroyIdList, Map<Long, PurchaseCategory> categoryMap) {
        //没有请求参数时终止程序
        if(CollectionUtils.isEmpty(parentCategroyIdList)) {
            return;
        }
        LambdaQueryWrapper<PurchaseCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PurchaseCategory::getCategoryId, parentCategroyIdList);
        //根据品类ID查询数据库
        List<PurchaseCategory> categoryList = purchaseCategoryMapper.selectList(queryWrapper);
        //没有父类时终止程序
        if(CollectionUtils.isEmpty(categoryList)) {
            return;
        }
        Map<Long, PurchaseCategory> purchaseCategoryMap = categoryList.stream().collect(Collectors.toMap(k -> k.getCategoryId(), Function.identity(), (k1, k2) -> k2));
        categoryMap.putAll(purchaseCategoryMap);
        //递归调用，找出所有的父类
        parentCategory(categoryList.stream().map(k -> k.getParentId()).filter(id -> ObjectUtils.allNotNull(id)).collect(Collectors.toList()), categoryMap);
    }

    /**
     * 品类信息快查--下拉选择
     * @param param
     * @return
     */
    public PageInfo<PurchaseCategory> listPageByCondition(InputSearchParam param) {
        CategoryQuickSearchParam categoryQuickSearchParam = new CategoryQuickSearchParam();
        categoryQuickSearchParam.setInputValue(param.getInputValue());
        categoryQuickSearchParam.setPageNum(param.getPageNum());
        categoryQuickSearchParam.setPageSize(param.getPageSize());

        return listPageByCondition(categoryQuickSearchParam);
    }
}
