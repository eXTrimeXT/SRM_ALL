package com.midea.cloud.srm.sou.sourcing.spi.init.open;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.sourcing.fixstatus.service.SouFixedProjectStatusService;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusFactory;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusRangeVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtThechOpenHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtNpmSouOpenBidRecordService openBidRecordService;

    @Autowired
    private SouFixedProjectStatusService fixedProjectStatusService;

    @Autowired
    private QlService qlService;

    @Autowired
    private ExtNpmSouOpenTodoService extNpmSouOpenTodoService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @ApiOperation("当前是否可以技术开标")
    public void judgeOpenTechAuth(long projectId, String souType) {

        //修正单据状态
        fixedProjectStatusService.fixedProjectStatus(projectService.getById(projectId), souType);

        ExtSouProject souProject = projectService.getById(projectId);
        AssertUtils.notNull(souProject, "寻源单据信息不存在！");

        if(StringUtils.equals(souProject.getExtSouMode(), SouBidSouModeEnum.TECH_THEN_BUS.getCode())){
            //先收技术后收商务
            if(!Arrays.asList(SouBiddingProStatusEnum.TECH_BID_END).contains(SouBiddingProStatusEnum.valueOf(souProject.getProjectStatus()))) {
                throw new BaseException("当前状态不允许技术开标！");
            }
        }else{
            //同时收标
            if(!Arrays.asList(SouBiddingProStatusEnum.BUS_BID_END).contains(SouBiddingProStatusEnum.valueOf(souProject.getProjectStatus()))) {
                throw new BaseException("当前状态不允许技术开标！");
            }
        }


        if(openBidRecordService.isOpenByAllUser(projectId, 1, ExtOrderTypeEnum.TECH.getCode())) {
            throw new BaseException("已开过技术标，请勿重复操作！");
        }

        //所有的评标小组成员都是有效的专家库
        List<ExtSouGroup> evaGroupList = groupService.lambdaQuery().eq(ExtSouGroup::getProjectId, projectId)
                .eq(ExtSouGroup::getExtEvaFlag, YesOrNo.YES.getValue())
                .eq(ExtSouGroup::getScoreAuth, SouScoreDimensionCodeEnum.SOU_TECH.getCode()).list();
        //查询有效的专家库
        List<Record> recordList = qlService.query(MqlType.EXT_SOU_EXPERT, MeiQl.newCondition().in("expertUserId", evaGroupList.stream().map(e -> e.getUserId()).distinct().collect(Collectors.toList()))
                .eq("hasQuite", YesOrNo.NO.getValue()).eq("hasFrozen", YesOrNo.NO.getValue()), Record.class);
        List<Long> expertUserIdList = recordList.stream().map(r -> r.getLong("expertUserId")).distinct().collect(Collectors.toList());
        //成员
        List<Long> groupUserIdList = evaGroupList.stream().map(g -> g.getUserId()).distinct().collect(Collectors.toList());
        //移除有效专家，剩下的是无效专家
        groupUserIdList.removeAll(expertUserIdList);
        if(CollectionUtils.isNotEmpty(groupUserIdList)) {
            throw new BaseException(MessageFormat.format("存在以下无效专家：{0}", evaGroupList.stream().filter(g -> groupUserIdList.contains(g.getUserId())).map(g -> g.getFullName()).distinct().collect(Collectors.joining("、"))));
        }

        //要求评标人数和高级专家人数
        Map<String, List<ExtSouGroup>> evaGroupMap = evaGroupList.stream().collect(Collectors.groupingBy(ExtSouGroup::getExtExpertLevel));

        Integer totalCount = new Integer(evaGroupList.size());
        Integer seniorCount = new Integer(evaGroupMap.getOrDefault(SouBidExpertLevelEnum.SENIOR.getCode(), new ArrayList<>(16)).size());

        if(totalCount.compareTo(ObjectUtils.defaultIfNull(souProject.getExtBidEvaluatorNum(), SrmConstant.NUM_ZERO)) == -1) {
            throw new BaseException(MessageFormat.format("评标总人数要求{0}个，当前评标人数{1}个，不满足评标总人数要求不允许开标！", souProject.getExtBidEvaluatorNum(), totalCount));
        }

        if(seniorCount.compareTo(ObjectUtils.defaultIfNull(souProject.getExtAskSeniorExpertNum(), SrmConstant.NUM_ZERO)) == -1) {
            throw new BaseException(MessageFormat.format("高级专家人数要求{0}个，当前评标人数{1}个，不满足高级专家人数要求不允许开标！", souProject.getExtAskSeniorExpertNum(), totalCount));
        }
    }

    /**
     * 数据校验和数据转换
     * @param projectId 参数
     * @param souType 参数
     * @return 返回
     */
    public ExtTechOpenEditPO doHandlerConvertAndFormateTechOpen(Long projectId, String souType) {
        ExtTechOpenEditPO extTechOpenEditPo = new ExtTechOpenEditPO();

        //记录开标记录
        openBidRecordService.openRecord(projectId, 1, ExtOrderTypeEnum.TECH.getCode());

        ExtSouProject project = projectService.getById(projectId);
        project.setTechOpen(Enable.Y);
        project.setTechOpenTime(new Date());
        extTechOpenEditPo.setProject(project);

        //招标负责人和招标组长都已开标时修改状态
        if(openBidRecordService.isOpenByAllUser(projectId, 1, ExtOrderTypeEnum.TECH.getCode())) {
            project.setProjectStatus(SouBiddingProStatusEnum.TECH_BID_OPEN.getCode());
            //不隐藏关键信息时，自动开始评标
            if(!YesOrNo.YES.getValue().equals(project.getExtHideKeyInfo())) {
                extTechOpenEditPo.setAutoEvaTech(true);
            }
        } else {
            extNpmSouOpenTodoService.sendTodo(Collections.singletonList(project));
        }

        return extTechOpenEditPo;
    }

    @ApiOperation("技术开标后置处理")
    public void doHandlerAfterOpenTech(long projectId, String souType, ExtTechOpenEditPO po) {
        //更新实际技术开标时间
//        planService.applyAtualPoint(projectId, new Date(), ExtSouPlan::getTechOpenTime);
    }
}
