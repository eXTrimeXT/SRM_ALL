package com.midea.cloud.srm.sou.bid.earlywarnings.service.impl;

import com.midea.cloud.srm.model.extapi.coop.pr.requirement.entity.ExtPrRequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.enums.ExtPrRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.bid.earlywarnings.service.SouBidEarlyWarningService;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm 招标资料提交-预警
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Service
public class SouBidEarlyWarningServiceDataSubmitImpl extends SouBidEarlyWarningServiceCommonImpl implements SouBidEarlyWarningService {
    @Autowired
    private QlOpenClient qlOpenClient;

    @Override
    public String doWarning() {
        log.info("SouBidEarlyWarningService 预警招标资料提交开始");
        Map<String, Object> params = super.getQueryParams();
        List<RecordDTO> dataList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).in(ExtPrSouRequirementHead::getSendSouProfileEndDate, (List<String>)params.get(WARNING_TIME_LIST)));
        if(CollectionUtils.isNotEmpty(dataList)) {
            dataList = dataList.stream().filter(r -> !RequirementApproveStatus.APPROVED.name().equals(r.get(ExtPrSouRequirementHead::getSendSouProfileStatus))).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(dataList)) {
            log.info("SouBidEarlyWarningService 预警招标资料提交无延期数据");
        } else {
            /** 技术负责人 和 招标负责人 */
            List<Long> requirementHeadIdList = dataList.stream().map(r -> r.get(ExtPrSouRequirementHead::getRequirementHeadId)).collect(Collectors.toList());
            List<RecordDTO> groupList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_GROUP).in(ExtPrSouRequirementGroup::getRequirementHeadId, requirementHeadIdList).in(ExtPrSouRequirementGroup::getGroupType, Arrays.asList(ExtPrRequirementGroupTypeEnum.SOU.getCode(),ExtPrRequirementGroupTypeEnum.TECH.getCode())));
            Map<Long, List<RecordDTO>> groupMap = groupList.stream().collect(Collectors.groupingBy(r -> r.get(ExtPrSouRequirementGroup::getRequirementHeadId)));

            List<RecordDTO> reuirementList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD).in(RequirementHead::getRequirementHeadId, requirementHeadIdList));
            Map<Long, RecordDTO> reuirementMap = reuirementList.stream().collect(Collectors.toMap(r -> r.get(RequirementHead::getRequirementHeadId), Function.identity(), (k1, k2)->k2));

            //发送预警
            dataList.stream().forEach(data -> {
                Map<String, String> var = new HashMap<>(50);
                /** ${souName}(${souNo})${processName}已延期${remark}，请关注并及时处理。 */

                String remark = "";
                Long diffDays = diffDays(data.get(ExtSouBidComponent.fieldName(ExtPrSouRequirementHead::getSendSouProfileEndDate)));
                if(Long.compare(diffDays, DIFF_THREE_DAYS) >= 0) {
                    remark = MessageFormat.format("（{0}天）", diffDays);
                }

                var.put("${souName}", data.get(ExtPrSouRequirementHead::getProjectName));
                var.put("${souNo}", reuirementMap.get(data.get(ExtPrSouRequirementHead::getRequirementHeadId)).get(RequirementHead::getRequirementHeadNum));
                var.put("${processName}", PROCESS_DATA_SUBMIT);
                var.put("${remark}", remark);

                List<String> userNameList = groupMap.getOrDefault(data.get(ExtPrSouRequirementHead::getRequirementHeadId), new ArrayList<>(50)).stream().map(r -> r.get(ExtPrSouRequirementGroup::getUsername)).distinct().collect(Collectors.toList());
                super.dingTalk(userNameList, var);
            });
        }
        log.info("SouBidEarlyWarningService 预警招标资料提交结束");
        return super.doWarning();
    }
}
