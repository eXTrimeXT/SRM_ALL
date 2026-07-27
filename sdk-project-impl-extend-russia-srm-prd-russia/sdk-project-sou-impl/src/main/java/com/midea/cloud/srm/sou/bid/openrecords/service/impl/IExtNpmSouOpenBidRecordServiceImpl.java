package com.midea.cloud.srm.sou.bid.openrecords.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtNpmSouOpenBidRecordDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtNpmSouOpenBidRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.sou.bid.openrecords.mapper.ExtNpmSouOpenBidRecordMapper;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtNpmSouOpenTodoService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description scc_npm_sou_open_bid_record
 * @author panmq
 * @date 2023-11-13
 */
@Slf4j
@Service
public class IExtNpmSouOpenBidRecordServiceImpl extends ServiceImpl<ExtNpmSouOpenBidRecordMapper, ExtNpmSouOpenBidRecord> implements IExtNpmSouOpenBidRecordService {
    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private ExtNpmSouOpenTodoService extNpmSouOpenTodoService;

    @Override
    public List<ExtNpmSouOpenBidRecordDto> queryTechOpenRecord(Long projectId) {
        List<ExtNpmSouOpenBidRecord> recordList = this.lambdaQuery().eq(ExtNpmSouOpenBidRecord::getProjectId, projectId).eq(ExtNpmSouOpenBidRecord::getRound, 1)
                .eq(ExtNpmSouOpenBidRecord::getOpenType, ExtOrderTypeEnum.TECH.getCode()).list();
        //查询招标负责人+评标组长
        List<ExtSouGroup> groupList = groupService.lambdaQuery().eq(ExtSouGroup::getProjectId, projectId).in(ExtSouGroup::getGroupRole, Arrays.asList(ExtSouGroupRoleEnum.PRINCIPAL.getCode(), ExtSouGroupRoleEnum.LEADER.getCode())).list();
        Map<Long, ExtNpmSouOpenBidRecord> recordMap = recordList.stream().collect(Collectors.toMap(k -> ObjectUtils.defaultIfNull(k.getUserId(), 0L), Function.identity(), (k1, k2)->k2));

        //构造数据
        List<ExtNpmSouOpenBidRecordDto> recordDtos = new ArrayList<>(groupList.size());

        groupList.stream().forEach(group -> recordDtos.add(buildRecord(recordMap.get(group.getUserId()), group, 1, ExtOrderTypeEnum.TECH.getCode())));

        return recordDtos;
    }

    @Override
    public List<ExtNpmSouOpenBidRecordDto> queryBusOpenRecord(Long projectId, Integer round) {
        List<ExtNpmSouOpenBidRecord> recordList = this.lambdaQuery().eq(ExtNpmSouOpenBidRecord::getProjectId, projectId).eq(ExtNpmSouOpenBidRecord::getRound, round)
                .eq(ExtNpmSouOpenBidRecord::getOpenType, ExtOrderTypeEnum.BUS.getCode()).list();
        //查询招标负责人+部长
        List<ExtSouGroup> groupList = groupService.lambdaQuery().eq(ExtSouGroup::getProjectId, projectId).in(ExtSouGroup::getGroupRole, Arrays.asList(ExtSouGroupRoleEnum.PRINCIPAL.getCode(), ExtSouGroupRoleEnum.MINISTER.getCode())).list();
        Map<Long, ExtNpmSouOpenBidRecord> recordMap = recordList.stream().collect(Collectors.toMap(k -> ObjectUtils.defaultIfNull(k.getUserId(), 0L), Function.identity(), (k1, k2)->k2));

        //构造数据
        List<ExtNpmSouOpenBidRecordDto> recordDtos = new ArrayList<>(groupList.size());

        groupList.stream().forEach(group -> recordDtos.add(buildRecord(recordMap.get(group.getUserId()), group, round, ExtOrderTypeEnum.BUS.getCode())));

        return recordDtos;
    }

    @Override
    public Boolean isOpenByAllUser(Long projectId, Integer round, String openType) {
        List<ExtNpmSouOpenBidRecordDto> recordDtos = new ArrayList<>();
        if(ExtOrderTypeEnum.TECH.getCode().equals(openType)) {
            recordDtos = queryTechOpenRecord(projectId);
        } else {
            recordDtos = queryBusOpenRecord(projectId, round);
        }
        return !recordDtos.stream().filter(r -> !ProcessStatusEnum.COMPLETED.getCode().equals(r.getOpenStatus())).findAny().isPresent();
    }

    private ExtNpmSouOpenBidRecordDto buildRecord(ExtNpmSouOpenBidRecord record, ExtSouGroup group, Integer round, String openType) {
        ExtNpmSouOpenBidRecordDto recordDto = new ExtNpmSouOpenBidRecordDto();
        recordDto.setProjectId(group.getProjectId());
        recordDto.setUserId(group.getUserId());
        recordDto.setFullName(group.getFullName());
        recordDto.setUserName(group.getUserName());
        recordDto.setOpenStatus(ProcessStatusEnum.PENDING.getCode());
        recordDto.setRound(round);
        recordDto.setOpenType(openType);
        if(ObjectUtils.allNotNull(record)) {
            BeanCopyUtil.copyProperties(recordDto, record);
        }
        return recordDto;
    }

    @Override
    public ExtNpmSouOpenBidRecord openRecord(Long projectId, Integer round, String openType) {

        Long userId = AppUserUtil.getLoginAppUser().getUserId();
        List<ExtNpmSouOpenBidRecord> recordList = this.lambdaQuery().eq(ExtNpmSouOpenBidRecord::getProjectId, projectId).eq(ExtNpmSouOpenBidRecord::getRound, round)
                .eq(ExtNpmSouOpenBidRecord::getOpenType, openType).eq(ExtNpmSouOpenBidRecord::getUserId, userId).list();

        ExtNpmSouOpenBidRecord record = new ExtNpmSouOpenBidRecord();
        record.setProjectId(projectId);
        record.setUserId(userId);
        record.setFullName(AppUserUtil.getLoginAppUser().getNickname());
        record.setUserName(AppUserUtil.getLoginAppUser().getUsername());
        record.setOpenStatus(ProcessStatusEnum.COMPLETED.getCode());
        record.setRound(round);
        record.setOpenType(openType);
        if(CollectionUtils.isNotEmpty(recordList)) {
            record.setOpenId(recordList.get(0).getOpenId());
            this.updateById(record);
        } else {
            record.setOpenId(IdGenrator.generate());
            this.save(record);
        }
        extNpmSouOpenTodoService.havedone(projectId, record);
        return record;
    }
}

