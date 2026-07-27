package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtNpmSouOpenBidRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtScoreRule;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtNpmSouOpenTodoService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouScoreRuleService;
import com.midea.cloud.srm.sou.timertasks.enums.TimerTaskTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
@Service
@Slf4j
public class ExtNpmSouOpenTodoServiceImpl implements ExtNpmSouOpenTodoService {
    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtNpmSouOpenBidRecordService openBidRecordService;

    @Autowired
    private IExtSouScoreRuleService souScoreRuleService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    private static final String EXT_URL_PARAM_ROUND = "round={0}";

    @Override
    public void sendTodo(List<ExtSouProject> souProjectList) {
        if(CollectionUtils.isNotEmpty(souProjectList)) {
            List<Long> projectIdList = souProjectList.stream().map(ExtSouProject::getProjectId).collect(Collectors.toList());
            List<ExtScoreRule> scoreRuleList = souScoreRuleService.lambdaQuery().in(ExtScoreRule::getProjectId, projectIdList).list();
            Map<Long, List<ExtScoreRule>>  scoreRuleMap = scoreRuleList.stream().collect(Collectors.groupingBy(ExtScoreRule::getProjectId));

            List<ExtNpmSouOpenBidRecord> recordList = openBidRecordService.lambdaQuery().in(ExtNpmSouOpenBidRecord::getProjectId, projectIdList).list();
            Map<String, ExtNpmSouOpenBidRecord> recordMap = recordList.stream().collect(Collectors.toMap(k -> StringUtils.joinWith(SrmConstant.UNDER_LINE, k.getProjectId(), k.getRound(), k.getOpenType(), k.getUserName()), Function.identity(), (k1, k2)->k2));

            List<ExtSouGroup> groupList = groupService.lambdaQuery().in(ExtSouGroup::getProjectId, projectIdList).in(ExtSouGroup::getGroupRole, Arrays.asList(ExtSouGroupRoleEnum.PRINCIPAL.getCode(), ExtSouGroupRoleEnum.LEADER.getCode(), ExtSouGroupRoleEnum.MINISTER.getCode())).list();
            Map<String, ExtSouGroup> groupMap = groupList.stream().collect(Collectors.toMap(k -> StringUtils.joinWith(SrmConstant.UNDER_LINE, k.getProjectId(), k.getGroupRole()), Function.identity(), (k1, k2)->k2));

            souProjectList.stream().forEach(project -> {
                try {
                    Boolean techFlag = checkTech(project, scoreRuleMap, recordMap, groupMap);
                    if(techFlag) {
                        log.info(MessageFormat.format("sendTodo as tech {0}", project.getProjectId()));
                        sendTechTodo(project, recordMap, groupMap);
                    } else {
                        log.info(MessageFormat.format("sendTodo as bus {0}", project.getProjectId()));
                        sendBusTodo(project, recordMap, groupMap);
                    }
                } catch (Exception e) {
                    log.error("sendTodo Exception "+project.getProjectId(), e);
                }
            });
        }
    }

    @Override
    public void havedone(Long projectId) {

        List<ExtNpmSouOpenBidRecord> recordList = openBidRecordService.lambdaQuery().eq(ExtNpmSouOpenBidRecord::getProjectId, projectId).eq(ExtNpmSouOpenBidRecord::getOpenStatus, ProcessStatusEnum.PENDING.getCode()).list();

        if(CollectionUtils.isNotEmpty(recordList)) {
            recordList.stream().forEach(record -> {
                String type = ExtOrderTypeEnum.TECH.getCode().equals(record.getOpenType()) ? TimerTaskTypeEnum.OPEN_TECHNICAL_BID.name() : TimerTaskTypeEnum.OPEN_BUSINESS_BID.name();
                try {
                    pjProjectExtClient.srmbpmHavedone(projectId, type, record.getUserName());
                    /** 给错误码--用于解除待办 */
                    record.setOpenStatus(ProcessStatusEnum.ERROR.getCode());
                } catch (Exception e) {
                    throw new BaseException(e.getMessage());
                }
            });

        }

    }

