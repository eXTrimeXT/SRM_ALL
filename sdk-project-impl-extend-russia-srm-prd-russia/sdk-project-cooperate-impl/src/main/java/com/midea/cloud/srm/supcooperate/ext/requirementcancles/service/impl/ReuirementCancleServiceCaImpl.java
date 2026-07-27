package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.ExtInspectStatusEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
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
@Service(value = "REQ_CANCLE_CA")
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class ReuirementCancleServiceCaImpl extends RequirementCancleFlowServiceImpl implements RequirementCancleService {
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

        List<RecordDTO> recordDTOList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(TypeEnum.Ca.getCode()).in(CaDTO::getProjectId, souProjectList.stream().map(r -> r.get(ExtSouProject::getProjectId)).collect(Collectors.toList())));
        if(CollectionUtils.isEmpty(recordDTOList)) {
            return null;
        }

        List<RecordDTO> updateList = new ArrayList<>();
        //取消逻辑
        for (RecordDTO ca : recordDTOList) {
            RecordDTO souProject = souProjectMap.get(ca.get(CaDTO::getProjectId));
            ca.put(CaDTO::getDiscardDescription, souProject.get(ExtSouProject::getCancelReason));
            if(SouBiddingProStatusEnum.ABANDON.getCode().equals(souProject.get(ExtSouProject::getProjectStatus))) {
                //定标申请
                if(CaTypeEnum.APPLY.getCode().equals(ca.get(CaDTO::getType)) && CaStatusEnum.APPROVING.getCode().equals(ca.get(CaDTO::getStatus))) {
                    super.cancleFlow(ca.get(CaDTO::getCaId), SrmConstant.FLOW_CODE_SOU_CA);
                }

                //废标申请
                if(CaTypeEnum.DESTORY.getCode().equals(ca.get(CaDTO::getType)) && CaStatusEnum.APPROVING.getCode().equals(ca.get(CaDTO::getStatus))) {
                    super.cancleFlow(ca.get(CaDTO::getCaId), SrmConstant.FLOW_CODE_SOU_DCA);
                }

                ca.put(CaDTO::getStatus, CaStatusEnum.ABANDON.getCode());
            } else {
                ca.put(CaDTO::getExtBudget, souProject.get(ExtSouProject::getExtBudget));
            }

            updateList.add(ca);
        }
        if(CollectionUtils.isNotEmpty(updateList)) {
            qlOpenClient.update(ContextPath.SOU, TypeEnum.Ca.getCode(), updateList);
        }
        return null;
    }

}
