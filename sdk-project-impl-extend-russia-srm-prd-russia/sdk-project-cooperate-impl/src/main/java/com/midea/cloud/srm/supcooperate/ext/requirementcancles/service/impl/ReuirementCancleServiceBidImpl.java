package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.SouExtClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.SouRecommvendorStatusEnum;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.constants.CancleCacheConstants;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.RequirementCancleService;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.utils.RequirementCancleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Service(value = "REQ_CANCLE_BID")
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class ReuirementCancleServiceBidImpl extends RequirementCancleFlowServiceImpl implements RequirementCancleService {
    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private SouExtClient souExtClient;


    @Override
    public Object cancle(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache) {

        List<String> requirementHeadNumList = new ArrayList<>(requirementHeadNumMap.values());
        List<RecordDTO> recordDTOList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(MqlType.NPM_SOU_DEMAND).in(ExtSouDemand::getApplicantNo, requirementHeadNumList));
        if(CollectionUtils.isEmpty(recordDTOList)) {
            return null;
        }

        List<RecordDTO> souProjectList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(MqlType.NPM_SOU_PROJECT).in(ExtSouProject::getProjectId, recordDTOList.stream().map(r -> r.get(ExtSouDemand::getProjectId)).distinct().collect(Collectors.toList())).eq(ExtSouProject::getSouType, SouTypeEnum.bid.name()));
        if(CollectionUtils.isEmpty(souProjectList)) {
            return null;
        }

        List<RecordDTO> souDemandList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(MqlType.NPM_SOU_DEMAND).in(ExtSouDemand::getProjectId, souProjectList.stream().map(r -> r.get(RecommvendorProjectDto::getProjectId)).collect(Collectors.toList())));
        Map<Long, List<RecordDTO>> souProjectDemandMap = souDemandList.stream().collect(Collectors.groupingBy(r -> r.get(ExtSouDemand::getProjectId)));


        List<RecordDTO> updateSouProjectList = new ArrayList<>();
        List<RecordDTO> updateDemandList = new ArrayList<>();
        List<ExtPrSouRequirementHead> updateRequirementList = new ArrayList<>();

        //取消逻辑
        for (RecordDTO souProject : souProjectList) {

            List<RecordDTO> demandList = souProjectDemandMap.getOrDefault(souProject.get(RecommvendorProjectDto::getProjectId), new ArrayList<>());
            Map<String, RecordDTO> demandMap = demandList.stream().collect(Collectors.toMap(k -> k.get(ExtSouDemand::getApplicantNo), Function.identity(), (k1, k2)->k2));

            //取消的申请单号
            List<String> cancleRequirementHeadNumList = requirementHeadNumList.stream().filter(s -> demandMap.containsKey(s)).collect(Collectors.toList());

            cancleRequirementHeadNumList.stream().forEach(s -> {
                RecordDTO demand = demandMap.get(s);
                demand.put(ExtSouDemand::getStatus, SrmConstant.NUM_ONE);
                updateDemandList.add(demand);
            });

            List<RecordDTO> unCancleDemandList = new ArrayList<>(16);

            demandList.stream().forEach(demand -> {
                if(!cancleRequirementHeadNumList.contains(demand.get(ExtSouDemand::getApplicantNo)) && Integer.compare(SrmConstant.NUM_ZERO, ObjectUtils.defaultIfNull(demand.get(ExtSouDemand::getStatus), SrmConstant.NUM_ZERO)) == 0) {
                    unCancleDemandList.add(demand);
                }
            });

            //合并招标
            if(Integer.compare(demandList.size(), 1) != 0) {
                List<String> unCancleRequirementHeadNumList = unCancleDemandList.stream().map(r -> r.get(ExtSouDemand::getApplicantNo)).distinct().collect(Collectors.toList());
                souProject.put(RecommvendorProjectDto::getCancelReason, RequirementCancleUtils.cancleReason(souProject.get(RecommvendorProjectDto::getCancelReason), cancleRequirementHeadNumList.stream().collect(Collectors.joining(SrmConstant.SIG_3))));

                if(CollectionUtils.isNotEmpty(unCancleDemandList)) {
                    //缓存数据
                    cacheRequirementInfo(unCancleRequirementHeadNumList, localCache);

                    //查询采购金额
                    Map<String, BidDataSubmit> dataSubmitCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_DATASUBMIT_MAP, new HashMap<>(15));
                    List<BidDataSubmit> dataSubmitList = RequirementCancleUtils.dataSubmitSortDesc(dataSubmitCache, unCancleRequirementHeadNumList);
                    if(CollectionUtils.isNotEmpty(dataSubmitList)) {
                        souProject.put(RecommvendorProjectDto::getExtBudget, dataSubmitList.stream().map(BidDataSubmit::getTotalBudget).reduce(BigDecimal.ZERO, (a, b) -> BigDecimalUtil.add(a, b)));
                    }

                    //直接废弃he释放
                    if(SouBiddingProStatusEnum.DRAW_UP.getCode().equals(souProject.get(ExtSouProject::getProjectStatus))) {
                        souProject.put(ExtSouProject::getCreateApprovalStatus, SouApprovalStatusEnum.ABANDONED.name());
                        souProject.put(ExtSouProject::getProjectStatus, SouBiddingProStatusEnum.ABANDON.getCode());
                        Map<Long, ExtPrSouRequirementHead> extRequirmentCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_EXT_REQUIRMENTHEAD_MAP, new HashMap<>(15));
                        Map<String, Record> requirmentNumCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_REQUIRMENTHEAD_NUM_MAP, new HashMap<>(15));

                        //释放
                        unCancleRequirementHeadNumList.stream().filter(s -> requirmentNumCache.containsKey(s)).forEach(s -> {
                            Record requirementHead = requirmentNumCache.get(s);
                            ExtPrSouRequirementHead extPrSouRequirementHead = extRequirmentCache.get(requirementHead.get(RequirementHead::getRequirementHeadId));
                            updateRequirementList.add(extPrSouRequirementHead);
                        });
                    }
                } else {
                    souProject.put(ExtSouProject::getCreateApprovalStatus, SouApprovalStatusEnum.ABANDONED.name());
                    souProject.put(ExtSouProject::getProjectStatus, SouBiddingProStatusEnum.ABANDON.getCode());
                }

                updateSouProjectList.add(souProject);
            } else {
                souProject.put(ExtSouProject::getCreateApprovalStatus, SouApprovalStatusEnum.ABANDONED.name());
                souProject.put(RecommvendorProjectDto::getCancelReason, RequirementCancleUtils.cancleReason(souProject.get(RecommvendorProjectDto::getCancelReason), cancleRequirementHeadNumList.stream().collect(Collectors.joining(SrmConstant.SIG_3))));
                souProject.put(ExtSouProject::getProjectStatus, SouBiddingProStatusEnum.ABANDON.getCode());
                updateSouProjectList.add(souProject);
            }
        }

        if(CollectionUtils.isNotEmpty(updateDemandList)) {
            qlOpenClient.update(ContextPath.SOU, MqlType.NPM_SOU_DEMAND, updateDemandList);
        }

        if(CollectionUtils.isNotEmpty(updateSouProjectList)) {
            qlOpenClient.update(ContextPath.SOU, MqlType.NPM_SOU_PROJECT, updateSouProjectList);

            //招标单记录本地缓存
            localCache.put(CancleCacheConstants.SOU_PROJECT_LIST, updateSouProjectList);

            //取消待办任务
            List<Long> projectIdList = updateSouProjectList.stream().filter(souProject -> SouBiddingProStatusEnum.ABANDON.getCode().equals(souProject.get(ExtSouProject::getProjectStatus))).map(r -> r.get(ExtSouProject::getProjectId)).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(projectIdList)) {
                souExtClient.cancleBid(projectIdList);
            }
        }

        if(CollectionUtils.isNotEmpty(updateRequirementList)) {
            qlService.updateByWrapper(QlWrappers.update(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).set(ExtPrSouRequirementHead::getHasCreateSou, Enable.N.name()).in(ExtPrSouRequirementHead::getRequirementHeadId, updateRequirementList.stream().map(r->r.getRequirementHeadId()).collect(Collectors.toList())));
        }

        return null;
    }

    /**
     * 缓存采购信息
     * @param requirementHeadNumList
     * @param localCache
     */
    private void cacheRequirementInfo(List<String> requirementHeadNumList, HashMap<String, Object> localCache) {
        if(CollectionUtils.isEmpty(requirementHeadNumList)) {
            return;
        }
        /**按申请单号缓存*/
        Map<String, Record> requirmentNumCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_REQUIRMENTHEAD_NUM_MAP, new HashMap<>(15));

        List<String> requirementHeadNumCacheList = requirementHeadNumList.stream().filter(s -> !requirmentNumCache.containsKey(s)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(requirementHeadNumCacheList)) {
            return;
        }


        List<Record> requirmentList = qlService.queryByWrapper(QlWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD).in(RequirementHead::getRequirementHeadNum, requirementHeadNumCacheList), Record.class);
        Map<Long, Record> requirmentMap = requirmentList.stream().collect(Collectors.toMap(k -> k.get(RequirementHead::getRequirementHeadId), Function.identity(), (k1, k2)->k2));
        Map<String, Record> requirmentNumMap = requirmentList.stream().collect(Collectors.toMap(k -> k.get(RequirementHead::getRequirementHeadNum), Function.identity(), (k1, k2)->k2));

        List<ExtPrSouRequirementHead> extRequirementList = qlService.queryByWrapper(QlWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).in(ExtPrSouRequirementHead::getRequirementHeadId, new ArrayList<>(requirmentMap.keySet())), ExtPrSouRequirementHead.class);
        Map<Long, ExtPrSouRequirementHead> extRequirementMap = extRequirementList.stream().collect(Collectors.toMap(k -> k.getRequirementHeadId(), Function.identity(), (k1, k2)->k2));

        /**按ID缓存*/
        Map<Long, Record> requirmentCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_REQUIRMENTHEAD_MAP, new HashMap<>(15));
        requirmentCache.putAll(requirmentMap);
        RequirementCancleUtils.cacheKeyValue(localCache, CancleCacheConstants.MERGE_REQUIRMENTHEAD_MAP, requirmentCache);

        requirmentNumCache.putAll(requirmentNumMap);
        RequirementCancleUtils.cacheKeyValue(localCache, CancleCacheConstants.MERGE_REQUIRMENTHEAD_NUM_MAP, requirmentNumCache);


        /**按ID缓存*/
        Map<Long, ExtPrSouRequirementHead> extRequirmentCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_EXT_REQUIRMENTHEAD_MAP, new HashMap<>(15));
        extRequirmentCache.putAll(extRequirementMap);
        RequirementCancleUtils.cacheKeyValue(localCache, CancleCacheConstants.MERGE_EXT_REQUIRMENTHEAD_MAP, extRequirmentCache);

        //取招标资料提交，累计预算金额
        List<BidDataSubmit> dataSubmitList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(MqlType.SUBMIT_BUYER).in(BidDataSubmit::getRequirementHeadNum, requirementHeadNumCacheList), BidDataSubmit.class);

        /**按申请单号缓存*/
        Map<String, BidDataSubmit> dataSubmitMap = dataSubmitList.stream().collect(Collectors.toMap(k -> k.getRequirementHeadNum(), Function.identity(), (k1, k2)->k2));
        Map<String, BidDataSubmit> dataSubmitCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_DATASUBMIT_MAP, new HashMap<>(15));
        dataSubmitCache.putAll(dataSubmitMap);
        RequirementCancleUtils.cacheKeyValue(localCache, CancleCacheConstants.MERGE_DATASUBMIT_MAP, dataSubmitCache);
    }

}
