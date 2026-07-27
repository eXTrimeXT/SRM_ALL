package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.enums.ExtInspectStatusEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.BidDataSubmitStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.constants.CancleCacheConstants;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.RequirementCancleService;
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
@Service(value = "REQ_CANCLE_INSPECT")
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class ReuirementCancleServiceInspectImpl extends RequirementCancleFlowServiceImpl implements RequirementCancleService {
    @Autowired
    private QlOpenClient qlOpenClient;


    @Override
    public Object cancle(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache) {
        //读取本地缓存
        List<RecordDTO> souProjectList = (List<RecordDTO>) localCache.get(CancleCacheConstants.SOU_PROJECT_LIST);
        if(CollectionUtils.isEmpty(souProjectList)) {
            return null;
        }

        Map<Long, RecordDTO> souProjectMap = souProjectList.stream().collect(Collectors.toMap(s -> s.get(ExtSouProject::getProjectId), Function.identity(), (k1, k2)-> k2));

        List<RecordDTO> recordDTOList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(MqlType.INSPECT).in("bidingId", souProjectList.stream().map(r -> r.get(ExtSouProject::getProjectId)).collect(Collectors.toList())));
        if(CollectionUtils.isEmpty(recordDTOList)) {
            return null;
        }
        List<RecordDTO> updateList = new ArrayList<>();
        //取消逻辑
        for (RecordDTO inspect : recordDTOList) {

            RecordDTO souProject = souProjectMap.get(inspect.get("bidingId"));
            if(SouBiddingProStatusEnum.ABANDON.getCode().equals(souProject.get(ExtSouProject::getProjectStatus))) {
                //考察申请
                if(ExtInspectStatusEnum.APPLY_APPROVING.getCode().equals(inspect.getString("inspectStatus"))) {
                    super.cancleFlow(inspect.getLong("inspectId"), SrmConstant.FLOW_CODE_INSPECT_APPLY);
                }
                //考察报告
                if(ExtInspectStatusEnum.REPORT_APPROVING.getCode().equals(inspect.getString("inspectStatus"))) {
                    super.cancleFlow(inspect.getLong("inspectId"), SrmConstant.FLOW_CODE_INSPECT_REPORT);
                }
                inspect.put("inspectStatus", ExtInspectStatusEnum.ABANDON.getCode());
                inspect.put("reasonDesc", SrmConstant.PR_ABANDON_DEAULT_REASON);
                updateList.add(inspect);
            }
        }
        if(CollectionUtils.isNotEmpty(updateList)) {
            qlOpenClient.update(ContextPath.SOU, MqlType.INSPECT, updateList);
        }
        return null;
    }

}
