package com.midea.cloud.srm.base.category.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.meicloud.meida.constants.DbConstant;
import com.meicloud.meida.model.dto.Condition;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.base.category.dto.PullQueryDto;
import com.midea.cloud.srm.base.category.service.PjPurchaseCategoryService;
import com.midea.cloud.srm.base.purchase.mapper.PurchaseCategoryMapper;
import com.midea.cloud.srm.base.purchase.service.IPurchaseCategoryService;
import com.midea.cloud.srm.model.base.purchase.dto.PurchaseCategoryTreeNodeVO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.mideacloud.classification.api.MtClassificationApi;
import com.mideacloud.classification.dto.request.MtClassificationPageQueryDTO;
import com.mideacloud.classification.dto.request.MtClassificationRequestDTO;
import com.mideacloud.classification.dto.response.MtClassificationResponseDTO;
import com.mideacloud.common.dto.response.BaseResponse;
import com.mideacloud.common.dto.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangzh242
 * @date 2023/10/14 10:02
 */
@Slf4j
@Service
public class PjPurchaseCategoryServiceImpl extends ServiceImpl<PurchaseCategoryMapper, PurchaseCategory> implements PjPurchaseCategoryService {

    @Autowired
    private PurchaseCategoryMapper purchaseCategoryMapper;
    @Autowired
    private IPurchaseCategoryService purchaseCategoryService;

    @Resource
    private MtClassificationApi mtClassificationApi;

    private static final String SUCCESS_CODE = "0";

    private static final String PURCHASE = "purchase";

    private static final String LEVEL = "level";
    private static final String PARENT_IDS = "parentIds";
    private static final String CATEGORY_CODE = "categoryCode";
    private static final String CATEGORY_NAME = "categoryName";

