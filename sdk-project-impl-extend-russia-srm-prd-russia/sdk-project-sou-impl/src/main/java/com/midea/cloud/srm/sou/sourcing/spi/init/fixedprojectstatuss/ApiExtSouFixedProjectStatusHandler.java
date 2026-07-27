package com.midea.cloud.srm.sou.sourcing.spi.init.fixedprojectstatuss;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.sou.enums.SouBidPlanTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouRound;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouPlanService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouRoundService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusFactory;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusRangeVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouFixedProjectStatusHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ExtSouProjectMapper extSouProjectMapper;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtSouRoundService roundService;

    /** 需要修复的状态，技术标投标中、商务标投标中 */
    private static final List<SouBiddingProStatusEnum> NEED_FIXED_STATUS = Arrays.asList(SouBiddingProStatusEnum.TECH_BID, SouBiddingProStatusEnum.BUS_BID);

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @ApiOperation("校验是否需要修正项目状态")
    public boolean judgeNeedFixedProjectStatus(ExtSouProject param, String souType) {
        //修正标识, true-需修正，false-不许修正
        AtomicBoolean fixedFlag = new AtomicBoolean(false);
        SouBiddingProStatusEnum proStatusEnum = getSouBiddingProStatusEnum(param.getProjectStatus());

        if(!Objects.isNull(proStatusEnum) && NEED_FIXED_STATUS.contains(proStatusEnum)) {
            //查询招标计划
            ExtSouPlan extSouPlan = getProjectPlan(param.getProjectId(), souType);
            if(SouBiddingProStatusEnum.TECH_BID.equals(proStatusEnum)) {
                return judgeDelineTime(Objects.isNull(extSouPlan.getTechEndFixTime())? extSouPlan.getTechEndTime(): extSouPlan.getTechEndFixTime());
            }
            //查询最新轮次
            ExtSouRound souRound = getNewestRound(param, souType);
            if(Objects.isNull(souRound)) {
                return judgeDelineTime(extSouPlan.getBusEndTime());
            }
            return judgeDelineTime(souRound.getOrderEndTime());
        }
        return fixedFlag.get();
    }

    protected SouBiddingProStatusEnum getSouBiddingProStatusEnum(String status) {
        SouBiddingProStatusEnum proStatusEnum = null;

        try {
            proStatusEnum = SouBiddingProStatusEnum.valueOf(status);
        } catch (Exception e) {
            log.info("状态转换异常");
        }
        return proStatusEnum;
    }

    /**
     * 获取修正状态
     * @param param
     * @param souType
     * @return
     */
    public SouBiddingProStatusEnum getFixedProjectStatus(ExtSouProject param, String souType) {
        SouBiddingProStatusEnum proStatusEnum = getSouBiddingProStatusEnum(param.getProjectStatus());
        switch (proStatusEnum) {
            case TECH_BID:
                return SouBiddingProStatusEnum.TECH_BID_END;
            case BUS_BID:
                return SouBiddingProStatusEnum.BUS_BID_END;
            default:;
        }
        return proStatusEnum;
    }

    /**
     * 判断当前时间是否大于等于截止时间
     * @param endTime
     * @return
     */
    protected Boolean judgeDelineTime(Date endTime) {
        AtomicBoolean judgeFlag = new AtomicBoolean(false);
        //如果当前时间等于或者大于截止时间，则返回true
        if(!Objects.isNull(endTime) && !endTime.after(new Date())) {
            judgeFlag.set(true);
        }
        return judgeFlag.get();
    }

    /**
     * 查询最大轮次
     * @param param
     * @param souType
     * @return
     */
    protected ExtSouRound getNewestRound(ExtSouProject param, String souType) {
        LambdaQueryWrapper<ExtSouRound> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouRound::getProjectId, param.getProjectId());
        queryWrapper.gt(ExtSouRound::getRound, 0);
        queryWrapper.orderByDesc(ExtSouRound::getRound);
        PageUtil.startPage(1, 1);
        List<ExtSouRound> souRoundList = roundService.list(queryWrapper);

        if(CollectionUtils.isNotEmpty(souRoundList)) {
            return souRoundList.get(0);
        }
        return null;
    }

    /**
     * 招标计划
     * @param projectId
     * @param souType
     * @return
     */
    protected ExtSouPlan getProjectPlan(Long projectId, String souType) {
        LambdaQueryWrapper<ExtSouPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouPlan::getProjectId, projectId);
        queryWrapper.eq(ExtSouPlan::getPlanType, SouBidPlanTypeEnum.PLAN.getCode());
        List<ExtSouPlan> souPlanList = planService.list(queryWrapper);
        if(CollectionUtils.isNotEmpty(souPlanList)) {
            return souPlanList.get(0);
        }
        return new ExtSouPlan();
    }

    @ApiOperation("校验商务标投标中是否需要修正项目状态")
    public List<ExtSouProject> judgeNeedFixedProjectStatusAsBusBid(String souType) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("s.PROJECT_STATUS", SouBiddingProStatusEnum.BUS_BID.getCode());
        List<ExtSouProject> projectList = extSouProjectMapper.busBidCount(queryWrapper);
        if(CollectionUtils.isNotEmpty(projectList)) {
            projectList.stream().forEach(p -> p.setProjectStatus(SouBiddingProStatusEnum.BUS_BID_END.getCode()));
        }
        return projectList;
    }

    @ApiOperation("校验技术标投标中是否需要修正项目状态")
    public List<ExtSouProject> judgeNeedFixedProjectStatusAsTechBid(String souType) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("s.PROJECT_STATUS", SouBiddingProStatusEnum.TECH_BID.getCode());
        queryWrapper.eq("p.PLAN_TYPE", SouBidPlanTypeEnum.PLAN.getCode());
        List<ExtSouProject> projectList = extSouProjectMapper.techBidCount(queryWrapper);
        if(CollectionUtils.isNotEmpty(projectList)) {
            projectList.stream().forEach(p -> p.setProjectStatus(SouBiddingProStatusEnum.TECH_BID_END.getCode()));
        }
        return projectList;
    }
}
