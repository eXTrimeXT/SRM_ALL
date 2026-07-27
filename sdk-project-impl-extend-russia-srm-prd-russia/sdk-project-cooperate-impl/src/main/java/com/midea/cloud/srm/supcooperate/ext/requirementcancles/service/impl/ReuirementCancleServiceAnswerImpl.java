
package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerStatusEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.question.dto.QuestionDTO;
import com.midea.cloud.srm.model.sou.question.enums.QuestionStatusEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
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
@Service(value = "REQ_CANCLE_ANSWER")
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class ReuirementCancleServiceAnswerImpl extends RequirementCancleFlowServiceImpl implements RequirementCancleService {
    @Autowired
    private QlOpenClient qlOpenClient;


    @Override
    public Object cancle(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache) {
        //读取本地缓存
        List<RecordDTO> souProjectList = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.SOU_PROJECT_LIST, new ArrayList<>(16));

        List<RecordDTO> questionList = RequirementCancleUtils.getCacheValue(localCache, CancleCacheConstants.QUESTION_LIST, new ArrayList<>(16));

        List<Long> projectIdList = null;
        List<Long> questionIdList = null;
        if(CollectionUtils.isNotEmpty(souProjectList)) {
            projectIdList = souProjectList.stream().filter(souProject -> SouBiddingProStatusEnum.ABANDON.getCode().equals(souProject.get(ExtSouProject::getProjectStatus))).map(r -> r.get(ExtSouProject::getProjectId)).collect(Collectors.toList());
        }

        if(CollectionUtils.isNotEmpty(questionList)) {
            questionIdList = questionList.stream().map(r -> r.get(QuestionDTO::getQuestionId)).distinct().collect(Collectors.toList());
        }
        cancleAsBid(requirementHeadIdList, requirementHeadNumMap, projectIdList);
        cancleAsQuestion(requirementHeadIdList, requirementHeadNumMap, questionIdList);
        return null;
    }

    protected void cancleAsQuestion(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, List<Long> questionIdList) {
        if(CollectionUtils.isEmpty(questionIdList)) {
            return;
        }
        QlOpenQueryWrapper qlOpenQueryWrapper = QlOpenWrappers.query(MqlType.ANSWER);
        qlOpenQueryWrapper.in(AnswerDTO::getQuestionId, questionIdList);
        List<RecordDTO> recordList = qlOpenClient.query(ContextPath.SOU, qlOpenQueryWrapper);
        cancleAnswer(recordList);
    }

    protected void cancleAsBid(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, List<Long> projectIdList) {
        if(CollectionUtils.isEmpty(projectIdList)) {
            return;
        }
        QlOpenQueryWrapper qlOpenQueryWrapper = QlOpenWrappers.query(MqlType.ANSWER);
        qlOpenQueryWrapper.in(AnswerDTO::getProjectId, projectIdList);
        List<RecordDTO> recordList = qlOpenClient.query(ContextPath.SOU, qlOpenQueryWrapper);
        cancleAnswer(recordList);
    }

    protected void cancleAnswer(List<RecordDTO> recordList) {
        if(CollectionUtils.isEmpty(recordList)) {
            return;
        }
        List<RecordDTO> updateList = new ArrayList<>();
        //取消逻辑
        for (RecordDTO answer : recordList) {
            //澄清
            answer.put(AnswerDTO::getAnswerStatus, AnswerStatusEnum.ABANDON.getCode());
            answer.put(AnswerDTO::getReasonDesc, SrmConstant.PR_ABANDON_DEAULT_REASON);
            updateList.add(answer);
        }
        if(CollectionUtils.isNotEmpty(updateList)) {
            qlOpenClient.update(ContextPath.SOU, MqlType.ANSWER, updateList);
        }
    }

}