    @Override
    public PageInfo<PurchaseCategory> listPageByParmForComponent(PurchaseCategory purchaseCategory) {
        PageInfo<PurchaseCategory> purchaseCategories = new PageInfo<>();
        if (StringUtils.isEmpty(purchaseCategory.getCategoryFullName())) {
            List<PurchaseCategory> purchaseCategoryList = purchaseCategoryMapper.selectList(new QueryWrapper<PurchaseCategory>().eq("LAST_LEVEL_FLAG", Enable.Y.name()));
            if (CollectionUtil.isNotEmpty(purchaseCategoryList)) {
                List<Condition> reqCondition = new ArrayList<>();
                addConditionWithAnd(reqCondition, new Condition(MtClassificationRequestDTO.ENABLED, DbConstant.SQL_EQ, Enable.Y.name(), null, null));
                if (StringUtils.isNotEmpty(purchaseCategory.getCategoryCode())) {
                    List<Condition> subConditionList = new ArrayList<>();
                    //CategoryCode字段，实际语义是对分类名称或分类编码的模糊查询
                    addConditionWithAnd(subConditionList, new Condition(MtClassificationRequestDTO.CLASSIFICATION_CODE, DbConstant.SQL_LIKE, purchaseCategory.getCategoryCode(), null, null));
                    addConditionWithOr(subConditionList, new Condition(MtClassificationRequestDTO.CLASSIFICATION_NAME, DbConstant.SQL_LIKE, purchaseCategory.getCategoryCode(), null, null));
                    Condition sub = new Condition();
                    sub.setConditions(subConditionList);
                    addConditionWithAnd(reqCondition, sub);
                }
                List<Long> ids = purchaseCategoryList.stream().map(a -> a.getCategoryId()).collect(Collectors.toList());
                addConditionWithAnd(reqCondition, new Condition(MtClassificationRequestDTO.MASTER_ID, DbConstant.SQL_IN, ids, null, null));
                MtClassificationPageQueryDTO mtClassificationPageQueryDTO = new MtClassificationPageQueryDTO();
                mtClassificationPageQueryDTO.setConditions(reqCondition);
                mtClassificationPageQueryDTO.setPageNum(purchaseCategory.getPageNum());
                mtClassificationPageQueryDTO.setPageSize(purchaseCategory.getPageSize());
                purchaseCategories = pageByRpc(mtClassificationPageQueryDTO);
            }
        } else {
            // 根据全名称,获取当前品类iD,再左匹配最末级
            List<String> categoryNameList = Arrays.asList(purchaseCategory.getCategoryFullName().split("-"));
            List<PurchaseCategory> categoryList = purchaseCategoryService.listByNameBatch(categoryNameList);
            List<PurchaseCategory> collect = categoryList.stream().filter(item -> Enable.Y.name().equals(item.getEnabled())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(collect)) {
                List<PurchaseCategory> sortedList = collect.stream()
                        .sorted(Comparator.comparing(PurchaseCategory::getLevel).reversed())
                        .collect(Collectors.toList());
                StrBuilder idStruct = new StrBuilder();
                Map<String, Long> nameIdMap = sortedList.stream().collect(Collectors.toMap(PurchaseCategory::getCategoryName, PurchaseCategory::getCategoryId, (k1, k2) -> k2));
                for (String name : categoryNameList) {
                    Long id = nameIdMap.get(name);
                    idStruct.append(id + "-");
                }
                List<PurchaseCategory> purchaseCategoryList = purchaseCategoryMapper.selectList(new QueryWrapper<PurchaseCategory>().eq("LAST_LEVEL_FLAG", Enable.Y.name()));
                if (CollectionUtil.isNotEmpty(purchaseCategoryList)) {
                    List<Condition> reqCondition = new ArrayList<>();
                    addConditionWithAnd(reqCondition, new Condition(MtClassificationRequestDTO.ENABLED, DbConstant.SQL_EQ, Enable.Y.name(), null, null));
                    if (StringUtils.isNotEmpty(purchaseCategory.getStruct())) {
                        addConditionWithAnd(reqCondition, new Condition(MtClassificationRequestDTO.CLASSIFICATION_PATH, DbConstant.RIGHT_LIKE, purchaseCategory.getStruct(), null, null));
                    } else {
                        addConditionWithAnd(reqCondition, new Condition(MtClassificationRequestDTO.CLASSIFICATION_PATH, DbConstant.RIGHT_LIKE, idStruct.toString().substring(0, idStruct.length() - 1), null, null));
                    }
                    if (StringUtils.isNotEmpty(purchaseCategory.getCategoryCode())) {
                        List<Condition> subConditionList = new ArrayList<>();
                        addConditionWithAnd(subConditionList, new Condition(MtClassificationRequestDTO.CLASSIFICATION_CODE, DbConstant.SQL_LIKE, purchaseCategory.getCategoryCode(), null, null));
                        addConditionWithOr(subConditionList, new Condition(MtClassificationRequestDTO.CLASSIFICATION_NAME, DbConstant.SQL_LIKE, purchaseCategory.getCategoryCode(), null, null));
                        Condition sub = new Condition();
                        sub.setConditions(subConditionList);
                        addConditionWithAnd(reqCondition, sub);
                    }
                    List<Long> ids = purchaseCategoryList.stream().map(a -> a.getCategoryId()).collect(Collectors.toList());
                    addConditionWithAnd(reqCondition, new Condition(MtClassificationRequestDTO.MASTER_ID, DbConstant.SQL_IN, ids, null, null));
                    MtClassificationPageQueryDTO mtClassificationPageQueryDTO = new MtClassificationPageQueryDTO();
                    mtClassificationPageQueryDTO.setConditions(reqCondition);
                    mtClassificationPageQueryDTO.setPageNum(purchaseCategory.getPageNum());
                    mtClassificationPageQueryDTO.setPageSize(purchaseCategory.getPageSize());
                    purchaseCategories = pageByRpc(mtClassificationPageQueryDTO);
                }
            }
        }
        return purchaseCategories;
    }

