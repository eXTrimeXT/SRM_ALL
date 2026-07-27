package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.SouReqHeadStatusEnum;
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
@Service(value = "REQ_CANCLE_SOU")
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class ReuirementCancleServiceSouImpl extends RequirementCancleFlowServiceImpl implements RequirementCancleService {
    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private QlService qlService;

    @Override
    public Object cancle(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache) {
        List<Record> extPrSouRequirementHeadList = (List<Record>) localCache.get(CancleCacheConstants.EXT_CANCLE_REQUIRMENTHEAD_LIST);
        if(CollectionUtils.isEmpty(extPrSouRequirementHeadList)) {
            return null;
        }

        List<RecordDTO> reqHeadList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(MqlType.SOU_REQ_HEAD_BUYER).in(SouReqHead::getReqHeadId, extPrSouRequirementHeadList.stream().map(r -> r.get(ExtPrSouRequirementHead::getSouReqId)).distinct().collect(Collectors.toList())));
        //没有需求单据
        if(CollectionUtils.isEmpty(reqHeadList)) {
            return null;
        }

        Map<Long, List<Record>> reqGroup = extPrSouRequirementHeadList.stream().collect(Collectors.groupingBy(r -> r.get(ExtPrSouRequirementHead::getSouReqId)));

        for(RecordDTO reqHead : reqHeadList) {

            List<Record> cancleRequirementList = reqGroup.get(reqHead.get(SouReqHead::getReqHeadId));

            //判断是否合并招标
            if(ObjectUtils.defaultIfNull(reqHead.get(SouReqHead::getRequirementHeadNoList), "").contains(SrmConstant.SIG_3)) {
                //合并招标
                cancleForMerge(reqHead, cancleRequirementList, requirementHeadNumMap, localCache);
                continue;
            }

            if(SouReqHeadStatusEnum.APPROVING.getCode().equals(reqHead.get(SouReqHead::getStatus))) {
                //取消审批流程
                super.cancleFlow(reqHead.get(SouReqHead::getReqHeadId), SrmConstant.FLOW_CODE_SOU_REQ_HEAD);
            }

            //废弃
            reqHead.put(SouReqHead::getStatus, SouReqHeadStatusEnum.ABANDON.getCode());
            reqHead.put(SouReqHead::getReasonDesc, RequirementCancleUtils.cancleReason(reqHead.get(SouReqHead::getReasonDesc), reqHead.get(SouReqHead::getRequirementHeadNo)));
        }

        //更新状态
        qlOpenClient.update(ContextPath.SOU, MqlType.SOU_REQ_HEAD_BUYER, reqHeadList);
        return null;
    }


    private void cancleForMerge(RecordDTO reqHead, List<Record> cancleRequirementList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache) {

        String reqHeadIdListStr = ObjectUtils.defaultIfNull(reqHead.get(SouReqHead::getRequirementHeadIdList), "");
        String reqHeadNoListStr = ObjectUtils.defaultIfNull(reqHead.get(SouReqHead::getRequirementHeadNoList), "");

        List<String> reqHeadIdList = new ArrayList<>(Arrays.asList(reqHeadIdListStr.split(SrmConstant.SIG_3)));
        List<String> reqHeadNoList = new ArrayList<>(Arrays.asList(reqHeadNoListStr.split(SrmConstant.SIG_3)));

        List<String> cancleHeadIdList = new ArrayList<>(cancleRequirementList.size());
        List<String> cancleHeadNoList = new ArrayList<>(cancleRequirementList.size());
        cancleRequirementList.stream().forEach(r -> {
            Long requirementHeadId = r.get(ExtPrSouRequirementHead::getRequirementHeadId);
            cancleHeadIdList.add(requirementHeadId.toString());
            cancleHeadNoList.add(requirementHeadNumMap.get(requirementHeadId));
        });

        /**移除取消的申请单信息*/
        reqHeadIdList.removeAll(cancleHeadIdList);
        reqHeadNoList.removeAll(cancleHeadNoList);

        if(CollectionUtils.isNotEmpty(reqHeadIdList)) {
            List<Record> requirmentList = qlService.queryByWrapper(QlWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD).in(RequirementHead::getRequirementHeadId, reqHeadIdList), Record.class);
            Map<Long, Record> requirmentMap = requirmentList.stream().collect(Collectors.toMap(k -> k.get(RequirementHead::getRequirementHeadId), Function.identity(), (k1, k2)->k2));
            Map<String, Record> requirmentNumMap = requirmentList.stream().collect(Collectors.toMap(k -> k.get(RequirementHead::getRequirementHeadNum), Function.identity(), (k1, k2)->k2));

            List<ExtPrSouRequirementHead> extRequirementList = qlService.queryByWrapper(QlWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).in(ExtPrSouRequirementHead::getRequirementHeadId, reqHeadIdList), ExtPrSouRequirementHead.class);
            Map<Long, ExtPrSouRequirementHead> extRequirementMap = extRequirementList.stream().collect(Collectors.toMap(k -> k.getRequirementHeadId(), Function.identity(), (k1, k2)->k2));

            /**按ID缓存*/
            Map<Long, Record> requirmentCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_REQUIRMENTHEAD_MAP, new HashMap<>(15));
            requirmentCache.putAll(requirmentMap);
            RequirementCancleUtils.cacheKeyValue(localCache, CancleCacheConstants.MERGE_REQUIRMENTHEAD_MAP, requirmentCache);

            /**按申请单号缓存*/
            Map<String, Record> requirmentNumCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_REQUIRMENTHEAD_NUM_MAP, new HashMap<>(15));
            requirmentNumCache.putAll(requirmentNumMap);
            RequirementCancleUtils.cacheKeyValue(localCache, CancleCacheConstants.MERGE_REQUIRMENTHEAD_NUM_MAP, requirmentNumCache);


            /**按ID缓存*/
            Map<Long, ExtPrSouRequirementHead> extRequirmentCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_EXT_REQUIRMENTHEAD_MAP, new HashMap<>(15));
            extRequirmentCache.putAll(extRequirementMap);
            RequirementCancleUtils.cacheKeyValue(localCache, CancleCacheConstants.MERGE_EXT_REQUIRMENTHEAD_MAP, extRequirmentCache);

            //取招标资料提交，累计预算金额
            List<BidDataSubmit> dataSubmitList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(MqlType.SUBMIT_BUYER).in(BidDataSubmit::getRequirementHeadNum, reqHeadNoList), BidDataSubmit.class);

            /**按申请单号缓存*/
            Map<String, BidDataSubmit> dataSubmitMap = dataSubmitList.stream().collect(Collectors.toMap(k -> k.getRequirementHeadNum(), Function.identity(), (k1, k2)->k2));
            Map<String, BidDataSubmit> dataSubmitCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_DATASUBMIT_MAP, new HashMap<>(15));
            dataSubmitCache.putAll(dataSubmitMap);
            RequirementCancleUtils.cacheKeyValue(localCache, CancleCacheConstants.MERGE_DATASUBMIT_MAP, dataSubmitCache);

        }


        //拟定状态直接废弃，并释放
        if(SouReqHeadStatusEnum.DRAFT.getCode().equals(reqHead.get(SouReqHead::getStatus))) {
            //废弃
            reqHead.put(SouReqHead::getStatus, SouReqHeadStatusEnum.ABANDON.getCode());
            reqHead.put(SouReqHead::getReasonDesc, RequirementCancleUtils.cancleReason(reqHead.get(SouReqHead::getReasonDesc), cancleHeadNoList.stream().collect(Collectors.joining(SrmConstant.SIG_3))));

            if(CollectionUtils.isNotEmpty(reqHeadIdList)) {
                //释放
                qlService.updateByWrapper(QlWrappers.update(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).set(ExtPrSouRequirementHead::getHasCreateSouReq, Enable.N.name()).in(ExtPrSouRequirementHead::getRequirementHeadId, reqHeadIdList));
            }

           return;
        }

        if(CollectionUtils.isNotEmpty(reqHeadIdList)) {
            /**重新设置采购申请单号*/
            reqHead.put(SouReqHead::getRequirementHeadIdList, reqHeadIdList.stream().collect(Collectors.joining(SrmConstant.SIG_3)));
            reqHead.put(SouReqHead::getRequirementHeadNoList, reqHeadNoList.stream().collect(Collectors.joining(SrmConstant.SIG_3)));

            Map<String, BidDataSubmit> dataSubmitCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_DATASUBMIT_MAP, new HashMap<>(15));
            List<BidDataSubmit> dataSubmitList = RequirementCancleUtils.dataSubmitSortDesc(dataSubmitCache, reqHeadNoList);
            if(CollectionUtils.isNotEmpty(dataSubmitList)) {
                reqHead.put(SouReqHead::getTotalAmountByTenKilo, dataSubmitList.stream().map(BidDataSubmit::getTotalBudget).reduce(BigDecimal.ZERO, (a, b) -> BigDecimalUtil.add(a, b)));

                Map<String, Record> requirmentNumCache = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.MERGE_REQUIRMENTHEAD_NUM_MAP, new HashMap<>(15));

                for(BidDataSubmit dataSubmit : dataSubmitList) {
                    Record requirementHead = requirmentNumCache.get(dataSubmit.getRequirementHeadNum());
                    if(ObjectUtils.allNotNull(requirementHead)) {
                        reqHead.put(SouReqHead::getRequirementHeadId, requirementHead.get(RequirementHead::getRequirementHeadId));
                        reqHead.put(SouReqHead::getRequirementHeadNo, requirementHead.get(RequirementHead::getRequirementHeadNum));
                        break;
                    }
                }
            }

            reqHead.put(SouReqHead::getPartCancle, YesOrNo.YES.getValue());
            reqHead.put(SouReqHead::getReasonDesc, RequirementCancleUtils.cancleReason(reqHead.get(SouReqHead::getReasonDesc), cancleHeadNoList.stream().collect(Collectors.joining(SrmConstant.SIG_3))));
        } else {
            if(SouReqHeadStatusEnum.APPROVING.getCode().equals(reqHead.get(SouReqHead::getStatus))) {
                //取消审批流程
                super.cancleFlow(reqHead.get(SouReqHead::getReqHeadId), SrmConstant.FLOW_CODE_SOU_REQ_HEAD);
            }
            //废弃
            reqHead.put(SouReqHead::getStatus, SouReqHeadStatusEnum.ABANDON.getCode());
            reqHead.put(SouReqHead::getReasonDesc, RequirementCancleUtils.cancleReason(reqHead.get(SouReqHead::getReasonDesc), cancleHeadNoList.stream().collect(Collectors.joining(SrmConstant.SIG_3))));

        }

    }
}
