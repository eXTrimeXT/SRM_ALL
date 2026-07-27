
package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.question.dto.QuestionDTO;
import com.midea.cloud.srm.model.sou.question.enums.QuestionStatusEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
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
@Service(value = "REQ_CANCLE_QUESTION")
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class ReuirementCancleServiceQuestionImpl extends RequirementCancleFlowServiceImpl implements RequirementCancleService {
    @Autowired
    private QlOpenClient qlOpenClient;


    @Override
    public Object cancle(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache) {
        //取消由招标单发起的质疑单
        cancleAsBid(requirementHeadIdList, requirementHeadNumMap, localCache);
        //取消由寻源需求列表发起的质疑单
        cancleAsSou(requirementHeadIdList, requirementHeadNumMap, localCache);
        return null;
    }

    /**
     * 取消由招标单发起的质疑单
     * @param requirementHeadIdList
     * @param requirementHeadNumMap
     * @param localCache
     * @return
     */
    private Object cancleAsBid(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache) {
        //读取本地缓存
        List<RecordDTO> souProjectList = (List<RecordDTO>) localCache.get(CancleCacheConstants.SOU_PROJECT_LIST);
        if(CollectionUtils.isEmpty(souProjectList)) {
            return null;
        }

        List<Long> projectIdList = souProjectList.stream().filter(souProject -> SouBiddingProStatusEnum.ABANDON.getCode().equals(souProject.get(ExtSouProject::getProjectStatus))).map(r -> r.get(ExtSouProject::getProjectId)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(projectIdList)) {
            return null;
        }

        doCancle(QlOpenWrappers.query(MqlType.QUESTION).in(QuestionDTO::getProjectId, projectIdList), localCache);

        return null;
    }

    private List<RecordDTO> doCancle(QlOpenQueryWrapper queryWrapper, HashMap<String, Object> localCache) {
        List<RecordDTO> recordDTOList = qlOpenClient.query(ContextPath.SOU, queryWrapper);
        if(CollectionUtils.isEmpty(recordDTOList)) {
            return recordDTOList;
        }
        List<RecordDTO> updateList = new ArrayList<>();
        //取消逻辑
        for (RecordDTO question : recordDTOList) {
            //质疑
            question.put(QuestionDTO::getQuestionStatus, QuestionStatusEnum.ABANDON.getCode());
            question.put(QuestionDTO::getReasonDesc, SrmConstant.PR_ABANDON_DEAULT_REASON);
            updateList.add(question);
        }
        if(CollectionUtils.isNotEmpty(updateList)) {
            qlOpenClient.update(ContextPath.SOU, MqlType.QUESTION, updateList);
        }
        cacheQuestion(recordDTOList, localCache);
        return recordDTOList;
    }

    private void cacheQuestion(List<RecordDTO> addList, HashMap<String, Object> localCache) {
        List<RecordDTO> questionList = (List<RecordDTO>) localCache.getOrDefault("questionList", new ArrayList<>());
        if(CollectionUtils.isNotEmpty(addList)) {
            questionList.addAll(addList);
        }
        localCache.put(CancleCacheConstants.QUESTION_LIST, questionList);
    }

    /**
     * 取消由寻源需求列表发起的质疑单
     * @param requirementHeadIdList
     * @param requirementHeadNumMap
     * @param localCache
     * @return
     */
    private Object cancleAsSou(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache) {


        List<Record> extPrSouRequirementHeadList = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.EXT_CANCLE_REQUIRMENTHEAD_LIST, new ArrayList<>());

        if(CollectionUtils.isEmpty(extPrSouRequirementHeadList)) {
            return null;
        }

        List<String> souReqNoList = new ArrayList<>(16);
        extPrSouRequirementHeadList.stream().forEach(ext -> {
            String souReqNo = ext.get(ExtPrSouRequirementHead::getSouReqNo);
            if(StringUtils.isBlank(souReqNo)) {
                souReqNoList.add(souReqNo);
            }
        });

        if(CollectionUtils.isNotEmpty(souReqNoList)) {
            doCancle(QlOpenWrappers.query(MqlType.QUESTION).in(QuestionDTO::getSouNo, souReqNoList), localCache);
        }

        return null;
    }

}