    /**
     * 调用物料中心的rpc接口进行分页查询
     *
     * @param mtClassificationPageQueryDTO
     * @return
     */
    private PageInfo<PurchaseCategory> pageByRpc(MtClassificationPageQueryDTO mtClassificationPageQueryDTO) {
        return pageByRpc(mtClassificationPageQueryDTO, true);
    }

    /**
     * 调用物料中心的rpc接口进行分页查询
     *
     * @param mtClassificationPageQueryDTO
     * @param fillWithLocal
     * @return
     */
    private PageInfo<PurchaseCategory> pageByRpc(MtClassificationPageQueryDTO mtClassificationPageQueryDTO, boolean fillWithLocal) {
        // 统一加上维度的查询条件
        addConditionWithAnd(mtClassificationPageQueryDTO.getConditions(), new Condition(MtClassificationRequestDTO.DIMENSION, DbConstant.SQL_EQ, PURCHASE, null, null));

        long startTime = System.currentTimeMillis();
        BaseResponse<PageResponse<MtClassificationResponseDTO>> pageResponseBaseResponse = mtClassificationApi.query(mtClassificationPageQueryDTO);
        long endTime = System.currentTimeMillis();
        if (!SUCCESS_CODE.equals(pageResponseBaseResponse.getCode())) {
            log.error("调用物料中心获取分类列表失败。参数：{}，返回信息:{},时延：{}", mtClassificationPageQueryDTO.toString(), pageResponseBaseResponse.toString(), endTime - startTime);
            throw new BaseException("调用物料中心获取分类列表失败");
        }


        log.info("调用物料中心获取分类列表成功。参数：{}，返回信息:{},时延：{}", mtClassificationPageQueryDTO.toString(), pageResponseBaseResponse.toString(), endTime - startTime);
        PageInfo<PurchaseCategory> pageInfo = new PageInfo<>();
        pageInfo.setTotal(pageResponseBaseResponse.getData().getTotal());
        pageInfo.setPageNum(pageResponseBaseResponse.getData().getPageNum().intValue());
        pageInfo.setPageSize(pageResponseBaseResponse.getData().getPageSize().intValue());
        pageInfo.setPages(pageResponseBaseResponse.getData().getPages().intValue());
        pageInfo.setList(convert(pageResponseBaseResponse.getData().getList(), fillWithLocal));


        return pageInfo;
    }


    /**
     * 把物料中心返回的物料分类对象转化成srm的物料分类对象
     *
     * @param mtClassificationResponseDTOList
     * @param fillWithLocal                   是否需要填充srm的db数据
     * @return
     */
    private List<PurchaseCategory> convert(List<MtClassificationResponseDTO> mtClassificationResponseDTOList, boolean fillWithLocal) {
        List<PurchaseCategory> purchaseCategoryList = new ArrayList<>();
        if (CollectionUtil.isEmpty(mtClassificationResponseDTOList)) {
            return purchaseCategoryList;
        }
        List<Long> ids = new ArrayList<>();
        for (MtClassificationResponseDTO mtClassificationResponseDTO : mtClassificationResponseDTOList) {
            purchaseCategoryList.add(convert(mtClassificationResponseDTO, false));
            ids.add(mtClassificationResponseDTO.getId());
        }

        if (!fillWithLocal) {
            return purchaseCategoryList;
        }
        //从db获取对象信息
        List<PurchaseCategory> localPurchaseCategoryList = purchaseCategoryMapper.selectBatchIds(ids);
        if (CollectionUtil.isEmpty(localPurchaseCategoryList)) {
            return purchaseCategoryList;
        } else {
            Map<Long, PurchaseCategory> longPurchaseCategoryMap = localPurchaseCategoryList.stream().collect(Collectors.toMap(PurchaseCategory::getCategoryId, Function.identity()));
            //忽略rpc获取的字段，其余字段用db获取的对象填充
            purchaseCategoryList.stream().forEach(purchaseCategory -> {
                if (longPurchaseCategoryMap.containsKey(purchaseCategory.getCategoryId())) {
                    BeanUtil.copyProperties(longPurchaseCategoryMap.get(purchaseCategory.getCategoryId()), purchaseCategory,
                            "categoryId", "categoryCode", "categoryName", "parentId", "level", "corder", "struct", "categoryFullName", "enabled",
                            "createdId", "createdBy", "creationDate", "lastUpdatedId", "lastUpdatedBy", "lastUpdateDate", "extensions");
                }
            });

        }

        fillParentInfo(purchaseCategoryList);

        return purchaseCategoryList;
    }

