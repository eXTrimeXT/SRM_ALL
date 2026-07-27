package com.midea.cloud.srm.sou.sourcing.spi.init;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveUser;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.approve.service.ISouApproveUserService;
import com.midea.cloud.srm.sou.sourcing.fixstatus.service.SouFixedProjectStatusService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class ApiExtSouInitQueryHandler implements ISouSpiBean {

    @Autowired
    private SouFixedProjectStatusService projectStatusService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ISouApproveUserService approveUserService;

    @Autowired
    private IExtSouDemandService demandService;

    @ApiOperation("寻源分页查询的前置处理")
    public void doHandlerBeforePageProjects(ApiExtSouProjectQueryDTO queryParam, String souType) {
        projectStatusService.fixedProjectStatusAll(souType);
    }

    @ApiOperation("寻源详情查询的前置处理")
    public void doHandlerBeforeGetProjectInfo(Long projectId, String souType) {
        ExtSouProject souProject = projectService.getById(projectId);
        projectStatusService.fixedProjectStatus(souProject, souType);
    }

    @ApiOperation("寻源分页查询的后置处理")
    public List<ExtSouProjectDto> doHandlerAfterPageProjects(ApiExtSouProjectQueryDTO queryParam, String souType, List<ExtSouProjectDto> souProjectList) {

        if(CollectionUtils.isEmpty(souProjectList)) {
            return souProjectList;
        }

        //补充审批人
        Map<Long, SouApproveUser> approveUserMap = approveUserService.getNewestApproveUserMap(souProjectList.stream().map(s -> s.getProjectId()).collect(Collectors.toList()));
        //是否部分取消
        Map<Long, String> cancleMap = partCancle(souProjectList.stream().map(s -> s.getProjectId()).collect(Collectors.toList()));
        souProjectList.stream().filter(s -> approveUserMap.containsKey(s.getProjectId())).forEach(s -> {
            SouApproveUser souApproveUser = approveUserMap.getOrDefault(s.getProjectId(), new SouApproveUser());
            s.setApproveUserId(souApproveUser.getUserId());
            s.setApproveUserName(souApproveUser.getUserName());
            s.setApproveFullName(souApproveUser.getFullName());
            s.setPartCancle(MapUtils.getString(cancleMap, s.getProjectId(), YesOrNo.NO.getValue()));
        });
        return souProjectList;
    }

    private Map<Long, String> partCancle(List<Long> projectIdList) {
        Map<Long, String> cancleMap = new HashMap<>(16);

        if(CollectionUtils.isEmpty(projectIdList)) {
            return cancleMap;
        }

        List<ExtSouDemand> demandList =  demandService.lambdaQuery().in(ExtSouDemand::getProjectId, projectIdList).groupBy(ExtSouDemand::getProjectId, ExtSouDemand::getStatus).list();

        Map<Long, List<ExtSouDemand>> demandGroup = demandList .stream().collect(Collectors.groupingBy(ExtSouDemand::getProjectId));

        projectIdList.stream().forEach(projectId -> {
            cancleMap.put(projectId, CollectionUtils.isNotEmpty(demandGroup.get(projectId)) && Integer.compare(demandGroup.get(projectId).size(), SrmConstant.NUM_ONE) == 1 ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
        });
        return cancleMap;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
