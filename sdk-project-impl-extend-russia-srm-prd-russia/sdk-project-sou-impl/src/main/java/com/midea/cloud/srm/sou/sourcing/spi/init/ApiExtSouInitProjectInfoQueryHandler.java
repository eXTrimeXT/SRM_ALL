package com.midea.cloud.srm.sou.sourcing.spi.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.model.bid.purchaser.bidprocessconfig.entity.ProcessNode;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveOperate;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveUser;
import com.midea.cloud.srm.model.sou.enums.SouBidProccessEnum;
import com.midea.cloud.srm.model.sou.enums.SouRecommvendorStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiExtSouProcessConfigVo;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreHead;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.approve.service.ISouApproveOperateService;
import com.midea.cloud.srm.sou.approve.service.ISouApproveUserService;
import com.midea.cloud.srm.sou.sourcing.fixstatus.service.SouFixedProjectStatusService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProcessConfigService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouTechScoreHeadService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
@Api("寻源核心-查询基本信息-行业包")
public class ApiExtSouInitProjectInfoQueryHandler implements ISouSpiBean {

    @Autowired
    private ISouApproveUserService approveUserService;

    @Autowired
    private ISouApproveOperateService approveOperateService;

    @Autowired
    private SouFixedProjectStatusService fixedProjectStatusService;

    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private QlService qlService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouTechScoreHeadService techScoreHeadService;

    @Autowired
    private IExtSouProcessConfigService processConfigService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @ApiOperation("查询寻源单据行业包后置处理")
    public void doHandlerAfterQueryProjectInfo(ApiExtSouProjectInfoDTO param, String souType) {
        if(Objects.isNull(param) || Objects.isNull(param.getProject())) {
            return;
        }


        //获取审批人
        SouApproveUser approveUser = approveUserService.getNewestApproveUser(param.getProject().getProjectId());
        if(!Objects.isNull(approveUser)) {
            param.getProject().setApproveUserId(approveUser.getUserId());
            param.getProject().setApproveUserName(approveUser.getUserName());
            param.getProject().setApproveFullName(approveUser.getFullName());

            SouApproveOperate approveOperate = approveOperateService.getNewestOperate(approveUser.getApproveUserId());
            if(!Objects.isNull(approveOperate) && ApproveStatusType.REJECTED.getValue().equals(approveOperate.getOperate())) {
                param.getProject().setApproveRejectDesc(approveOperate.getDescrption());
            }
        }

        //询比价招标不需要技术标、评分规则、技术标管理
        List<SouProcessNodeEnum> removeNodeList = new ArrayList<>();
        if(SouBidProccessEnum.INQUIRY.getCode().equals(param.getProject().getExtSouProcess()) && !Objects.isNull(param.getProject().getProcessConfig())) {
            removeNodeList.add(SouProcessNodeEnum.scoreRule);
            removeNodeList.add(SouProcessNodeEnum.techManagement);
        }
        //不需要保证金
        if(!YesOrNo.YES.getValue().equals(param.getProject().getExtEarnestFlag())) {
            removeNodeList.add(SouProcessNodeEnum.bondManagement);
        }

        //修正节点
        processConfigService.fixNpmProcessAndNode(param.getProject());

        //返回供应商推荐单号
        queryRecommvendorNo(param.getProject().getProjectId(), param.getProject().getApplicantNo(), param.getProject());

        //是否已确认
        Integer confirmCount = Math.toIntExact(techScoreHeadService.lambdaQuery().eq(ExtSouTechScoreHead::getProjectId, param.getProject().getProjectId())
                .eq(ExtSouTechScoreHead::getExtConfirmFlag, YesOrNo.YES.getValue()).count());
        if(Integer.compare(confirmCount, 0) == 1) {
            param.getProject().setExtConfirmFlag(YesOrNo.YES.getValue());
        } else {
            param.getProject().setExtConfirmFlag(YesOrNo.NO.getValue());
        }
    }

    private void queryRecommvendorNo(Long projectId, String applicationNo, ExtSouProjectDto projectDto) {

        if(StringUtils.isBlank(applicationNo)) {
            return;
        }

        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExtSouDemand::getApplicantNo, Arrays.stream(applicationNo.split(";")).collect(Collectors.toList()));
        queryWrapper.ne(ExtSouDemand::getProjectId, projectId);
        queryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);

        List<ExtSouDemand> demandList = demandService.list(queryWrapper);

        if(CollectionUtils.isEmpty(demandList)) {
            return;
        }

        //查询供应商推荐单
        LambdaQueryWrapper<ExtSouProject> projectQuery = new LambdaQueryWrapper<>();
        projectQuery.in(ExtSouProject::getProjectId, demandList.stream().map(ExtSouDemand::getProjectId).distinct().collect(Collectors.toList()));

        projectQuery.eq(ExtSouProject::getSouType, SouTypeEnum.recomm.name());
        projectQuery.eq(ExtSouProject::getProjectStatus, SouRecommvendorStatusEnum.APPROVED.getCode());
        projectQuery.orderByDesc(ExtSouProject::getCreationDate);

        List<ExtSouProject> projectList = projectService.list(projectQuery);

        if(CollectionUtils.isEmpty(projectList)) {
            return;
        }

        List<String> recommvendorNoList = new ArrayList<>();
        List<String> recommvendorIdList = new ArrayList<>();
        projectList.stream().forEach(p -> {
            recommvendorNoList.add(p.getSouNo());
            recommvendorIdList.add(p.getProjectId().toString());
        });

        projectDto.setExtRecommendNo(recommvendorNoList.stream().collect(Collectors.joining(",")));
        projectDto.setExtRecommendId(recommvendorIdList.stream().collect(Collectors.joining(",")));
    }


}