    private void fillParentInfo(PurchaseCategory purchaseCategory) {
        fillParentInfo(Arrays.asList(purchaseCategory));
    }

    /**
     * 填充父分类信息
     *
     * @param purchaseCategoryList
     */
    private void fillParentInfo(List<PurchaseCategory> purchaseCategoryList) {
        if (CollectionUtil.isEmpty(purchaseCategoryList)) {
            return;
        }

        List<Long> parentIds = new ArrayList<>();
        purchaseCategoryList.forEach(purchaseCategory -> {
            if (purchaseCategory.getParentId() != null) {
                parentIds.add(purchaseCategory.getParentId());
            }
        });

        if (CollectionUtil.isEmpty(parentIds)) {
            return;
        }

        List<PurchaseCategory> parentPurchaseCategoryList = listByIds(parentIds);
        if (CollectionUtil.isEmpty(parentPurchaseCategoryList)) {
            return;
        }

        Map<Long, PurchaseCategory> parentPurchaseCategoryMap = parentPurchaseCategoryList.stream().collect(Collectors.toMap(PurchaseCategory::getCategoryId, Function.identity()));

        purchaseCategoryList.forEach(purchaseCategory -> {
            if (purchaseCategory.getParentId() != null && parentPurchaseCategoryMap.containsKey(purchaseCategory.getParentId())) {
                purchaseCategory.setParentCode(parentPurchaseCategoryMap.get(purchaseCategory.getParentId()).getCategoryCode());
                purchaseCategory.setParentName(parentPurchaseCategoryMap.get(purchaseCategory.getParentId()).getCategoryName());
            }
        });
    }

    /**
     * 把物料中心返回的物料分类对象转化成srm的物料分类对象
     *
     * @param mtClassificationResponseDTO
     * @param fillWithLocal               是否需要填充srm的db数据
     * @return
     */
    private PurchaseCategory convert(MtClassificationResponseDTO mtClassificationResponseDTO, boolean fillWithLocal) {
        PurchaseCategory purchaseCategory = new PurchaseCategory();
        purchaseCategory.setCategoryId(mtClassificationResponseDTO.getId());
        purchaseCategory.setCategoryCode(mtClassificationResponseDTO.getClassificationCode());
        purchaseCategory.setCategoryName(mtClassificationResponseDTO.getClassificationName());
        purchaseCategory.setParentId(mtClassificationResponseDTO.getParentId());
        purchaseCategory.setLevel(mtClassificationResponseDTO.getLevel());
        purchaseCategory.setCorder(mtClassificationResponseDTO.getSortOrder().intValue());
        purchaseCategory.setEnabled(mtClassificationResponseDTO.getEnabled());
        purchaseCategory.setStruct(mtClassificationResponseDTO.getPath());
        purchaseCategory.setCategoryFullName(mtClassificationResponseDTO.getPathName());
        purchaseCategory.setExtensions(mtClassificationResponseDTO.getExtensions());
        if (mtClassificationResponseDTO.getAduit() != null) {
            purchaseCategory.setCreatedId(mtClassificationResponseDTO.getAduit().getCreatedId());
            purchaseCategory.setCreatedBy(mtClassificationResponseDTO.getAduit().getCreatedBy());
            purchaseCategory.setCreationDate(mtClassificationResponseDTO.getAduit().getCreationDate());
            purchaseCategory.setLastUpdatedId(mtClassificationResponseDTO.getAduit().getLastUpdatedId());
            purchaseCategory.setLastUpdatedBy(mtClassificationResponseDTO.getAduit().getLastUpdatedBy());
            purchaseCategory.setLastUpdateDate(mtClassificationResponseDTO.getAduit().getLastUpdateDate());
        }

        if (!fillWithLocal) {
            return purchaseCategory;
        }
        //从db获取对象信息
        PurchaseCategory localPurchaseCategory = purchaseCategoryMapper.selectById(purchaseCategory.getCategoryId());
        if (localPurchaseCategory != null) {
            //忽略rpc获取的字段，其余字段用db获取的对象填充
            BeanUtil.copyProperties(localPurchaseCategory, purchaseCategory,
                    "categoryId", "categoryCode", "categoryName", "parentId", "level", "corder", "struct", "categoryFullName", "enabled",
                    "createdId", "createdBy", "creationDate", "lastUpdatedId", "lastUpdatedBy", "lastUpdateDate", "extensions");
        }

        fillParentInfo(purchaseCategory);
        return purchaseCategory;
    }

