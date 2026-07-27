package com.midea.cloud.srm.sou.inq.ext.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.feign.ExtSupplierClient;
import com.midea.cloud.srm.model.common.enums.CategoryStatus;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouItemQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendor;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendorDel;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPjInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouItemQueryVO;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSourceFromTypeEnum;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.OrgCategory;
import com.midea.cloud.srm.model.supplier.vendororgcategory.dto.VendorAiRecommendDTO;
import com.midea.cloud.srm.model.supplier.vendororgcategory.vo.AiRecommendCompanyInfoVO;
import com.midea.cloud.srm.model.supplier.vendororgcategory.vo.VendorAiRecommendVO;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPJInqSouVendorDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPJInqSouVendorDelDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouOrderMapper;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouInitQueryService;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 长城 - 询比价 - 立项 - 查询服务
 * @author huangbf3
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouInitQueryServiceImpl implements ExtInqSouInitQueryService {

    @Autowired
    private ExtPJInqSouVendorDelDAO extPjInqSouVendorDelDao;
    @Autowired
    private ExtSupplierClient extSupplierClient;
    @Autowired
    private InqSouProjectDAO inqSouProjectDAO;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private ExtPjInqSouOrderMapper extPjInqSouOrderMapper;

    @Autowired
    private ExtPJInqSouVendorDAO extPJInqSouVendorDao;

    @Autowired
    private IExtSouVendorService extSouVendorService;

    /**
     * 供应商智能推荐
     */
    @Override
    public List<AiRecommendCompanyInfoVO> getVendorAiRecommend(long projectId) {
        InqSouProject inqSouProject = inqSouProjectDAO.getById(projectId);
        AssertUtils.notNull(inqSouProject, "询价单[{0}]不存在", projectId);
        List<SouItem> souItemList = souItemDAO.list(SouItem::getProjectId, projectId);

        // 1: 查询【排除黑名单+排除非本业务实体供应商+排除业务实体退出/冻结供应商+排除品类状态+排除是否重点关注】供应商
        // TODO 仅过滤供应商状态：准供应商、正式供应商
        VendorAiRecommendDTO param = new VendorAiRecommendDTO(); {
            param.setExcludeBlackVendors(inqSouProject.getExcludeBlackVendors());
            param.setExcludeNoCurrentOrgVendors(inqSouProject.getExcludeNoCurrentOrgVendors());
            param.setExcludeOrgQuitVendors(inqSouProject.getExcludeOrgQuitVendors());
            param.setExcludeOrgCategoryStatus(inqSouProject.getExcludeOrgCategoryStatus());
            param.setOrgCategoryList(new ArrayList<>(10)); {
                param.setOrgCategoryList(souItemList.stream()
                        .filter(e -> e.getOrgOuCode() != null && e.getCategoryCode() != null)
                        .map(e -> {
                            OrgCategory oc = new OrgCategory();
                            oc.setOrgId(e.getOrgOuId());
                            oc.setOrgCode(e.getOrgOuCode());
                            oc.setOrgName(e.getOrgOuName());
                            oc.setCategoryId(e.getCategoryId());
                            oc.setCategoryCode(e.getCategoryCode());
                            oc.setCategoryName(e.getCategoryName());
                            return oc;
                        }).collect(Collectors.toList()));
            }
        }
        List<VendorAiRecommendVO> recommendList = extSupplierClient.aiRecommend(param);

        if (CollectionUtils.isEmpty(recommendList)) { return Collections.emptyList(); }
        Set<Long> vendorId = recommendList.stream().map(e -> e.getVendor().getCompanyId()).collect(Collectors.toSet());
        List<OrgCategory> orgCategoryList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.ORG_CATEGORY).
                in(OrgCategory::getCompanyId, new ArrayList<>(vendorId))
                .in(OrgCategory::getCategoryId, souItemList.stream().map(SouItem::getCategoryId).distinct().collect(Collectors.toList()))
                .in(OrgCategory::getOrgCode, souItemList.stream().map(SouItem::getOrgOuCode).distinct().collect(Collectors.toList()))
                .eq(OrgCategory::getServiceStatus, CategoryStatus.QUALIFIED), OrgCategory.class);
        if (CollectionUtils.isEmpty(orgCategoryList)) {
            return new ArrayList<>();
        }
        //供应商品类关系中 供应商拥有的合格品类Map， key-value key为供应商ID， value为 品类ID_公司编码  集合
        Map<Long, Set<String>> vendorIdOrgCategoryKeyMap = new HashMap<>(15);
        orgCategoryList.stream().filter(e -> CategoryStatus.QUALIFIED.equals(e.getServiceStatus())).forEach(org -> {
            if(!vendorIdOrgCategoryKeyMap.containsKey(org.getCompanyId())) {
                vendorIdOrgCategoryKeyMap.put(org.getCompanyId(), new HashSet<>());
            }
            String key = StringUtils.joinWith(SrmConstant.UNDER_LINE, org.getCategoryId(), org.getOrgCode());
            vendorIdOrgCategoryKeyMap.get(org.getCompanyId()).add(key);
        });

        //物料的品类ID_公司编码 集合
        Set<String> itemKeySet = souItemList.stream().filter(e -> ObjectUtils.allNotNull(e.getCategoryId(), e.getOrgOuCode())).map(e -> StringUtils.joinWith(SrmConstant.UNDER_LINE, e.getCategoryId(), e.getOrgOuCode())).collect(Collectors.toSet());

        //不符合品类关系校验的供应商集合
        Set<Long> unMathchVendorIdSet = new HashSet<>();
        for(Long companyId : vendorIdOrgCategoryKeyMap.keySet()) {
            //匹配物料 品类ID_公司编码 集合
            Set<String> itemKeyMatchSet = new HashSet<>(itemKeySet);
            //移除匹配的集合
            itemKeyMatchSet.removeAll(vendorIdOrgCategoryKeyMap.get(companyId));
            //剩下的无效集合如果不为空，判断为不匹配
            if(CollectionUtils.isNotEmpty(itemKeyMatchSet)) {
                unMathchVendorIdSet.add(companyId);
            }

        }

        unMathchVendorIdSet.stream().forEach(companyId -> {
            vendorIdOrgCategoryKeyMap.remove(companyId);
        });

        if(MapUtils.isEmpty(vendorIdOrgCategoryKeyMap)) {
            return new ArrayList<>();
        }

        Set<Long> vendorIds = new HashSet<>(vendorIdOrgCategoryKeyMap.keySet());

        //先缩小范围
        recommendList = recommendList.stream().filter(e -> vendorIds.contains(e.getVendor().getCompanyId())).collect(Collectors.toList());

        // 2: 查询排除非受限供应商 TODO
        if (Enable.Y.equals(inqSouProject.getExtExcludeOrgLimitVendors())) {
            // 查询存在受限情况的供应商
            List<CompanyInfo> limitedCompanyInfo = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query("CompanyInfo")
                            .in(CompanyInfo::getCompanyId, new ArrayList<>(vendorIds))
                            .and(wrapper -> wrapper
                                    .or(e -> e.eq("focusFlag", Enable.N)) // 是否重点关注,0403 是否重点关注为N才显示
                                    .or(e -> e.eq("positionLimitFlag", Enable.Y)) // 是否单位受限
                                    .or(e -> e.eq("timeLimitFlag", Enable.Y))) // 是否时间受限
                            .select("companyId", "focusFlag", "positionLimitFlag", "timeLimitFlag"))
                    .stream().map(e -> SouObjectXUtil.convertTargetObj(e, CompanyInfo.class)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(limitedCompanyInfo)) {
                boolean isLimitedForAllTag = true;
                if (isLimitedForAllTag) {
                    // 不管具体的受限情况，直接过滤
                    Set<Long> limitedVendorIds = limitedCompanyInfo.stream().map(CompanyInfo::getCompanyId).collect(Collectors.toSet());
                    Set<Long> complementVendorIds = new HashSet<>(com.midea.cloud.common.utils.CollectionUtils.complement(vendorIds, limitedVendorIds));
                    recommendList = recommendList.stream().filter(e -> complementVendorIds.contains(e.getVendor().getCompanyId())).collect(Collectors.toList());
                } else {
                    // 需要进一步查询受限情况，是否满足询比价单所需 TODO
                }
            }
        }
        // 3: 查询供应商绩效 TODO

        // 5: 根据排名前几、是否随机得到最终结果
        if (inqSouProject.getExtVendorPerformanceRank() != null && inqSouProject.getExtVendorPerformanceRank() > 0 && inqSouProject.getExtVendorPerformanceRank() < recommendList.size()) {
            recommendList = new ArrayList<>(recommendList.subList(0, inqSouProject.getExtVendorPerformanceRank()));
        }

        //移除智能推荐历史供应商
        recommendList = removeExistsAutoRecom(projectId, recommendList);

        return recommendList.stream().map(VendorAiRecommendVO::getVendor).collect(Collectors.toList());
    }

    /**
     * 移除智能推荐历史供应商
     * @param projectId
     */
    private List<VendorAiRecommendVO> removeExistsAutoRecom(Long projectId, List<VendorAiRecommendVO> recommendList ) {

        if(CollectionUtils.isEmpty(recommendList)) {
            return recommendList;
        }

        List<Long> vendorIdList = recommendList.stream().map(v -> v.getVendor().getCompanyId()).collect(Collectors.toList());

        LambdaQueryWrapper<ExtSouVendor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouVendor::getProjectId, projectId);
        queryWrapper.in(ExtSouVendor::getVendorId, vendorIdList);

        queryWrapper.select(ExtSouVendor::getVendorId);

        List<ExtSouVendor> vendorList = extSouVendorService.list(queryWrapper);

        if(CollectionUtils.isEmpty(vendorList)) {
            return recommendList;
        }

        List<Long> existsVendorIdList = vendorList.stream().map(v -> v.getVendorId()).collect(Collectors.toList());

        return recommendList.stream().filter(v -> !existsVendorIdList.contains(v.getVendor().getCompanyId())).collect(Collectors.toList());
    }

    /**
     * 查看历史最低价供应商
     */
    @Override
    public List<AiRecommendCompanyInfoVO> getHistoryMinPriceVendors(long projectId) {
        InqSouProject inqSouProject = inqSouProjectDAO.getById(projectId);
        AssertUtils.notNull(inqSouProject, "询价单[{0}]不存在", projectId);
        SouProject souProject = souProjectDAO.getById(projectId);
        if (!SouSourceFromTypeEnum.PURCHASE_REQ.name().equals(souProject.getSourceFromType())) {
            // 非来源于采购申请，没有这个信息
            return Collections.emptyList();
        }
        /*List<SouItem> souItemList = souItemDAO.list(SouItem::getProjectId, projectId);
        if (souItemList.isEmpty()) { return Collections.emptyList(); }

        // 1: 根据采购申请单号，查询需求池最低价供应商
        Set<String> reqHeadNums = new HashSet<>(16); {
            souItemList.forEach(souItem -> {
                if (souItem.getSourceFromLineNo() != null) {
                    reqHeadNums.addAll(Arrays.asList(souItem.getSourceFromNo().split(",")));
                }
            });
        }*/
        Map<Long/* vendorId */, CompanyInfo> vendors; {
            Set<String> vendorCodes = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementLine")
//                            .in(RequirementLine::getRequirementHeadNum, new ArrayList<>(reqHeadNums))
                            .eq("extInqSouNo", souProject.getSouNo())
                            .select("extHistoryVendorCode1"))
                    .stream().map(e -> e.getString("extHistoryVendorCode1")).collect(Collectors.toSet());
            if (vendorCodes.isEmpty()) { return Collections.emptyList(); }
            //noinspection unchecked
            vendors = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query("CompanyInfo")
                    .in(CompanyInfo::getCompanyCode, new ArrayList<>(vendorCodes))
                    //.select(CompanyInfo::getCompanyId)
                    ).stream().map(e -> SouObjectXUtil.convertTargetObj(e, CompanyInfo.class))
                    .collect(Collectors.toMap(CompanyInfo::getCompanyId, Function.identity()));
        }
        // 2: 查询询价单物料需求的业务实体信息
        Set<String> needMatchOrgOus = souItemDAO.list(SouItem::getProjectId, projectId).stream().map(SouItem::getOrgOuCode).collect(Collectors.toSet());
        // 3: 查询供应商组织关系
        //noinspection unchecked
        Map<Long/* vendorId */, Set<String/* orgCode */>> vendorOrgRelationMap = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query("OrgCategory")
                .in(OrgCategory::getCompanyId, new ArrayList<>(vendors.keySet()))
                .select(OrgCategory::getCompanyId, OrgCategory::getOrgCode))
                .stream().collect(Collectors.groupingBy(e -> e.getLong("companyId"), Collectors.mapping(e -> e.getString("orgCode"), Collectors.toSet())));
        // 4: 筛选符合业务实体的供应商
        List<CompanyInfo> availableVendors = new ArrayList<>(vendors.size());
        vendors.forEach((vendorId, companyInfo) -> {
            Set<String/* orgCode */> orgCodes = vendorOrgRelationMap.get(vendorId);
            if (CollectionUtils.isNotEmpty(orgCodes)) {
                boolean isMatched = true;
                for (String matchOrgOu : needMatchOrgOus) {
                    if (!orgCodes.contains(matchOrgOu)) {
                        isMatched = false;
                        break;
                    }
                }
                if (isMatched) {
                    /*if (vendorId != null) {
                        CompanyInfo ci = qlOpenClient.read(ContextPath.SUP, "CompanyInfo", vendorId, CompanyInfo.class);
                        availableVendors.add(ci);
                    } else {
                        availableVendors.add(companyInfo);
                    }*/
                    log.info(vendorId + "===供应商信息===" + JSONObject.toJSONString(companyInfo));
                    availableVendors.add(companyInfo);
                }
            }
        });

        return SouObjectXUtil.convertList(availableVendors, AiRecommendCompanyInfoVO.class);
    }

    /**
     * 查看被删除的邀请供应商
     */
    @Override
    public List<ExtPJInqSouVendorDel> queryVendorDel(ExtPjInqSouVendorQueryDTO queryParam) {
        queryParam.formatParams();
        // 1: 查询数据
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        return extPjInqSouVendorDelDao.lambdaQuery()
                .eq(ExtPJInqSouVendorDel::getProjectId, queryParam.getProjectId())
                .like(queryParam.getVendorCode() != null, ExtPJInqSouVendorDel::getVendorCode, queryParam.getVendorCode())
                .like(queryParam.getVendorName() != null, ExtPJInqSouVendorDel::getVendorName, queryParam.getVendorName())
                .orderByDesc(ExtPJInqSouVendorDel::getCreationDate)
                .list();
    }

    /**
     * 询比价物料明细报表查询
     */
    @Override
    public List<ExtInqSouItemQueryVO> querySouItems(ExtInqSouItemQueryDTO queryParam) {
        queryParam.formatParams();
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }

        List<ExtInqSouItemQueryVO> itemList = extPjInqSouOrderMapper.querySouItems(queryParam);
        querySouItemsOrderCount(itemList);
        return itemList;
    }

    /**
     * 查询报价次数
     * @param dataList
     */
    private void querySouItemsOrderCount(List<ExtInqSouItemQueryVO> dataList) {
        if(CollectionUtils.isEmpty(dataList)) {
            return;
        }
        Map<String, Object> params = new HashMap<>(15);
        List<Map<String, Object>> souItemIdRoundList = new ArrayList<>();
        dataList.forEach(data -> {
            Map<String, Object> param = new HashMap<>(15);
            param.put("souItemId", data.getSouItemId());
            param.put("round", data.getCurrentRound());
            souItemIdRoundList.add(param);
        });
        params.put("souItemIdRoundList", souItemIdRoundList);

        List<ExtInqSouItemQueryVO> orderCountList = extPjInqSouOrderMapper.querySouItemsOrderCount(params);
        Map<String, ExtInqSouItemQueryVO> orderCountMap = orderCountList.stream().collect(Collectors.toMap(
                k -> StringUtils.joinWith(SrmConstant.UNDER_LINE, k.getSouItemId(), k.getRound()), Function.identity(), (k1, k2) -> k2
        ));

        dataList.stream().forEach(data -> {
            String key = StringUtils.joinWith(SrmConstant.UNDER_LINE, data.getSouItemId(), data.getCurrentRound());
            ExtInqSouItemQueryVO vo = orderCountMap.getOrDefault(key, new ExtInqSouItemQueryVO());
            data.setOrderCount(vo.getOrderCount());
        });
    }

}
