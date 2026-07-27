package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.enums.BidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.constants.CancleCacheConstants;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.RequirementCancleService;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.utils.RequirementCancleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Service(value = "REQ_CANCLE_NOTICE")
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class ReuirementCancleServiceNoticeImpl extends RequirementCancleFlowServiceImpl implements RequirementCancleService {
    @Autowired
    private QlOpenClient qlOpenClient;


    @Override
    public Object cancle(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache) {
        //读取本地缓存
        List<RecordDTO> souProjectList = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.SOU_PROJECT_LIST, new ArrayList<>(16));
        if(CollectionUtils.isEmpty(souProjectList)) {
            return null;
        }

        Map<Long, RecordDTO> souProjectMap = souProjectList.stream().collect(Collectors.toMap(s -> s.get(ExtSouProject::getProjectId), Function.identity(), (k1, k2)-> k2));

        List<RecordDTO> recordDTOList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(TypeEnum.BidNotice.getCode()).in(CaDTO::getProjectId, souProjectList.stream().map(r -> r.get(ExtSouProject::getProjectId)).collect(Collectors.toList())));
        if(CollectionUtils.isEmpty(recordDTOList)) {
            return null;
        }

        List<RecordDTO> updateList = new ArrayList<>();
        //取消逻辑
        for (RecordDTO notice : recordDTOList) {
            RecordDTO souProject = souProjectMap.get(notice.get(BidNoticeDTO::getProjectId));
            if(SouBiddingProStatusEnum.ABANDON.getCode().equals(souProject.get(ExtSouProject::getProjectStatus))) {
                //中/落标申请
                if(CaTypeEnum.APPLY.getCode().equals(notice.get(BidNoticeDTO::getType)) && BidNoticeStatusEnum.APPROVING.getCode().equals(notice.get(BidNoticeDTO::getStatus))) {
                    super.cancleFlow(notice.get(BidNoticeDTO::getBidNoticeId), SrmConstant.FLOW_CODE_SOU_TN);
                }

                //中/落标废弃
                if(CaTypeEnum.DESTORY.getCode().equals(notice.get(BidNoticeDTO::getType)) && BidNoticeStatusEnum.APPROVING.getCode().equals(notice.get(BidNoticeDTO::getStatus))) {
                    super.cancleFlow(notice.get(BidNoticeDTO::getBidNoticeId), SrmConstant.FLOW_CODE_SOU_ATN);
                }

                notice.put(BidNoticeDTO::getStatus, BidNoticeStatusEnum.ABANDON.getCode());
            }
            notice.put(BidNoticeDTO::getDiscardReason, souProject.get(ExtSouProject::getCancelReason));
            updateList.add(notice);
        }
        if(CollectionUtils.isNotEmpty(updateList)) {
            qlOpenClient.update(ContextPath.SOU, TypeEnum.BidNotice.getCode(), updateList);
        }
        return null;
    }

}