    /**
     * 在条件列表中以and的形式添加
     *
     * @param conditionList
     * @param condition2Add
     */
    private void addConditionWithAnd(List<Condition> conditionList, Condition condition2Add) {
        if (CollectionUtil.isNotEmpty(conditionList)) {
            conditionList.add(getAndCondition());
        }
        conditionList.add(condition2Add);
    }

    /**
     * 在条件列表中以or的形式添加
     *
     * @param conditionList
     * @param condition2Add
     */
    private void addConditionWithOr(List<Condition> conditionList, Condition condition2Add) {
        if (CollectionUtil.isNotEmpty(conditionList)) {
            conditionList.add(getOrCondition());
        }
        conditionList.add(condition2Add);
    }

    private Condition getAndCondition() {
        return new Condition(null, null, null, DbConstant.SQL_AND, null);
    }

    private Condition getOrCondition() {
        return new Condition(null, null, null, DbConstant.OR, null);
    }

    /**
     * @param pullQueryList
     * @return
     */
    @Override
    public List<Long> getCategoryId(List<PullQueryDto> pullQueryList) {
        List<String> oneLevelCodeList = new ArrayList<>();
        List<String> twoLevelCodeList = new ArrayList<>();
        for (PullQueryDto e : pullQueryList) {
            if (CollectionUtils.isNotEmpty(e.getTwoLevelList())) {
                for (PullQueryDto a : e.getTwoLevelList()) {
                    twoLevelCodeList.add(a.getInfoCode());
                }
            } else {
                oneLevelCodeList.add(e.getInfoCode());
            }
        }
        return new ArrayList<>();
    }

    /**
     * 根据所选品类ID集合获取对应的所有末级品类
     */
    @Override
    public List<PurchaseCategory> listLastLevelCategoryByCodes(Set<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return Collections.emptyList();
        }
        // 1: 查询所有有效状态的品类
        List<PurchaseCategory> categoryList = listByRpc(Stream.of(
                // 只选择有效的
                new Condition(MtClassificationRequestDTO.ENABLED, DbConstant.SQL_EQ, YesOrNo.YES.getValue(), null, null)
        ).collect(Collectors.toList()), true);
        // 2: 过滤得到末级品类
        categoryList = categoryList.stream().filter(e -> {
            if (StringUtils.isNotBlank(e.getStruct())) {
                boolean isLast = false;
                for (Long categoryId : categoryIds) {
                    if (e.getStruct().contains(categoryId.toString()) && e.getStruct().endsWith("-" + e.getCategoryId())) {
                        // 该品类在指定品类ID的路径上，且在路径记录中排名末尾
                        isLast = true;
                        break;
                    }
                }
                return isLast;
            } else {
                return true;
            }
        }).collect(Collectors.toList());