    @Override
    public void havedone(Long projectId, ExtNpmSouOpenBidRecord record) {
        String type = ExtOrderTypeEnum.TECH.getCode().equals(record.getOpenType()) ? TimerTaskTypeEnum.OPEN_TECHNICAL_BID.name() : TimerTaskTypeEnum.OPEN_BUSINESS_BID.name();
        try {
            pjProjectExtClient.srmbpmHavedone(projectId, type, record.getUserName());
            if(ProcessStatusEnum.PENDING.getCode().equals(record.getOpenStatus())) {
                record.setOpenStatus(ProcessStatusEnum.ERROR.getCode());
                openBidRecordService.updateById(record);
            }
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }

    protected void sendBusTodo(ExtSouProject souProject, Map<String, ExtNpmSouOpenBidRecord> recordMap, Map<String, ExtSouGroup> groupMap) throws Exception {
        //招标负责人
        String principalKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), ExtSouGroupRoleEnum.PRINCIPAL.getCode());
        ExtSouGroup principalGroup = groupMap.get(principalKey);

        String title = MessageFormat.format("[{0}-{1}]需开商务标，请及时处理。", souProject.getExtProjectNo(), souProject.getSouName());

        String principalOpenTechKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), souProject.getCurrentRound(), ExtOrderTypeEnum.BUS.getCode(), principalGroup.getUserName());
        ExtNpmSouOpenBidRecord principalOpenTechRecord = recordMap.get(principalOpenTechKey);
        if(Objects.isNull(principalOpenTechRecord) || !ProcessStatusEnum.COMPLETED.getCode().equals(principalOpenTechRecord.getOpenStatus())) {
            if(Objects.isNull(principalOpenTechRecord)) {
                principalOpenTechRecord = recordOpen(souProject, principalGroup, ExtOrderTypeEnum.BUS);
            } else {
                updateOpenRecord(principalOpenTechRecord);
            }
            //发送给招标负责人
            pjProjectExtClient.srmbpmTodo(souProject.getProjectId(), TimerTaskTypeEnum.OPEN_BUSINESS_BID.name(), title, principalGroup.getUserName(), MessageFormat.format(EXT_URL_PARAM_ROUND, souProject.getCurrentRound()));
            return;
        }

        //部长
        String ministerKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), ExtSouGroupRoleEnum.MINISTER.getCode());
        ExtSouGroup ministerGroup = groupMap.get(ministerKey);

        String ministerOpenTechKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), souProject.getCurrentRound(), ExtOrderTypeEnum.BUS.getCode(), ministerGroup.getUserName());
        ExtNpmSouOpenBidRecord ministerOpenTechRecord = recordMap.get(ministerOpenTechKey);
        if(Objects.isNull(ministerOpenTechRecord) || !ProcessStatusEnum.COMPLETED.getCode().equals(ministerOpenTechRecord.getOpenStatus())) {
            if(Objects.isNull(ministerOpenTechRecord)) {
                ministerOpenTechRecord = recordOpen(souProject, ministerGroup, ExtOrderTypeEnum.BUS);
            } else {
                updateOpenRecord(ministerOpenTechRecord);
            }
            //部长
            pjProjectExtClient.srmbpmTodo(souProject.getProjectId(), TimerTaskTypeEnum.OPEN_BUSINESS_BID.name(), title, ministerGroup.getUserName(), MessageFormat.format(EXT_URL_PARAM_ROUND, souProject.getCurrentRound()));
            return;
        }
    }

    protected void sendTechTodo(ExtSouProject souProject, Map<String, ExtNpmSouOpenBidRecord> recordMap, Map<String, ExtSouGroup> groupMap) throws Exception {
        //招标负责人
        String principalKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), ExtSouGroupRoleEnum.PRINCIPAL.getCode());
        ExtSouGroup principalGroup = groupMap.get(principalKey);

        String title = MessageFormat.format("[{0}-{1}]需开技术标，请及时处理。", souProject.getExtProjectNo(), souProject.getSouName());

        String principalOpenTechKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), souProject.getCurrentRound(), ExtOrderTypeEnum.TECH.getCode(), principalGroup.getUserName());
        ExtNpmSouOpenBidRecord principalOpenTechRecord = recordMap.get(principalOpenTechKey);
        if(Objects.isNull(principalOpenTechRecord) || !ProcessStatusEnum.COMPLETED.getCode().equals(principalOpenTechRecord.getOpenStatus())) {
            if(Objects.isNull(principalOpenTechRecord)) {
                principalOpenTechRecord = recordOpen(souProject, principalGroup, ExtOrderTypeEnum.TECH);
            } else {
                updateOpenRecord(principalOpenTechRecord);
            }
            //发送给招标负责人
            pjProjectExtClient.srmbpmTodo(souProject.getProjectId(), TimerTaskTypeEnum.OPEN_TECHNICAL_BID.name(), title, principalGroup.getUserName(), MessageFormat.format(EXT_URL_PARAM_ROUND, souProject.getCurrentRound()));
            return;
        }

        //技术负责人
        String leaderKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), ExtSouGroupRoleEnum.LEADER.getCode());
        ExtSouGroup leaderGroup = groupMap.get(leaderKey);

        String leaderOpenTechKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), souProject.getCurrentRound(), ExtOrderTypeEnum.TECH.getCode(), leaderGroup.getUserName());
        ExtNpmSouOpenBidRecord leaderOpenTechRecord = recordMap.get(leaderOpenTechKey);
        if(Objects.isNull(leaderOpenTechRecord) || !ProcessStatusEnum.COMPLETED.getCode().equals(leaderOpenTechRecord.getOpenStatus())) {
            if(Objects.isNull(leaderOpenTechRecord)) {
                leaderOpenTechRecord = recordOpen(souProject, leaderGroup, ExtOrderTypeEnum.TECH);
            } else {
                updateOpenRecord(leaderOpenTechRecord);
            }
            //技术负责人
            pjProjectExtClient.srmbpmTodo(souProject.getProjectId(), TimerTaskTypeEnum.OPEN_TECHNICAL_BID.name(), title, leaderGroup.getUserName(), MessageFormat.format(EXT_URL_PARAM_ROUND, souProject.getCurrentRound()));
            return;
        }
    }

    private void updateOpenRecord(ExtNpmSouOpenBidRecord record) {
        if(ProcessStatusEnum.ERROR.getCode().equals(record.getOpenStatus())) {
            record.setOpenStatus(ProcessStatusEnum.PENDING.getCode());
            openBidRecordService.updateById(record);
        }
    }

    private ExtNpmSouOpenBidRecord recordOpen(ExtSouProject souProject, ExtSouGroup group, ExtOrderTypeEnum typeEnum) {
        ExtNpmSouOpenBidRecord record = new ExtNpmSouOpenBidRecord();
        record.setOpenId(IdGenrator.generate());
        record.setProjectId(souProject.getProjectId());
        record.setRound(souProject.getCurrentRound());
        record.setOpenType(typeEnum.getCode());
        record.setOpenStatus(ProcessStatusEnum.PENDING.getCode());
        record.setUserId(group.getUserId());
        record.setUserName(group.getUserName());
        record.setFullName(group.getFullName());

        openBidRecordService.save(record);
        return record;
    }


    protected Boolean checkTech(ExtSouProject souProject, Map<Long, List<ExtScoreRule>>  scoreRuleMap, Map<String, ExtNpmSouOpenBidRecord> recordMap, Map<String, ExtSouGroup> groupMap) {
        //无评分规则或者轮次大于1
        if(!scoreRuleMap.containsKey(souProject.getProjectId()) || Integer.compare(souProject.getCurrentRound(), SrmConstant.NUM_ONE) == 1) {
            return false;
        }
        //招标负责人
        String principalKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), ExtSouGroupRoleEnum.PRINCIPAL.getCode());
        ExtSouGroup principalGroup = groupMap.get(principalKey);

        String principalOpenTechKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), souProject.getCurrentRound(), ExtOrderTypeEnum.TECH.getCode(), principalGroup.getUserName());
        ExtNpmSouOpenBidRecord principalOpenTechRecord = recordMap.get(principalOpenTechKey);
        if(Objects.isNull(principalOpenTechRecord) || !ProcessStatusEnum.COMPLETED.getCode().equals(principalOpenTechRecord.getOpenStatus())) {
            return true;
        }

        //技术负责人
        String leaderKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), ExtSouGroupRoleEnum.LEADER.getCode());
        ExtSouGroup leaderGroup = groupMap.get(leaderKey);

        String leaderOpenTechKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), souProject.getCurrentRound(), ExtOrderTypeEnum.TECH.getCode(), leaderGroup.getUserName());
        ExtNpmSouOpenBidRecord leaderOpenTechRecord = recordMap.get(leaderOpenTechKey);
        if(Objects.isNull(leaderOpenTechRecord) || !ProcessStatusEnum.COMPLETED.getCode().equals(leaderOpenTechRecord.getOpenStatus())) {
            return true;
        }

        return false;
    }
}
