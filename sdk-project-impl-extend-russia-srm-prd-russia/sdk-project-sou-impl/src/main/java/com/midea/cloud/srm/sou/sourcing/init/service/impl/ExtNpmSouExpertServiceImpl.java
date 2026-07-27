package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.SouBidExpertLevelEnum;
import com.midea.cloud.srm.model.sou.enums.SouExpertLevelEnum;
import com.midea.cloud.srm.model.sou.enums.SouExpertRangeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouExpertRandomExtractDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.sourcing.expert.mapper.ExtSouNpmExpertMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtNpmSouExpertService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ExtNpmSouExpertServiceImpl implements ExtNpmSouExpertService {
    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private ExtSouNpmExpertMapper extSouNpmExpertMapper;

    @Autowired
    private IExtSouGroupService groupService;

    @Override
    public List<ExtSouExpertRecord> queryExpert(ApiExtSouExpertRandomExtractDto param, ExtSouProject project, List<ExtSouGroup> groupList) {
        Map<String, Object> query = new HashMap<>(50);
        List<Long> orgIdList = new ArrayList<>();
        String categoryCode = project.getExtCategoryCode();
        query.put("categoryCode", categoryCode);
        if(SouExpertRangeEnum.BU.getCode().equals(param.getExtExpertRange())) {
            orgIdList = extSouNpmExpertMapper.queryOuIdListAsBuId(project.getExtOrgBuId());
            orgIdList.add(project.getExtOrgBuId());
            orgIdList.add(project.getExtOrgOuId());
        } else if(SouExpertRangeEnum.OU.getCode().equals(param.getExtExpertRange())) {
            orgIdList.add(project.getExtOrgOuId());
        }

        List<Long> userIdList = new ArrayList<>();

        if(Objects.isNull(groupList)) {
            groupList = groupService.lambdaQuery().eq(ExtSouGroup::getProjectId, project.getProjectId()).list();
        }

        if(CollectionUtils.isNotEmpty(groupList)) {
            userIdList = groupList.stream().map(g -> g.getUserId()).distinct().collect(Collectors.toList());
        }

        //排除招标计划池的招标技术负责人
        LambdaQueryWrapper<ExtSouDemand> demandQuery = new LambdaQueryWrapper<>();
        demandQuery.eq(ExtSouDemand::getProjectId, project.getProjectId());
        demandQuery.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        List<ExtSouDemand> demandList = demandService.list(demandQuery);
        if(CollectionUtils.isNotEmpty(demandList)) {
            List<Long> techUserIdList = extSouNpmExpertMapper.queryTechUserIdAsRequirement(demandList.stream().map(d->d.getApplicantNo()).distinct().collect(Collectors.toList()));
            if(CollectionUtils.isNotEmpty(techUserIdList)) {
                userIdList.addAll(techUserIdList);
            }
        }

        if(CollectionUtils.isNotEmpty(userIdList)) {
            query.put("userIdList", userIdList);
        }

        if(CollectionUtils.isNotEmpty(orgIdList)) {
            query.put("orgIdList", orgIdList.stream().distinct().collect(Collectors.toList()));
        }

        List<ExtSouExpertRecord> recordList = extSouNpmExpertMapper.queryExpertList(query);


        return recordList;
    }
}