        return categoryList;
    }

    @Override
    public PageInfo<PurchaseCategory> purchaseCategoryPageByCh(Map<String, Object> params) {
        PageInfo<PurchaseCategory> purchaseCategories = new PageInfo<>();
        List<Condition> reqCondition = new ArrayList<>();
        MtClassificationPageQueryDTO mtClassificationPageQueryDTO = new MtClassificationPageQueryDTO();
        mtClassificationPageQueryDTO.setConditions(reqCondition);
        mtClassificationPageQueryDTO.setPageNum(ObjectUtil.isEmpty(params.get("pageNum")) ? 1 : Convert.toInt(params.get("pageNum")));
        mtClassificationPageQueryDTO.setPageSize(ObjectUtil.isEmpty(params.get("pageSize")) ? 10 : Convert.toInt(params.get("pageSize")));
        if (ObjectUtil.isNotEmpty(params.get(LEVEL))) {
            addConditionWithAnd(reqCondition, new Condition(MtClassificationRequestDTO.LEVEL, DbConstant.SQL_EQ, params.get(LEVEL), null, null));
        }
        if (ObjectUtil.isNotEmpty(params.get(PARENT_IDS))) {
            addConditionWithAnd(reqCondition, new Condition(MtClassificationRequestDTO.PARENT_ID, DbConstant.SQL_IN, Collections.singletonList(Convert.toStr(params.get(PARENT_IDS))), null, null));
        }
        if (ObjectUtil.isNotEmpty(params.get(CATEGORY_CODE))) {
            addConditionWithAnd(reqCondition, new Condition(MtClassificationRequestDTO.CLASSIFICATION_CODE, DbConstant.SQL_LIKE, params.get(CATEGORY_CODE), null, null));
        }
        if (ObjectUtil.isNotEmpty(params.get(CATEGORY_NAME))) {
            addConditionWithAnd(reqCondition, new Condition(MtClassificationRequestDTO.CLASSIFICATION_NAME, DbConstant.SQL_LIKE, params.get(CATEGORY_NAME), null, null));
        }
        purchaseCategories = pageByRpc(mtClassificationPageQueryDTO);
        return purchaseCategories;
    }

    @Override
    public List<PurchaseCategory> listLastLevelCategoryByIds(Set<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return Collections.emptyList();
        }
        // 1: 查询所有有效状态的品类
        List<PurchaseCategory> categoryList = listByRpc(Stream.of(
                // 只选择有效的
                new Condition(MtClassificationRequestDTO.ENABLED, DbConstant.SQL_EQ, YesOrNo.YES.getValue(), null, null)
        ).collect(Collectors.toList()), true);
        // 2: 获取level=4,并且品类id路径包含入参的id数据
        return categoryList.stream()
                .filter(category -> category.getLevel() == 4
                        && category.getStruct() != null
                        && categoryIds.stream().anyMatch(categoryId -> category.getStruct().contains(categoryId.toString())))
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseCategoryTreeNodeVO> getLastThreeLevelCategoryTree() {
        //根据编码，查询1级分类
        List<Condition> level1condition = new ArrayList<>();
        //固定1级品类为物资类
        addConditionWithAnd(level1condition, new Condition(MtClassificationRequestDTO.OBJECT_CODE, DbConstant.SQL_EQ, "NA", null, null));
        addConditionWithAnd(level1condition, new Condition(MtClassificationRequestDTO.LEVEL, DbConstant.SQL_EQ, "1", null, null));
        //查询物资类一级品类
        List<PurchaseCategory> level1purchaseCategories = listByRpc(level1condition);
        Assert.isTrue(ObjectUtil.isNotEmpty(level1purchaseCategories), "查询失败：获取一级品类编码为NA的品类数据为空");
        //查询物资类二级品类
        Long parentId = level1purchaseCategories.get(0).getCategoryId();
        List<Condition> condition = new ArrayList<>();
        //固定1级品类为物资类
        addConditionWithAnd(condition, new Condition(MtClassificationRequestDTO.PARENT_ID, DbConstant.SQL_EQ, parentId, null, null));
        //查询物资类二级品类
        List<PurchaseCategory> purchaseCategories = listByRpc(condition);
        //查询三级
        List<Long> ids = purchaseCategories.stream().map(PurchaseCategory::getCategoryId).collect(Collectors.toList());
        List<Condition> level3Condition = new ArrayList<>();
        addConditionWithAnd(level3Condition, new Condition(MtClassificationRequestDTO.PARENT_ID, DbConstant.SQL_IN, ids, null, null));
        List<PurchaseCategory> level3PurchaseCategories = listByRpc(level3Condition);
        purchaseCategories.addAll(level3PurchaseCategories);
        //查询四级
        ids = level3PurchaseCategories.stream().map(PurchaseCategory::getCategoryId).collect(Collectors.toList());
        List<Condition> level4Condition = new ArrayList<>();
        addConditionWithAnd(level4Condition, new Condition(MtClassificationRequestDTO.PARENT_ID, DbConstant.SQL_IN, ids, null, null));
        List<PurchaseCategory> level4PurchaseCategories = listByRpc(level4Condition);
        purchaseCategories.addAll(level4PurchaseCategories);
        return this.buildTree(purchaseCategories, parentId);
    }

    private List<PurchaseCategory> listByRpc(List<Condition> conditions) {
        return listByRpc(conditions, true);
    }
    /**
     * 调用物料中心接口获取物料分类信息
     */
    private List<PurchaseCategory> listByRpc(List<Condition> conditions, boolean fillWithLocal) {
        // 统一加上维度的查询条件
        addConditionWithAnd(conditions, new Condition(MtClassificationRequestDTO.DIMENSION, DbConstant.SQL_EQ, PURCHASE, null, null));

        List<PurchaseCategory> resultList = new ArrayList<>();
        long total = Long.MAX_VALUE;
        int num1000 = 1000;
        int pageNum = 1;
        while ((pageNum - 1) * num1000 < total) {
            MtClassificationPageQueryDTO mtClassificationPageQueryDTO = new MtClassificationPageQueryDTO();
            mtClassificationPageQueryDTO.setPageNum(pageNum);
            mtClassificationPageQueryDTO.setPageSize(num1000);
            mtClassificationPageQueryDTO.setConditions(conditions);
            long startTime = System.currentTimeMillis();
            //分页获取
            BaseResponse<PageResponse<MtClassificationResponseDTO>> pageResponseBaseResponse = mtClassificationApi.query(mtClassificationPageQueryDTO);
            long endTime = System.currentTimeMillis();
            if (!SUCCESS_CODE.equals(pageResponseBaseResponse.getCode())) {
                log.error("调用物料中心获取分类列表失败。参数：{}，返回信息:{},时延：{}", mtClassificationPageQueryDTO, pageResponseBaseResponse, endTime - startTime);
                throw new BaseException("调用物料中心获取分类列表失败");
            }

            log.info("调用物料中心获取分类列表成功。参数：{}，返回信息:{},时延：{}", mtClassificationPageQueryDTO, pageResponseBaseResponse, endTime - startTime);

            // 把当前页数据添加到返回列表的
            if (CollectionUtil.isNotEmpty(pageResponseBaseResponse.getData().getList())) {
                resultList.addAll(convert(pageResponseBaseResponse.getData().getList(), fillWithLocal));
            }

            total = pageResponseBaseResponse.getData().getTotal();
            pageNum++;
        }

        return resultList;
    }

    /**
     * 递归
     *
     * @param dataList
     * @param parentId
     * @return
     */
    public List<PurchaseCategoryTreeNodeVO> buildTree(List<PurchaseCategory> dataList, Long parentId) {
        List<PurchaseCategoryTreeNodeVO> result = new ArrayList<>();
        for (PurchaseCategory data : dataList) {
            if (data.getParentId().equals(parentId)) {
                PurchaseCategoryTreeNodeVO node = new PurchaseCategoryTreeNodeVO();
                BeanUtil.copyProperties(data, node);
                node.setChildren(buildTree(dataList, node.getCategoryId()));
                result.add(node);
            }
        }
        return result;
    }
}
