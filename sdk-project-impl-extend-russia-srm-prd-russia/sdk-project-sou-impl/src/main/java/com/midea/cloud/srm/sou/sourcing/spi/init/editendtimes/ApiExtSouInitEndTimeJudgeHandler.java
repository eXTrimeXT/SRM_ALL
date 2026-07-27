
package com.midea.cloud.srm.sou.sourcing.spi.init.editendtimes;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidPlanTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouEndTimeDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectModifyDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtNpmSouOpenBidRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouRound;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouRoundDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtNpmSouOpenTodoService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouPlanService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusFactory;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusRangeVo;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.ExtSouProjectEditPO;
import com.midea.cloud.srm.sou.timertasks.enums.TimerTaskTypeEnum;
import com.midea.cloud.srm.sou.timertasks.service.SrmNpmSouTimerTaskService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouInitEndTimeJudgeHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private SouRoundDAO souRoundDAO;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtNpmSouOpenBidRecordService openBidRecordService;

    @Autowired
    private ExtNpmSouOpenTodoService extNpmSouOpenTodoService;

    @Autowired
    private SrmNpmSouTimerTaskService srmNpmSouTimerTaskService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }


    @ApiOperation("调整投标截止时间前的额外处理")
    public void doHandlerBeforeEditProject(ApiExtSouEndTimeDto param, boolean isCopy, String souType) {
    }

    public ExtSouEndTimePo doHandlerValidAndConvertEditProject(ApiExtSouEndTimeDto param, boolean isCopy, String souType) {
        ExtSouProject project = projectService.getById(param.getProjectId());
        AssertUtils.notNull(project, "单据信息不存在！");

        if (!Arrays.asList(SouBiddingProStatusEnum.TECH_BID.getCode(), SouBiddingProStatusEnum.TECH_BID_END.getCode(), SouBiddingProStatusEnum.BUS_BID.getCode(), SouBiddingProStatusEnum.BUS_BID_END.getCode()).contains(project.getProjectStatus())) {
            throw new BaseException("当前单据状态不允许调整投标截止时间！");
        }

        //查询当前轮次的开标记录
        List<ExtNpmSouOpenBidRecord> npmSouOpenBidRecords = openBidRecordService.lambdaQuery().eq(ExtNpmSouOpenBidRecord::getProjectId, param.getProjectId()).eq(ExtNpmSouOpenBidRecord::getRound, project.getCurrentRound()).eq(ExtNpmSouOpenBidRecord::getOpenStatus, ProcessStatusEnum.COMPLETED.getCode()).list();

        ExtSouEndTimePo po = new ExtSouEndTimePo();

        if(Arrays.asList(SouBiddingProStatusEnum.TECH_BID.getCode(), SouBiddingProStatusEnum.TECH_BID_END.getCode()).contains(project.getProjectStatus())) {
            //校验技术标开标状态
            checkOpenBid(npmSouOpenBidRecords, ExtOrderTypeEnum.TECH);

            List<ExtSouPlan> extSouPlans = planService.lambdaQuery().eq(ExtSouPlan::getProjectId, project.getProjectId())
                    .eq(ExtSouPlan::getPlanType, SouBidPlanTypeEnum.PLAN.getCode()).list();
            if(CollectionUtils.isNotEmpty(extSouPlans)) {
                po.setSouPlan(extSouPlans.get(0));
                po.getSouPlan().setTechEndFixTime(param.getAdjustEndTime());
            }
        } else {
            //校验商务标开标状态
            checkOpenBid(npmSouOpenBidRecords, ExtOrderTypeEnum.BUS);

            LambdaQueryWrapper<SouRound> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SouRound::getProjectId, param.getProjectId());
            queryWrapper.eq(SouRound::getRound, project.getCurrentRound());

            List<SouRound> souRoundList = souRoundDAO.list(queryWrapper);

            if(CollectionUtils.isNotEmpty(souRoundList)) {
                po.setSouRound(souRoundList.get(0));
                po.getSouRound().setOrderEndTime(param.getAdjustEndTime());
            } else {
                throw new BaseException("当前轮次信息不存在!");
            }
        }

        //修复截止状态
        if(SouBiddingProStatusEnum.TECH_BID_END.getCode().equals(project.getProjectStatus())) {
            project.setProjectStatus(SouBiddingProStatusEnum.TECH_BID.getCode());
            projectService.updateById(project);
        }
        if(SouBiddingProStatusEnum.BUS_BID_END.getCode().equals(project.getProjectStatus())) {
            project.setProjectStatus(SouBiddingProStatusEnum.BUS_BID.getCode());
            projectService.updateById(project);
        }

        return po;
    }

    protected void checkOpenBid(List<ExtNpmSouOpenBidRecord> npmSouOpenBidRecords, ExtOrderTypeEnum openType) {
        if(CollectionUtils.isEmpty(npmSouOpenBidRecords)) {
            return;
        }
        boolean check = npmSouOpenBidRecords.stream().filter(r -> openType.getCode().equals(r.getOpenType())).findAny().isPresent();
        if(check) {
            throw new BaseException("已存在开标行为，不允许调整时间！");
        }
    }

    @ApiOperation("调整投标截止时间后的额外处理")
    public void doHandlerAfterEditProject(ApiExtSouEndTimeDto param, boolean isCopy, String souType, ExtSouEndTimePo po) {
        extNpmSouOpenTodoService.havedone(param.getProjectId());
        srmNpmSouTimerTaskService.listeningTask(param.getProjectId(), TimerTaskTypeEnum.OPEN_TECHNICAL_BID.name(), param.getAdjustEndTime());
    }

}
