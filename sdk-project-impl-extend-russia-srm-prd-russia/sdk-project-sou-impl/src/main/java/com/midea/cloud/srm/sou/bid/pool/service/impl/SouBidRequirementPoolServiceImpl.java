package com.midea.cloud.srm.sou.bid.pool.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.pool.dto.SouBidRequirementPoolDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.pool.service.SouBidRequirementPoolService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 招标计划需求池
 * @author huangbf3
 */
@Service
@Slf4j
public class SouBidRequirementPoolServiceImpl implements SouBidRequirementPoolService {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private QlService qlService;

    @Override
    public SouBidRequirementPoolDto getRequirementPoolInfo(SouBidRequirementPoolDto param) {

        SouBidRequirementPoolDto poolDto = new SouBidRequirementPoolDto();

        //查询供应商推荐单、标书拟定单的
        LambdaQueryWrapper<ExtSouDemand> demandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        demandLambdaQueryWrapper.in(ExtSouDemand::getApplicantNo, param.getApplicantNoList());
        demandLambdaQueryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        List<ExtSouDemand> souDemandList = demandService.list(demandLambdaQueryWrapper);

        if (CollectionUtils.isNotEmpty(souDemandList)) {
            demandLambdaQueryWrapper = new LambdaQueryWrapper<>();
            demandLambdaQueryWrapper.in(ExtSouDemand::getProjectId, souDemandList.stream().map(ExtSouDemand::getProjectId).distinct().collect(Collectors.toList()));
            demandLambdaQueryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
            souDemandList = demandService.list(demandLambdaQueryWrapper);
            poolDto.setSouDemandList(souDemandList);

            LambdaQueryWrapper<ExtSouProject> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            projectLambdaQueryWrapper.in(ExtSouProject::getProjectId, souDemandList.stream().map(ExtSouDemand::getProjectId).distinct().collect(Collectors.toList()));
            List<ExtSouProject> extSouProjectList = projectService.list(projectLambdaQueryWrapper);

            Map<String, List<ExtSouProject>> projectMap = extSouProjectList.stream().collect(Collectors.groupingBy(ExtSouProject::getSouType));
            poolDto.setProjectList(projectMap.getOrDefault(SouTypeEnum.bid.name(), new ArrayList<>()));

            List<ExtSouProject> recommvendorList = projectMap.getOrDefault(SouTypeEnum.recomm.name(), new ArrayList<>());
            poolDto.setRecommvendorProjectList(JSON.parseArray(JSON.toJSONString(recommvendorList), RecommvendorProjectDto.class));
        }

        List<SouReqHead> souReqHeadList = new ArrayList<>();
        //查询寻求单
        param.getApplicantNoList().stream().forEach(applicatNo -> {
            QlQueryWrapper qlQueryWrapper = QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER).contains(SouReqHead::getRequirementHeadNoList, applicatNo);
            List<SouReqHead> headList = qlService.queryByWrapper(qlQueryWrapper, SouReqHead.class);
            if (CollectionUtils.isNotEmpty(headList)) {
                souReqHeadList.addAll(headList);
            }
        });

        List<SouReqHead> reqHeadList = souReqHeadList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new
                        TreeSet<>(Comparator.comparing(SouReqHead::getReqHeadId))),
                ArrayList::new));
        poolDto.setSouReqHeadList(reqHeadList);

        return poolDto;
    }
}
