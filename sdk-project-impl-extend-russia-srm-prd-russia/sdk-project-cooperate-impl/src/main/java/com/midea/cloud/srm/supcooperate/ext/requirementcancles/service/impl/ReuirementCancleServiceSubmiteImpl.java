package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.BidDataSubmitStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.SouReqHeadStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.RequirementCancleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Service(value = "REQ_CANCLE_SUBMITE")
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class ReuirementCancleServiceSubmiteImpl extends RequirementCancleFlowServiceImpl implements RequirementCancleService {
    @Autowired
    private QlOpenClient qlOpenClient;


    @Override
    public Object cancle(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache) {

        List<String> requirementHeadNumList = new ArrayList<>(requirementHeadNumMap.values());

        List<RecordDTO> recordDTOList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(MqlType.SUBMIT_BUYER).in(BidDataSubmit::getRequirementHeadNum, requirementHeadNumList));
        if(CollectionUtils.isEmpty(recordDTOList)) {
            return null;
        }
        //取消逻辑
        for (RecordDTO dataSubmite : recordDTOList) {
            if(BidDataSubmitStatusEnum.APPROVING.name().equals(dataSubmite.get(BidDataSubmit::getStatus))) {
                super.cancleFlow(dataSubmite.get(BidDataSubmit::getDataSubmitId), SrmConstant.FLOW_CODE_BID_DATA_SUBMIT);
            }
            dataSubmite.put(BidDataSubmit::getStatus, BidDataSubmitStatusEnum.ABANDON.name());
            dataSubmite.put(BidDataSubmit::getReasonDesc, SrmConstant.PR_ABANDON_DEAULT_REASON);
        }
        qlOpenClient.update(ContextPath.SOU, MqlType.SUBMIT_BUYER, recordDTOList);
        return null;
    }

}
