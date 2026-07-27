package com.midea.cloud.srm.perf.projectscoreitem.controller;

import com.esotericsoftware.minlog.Log;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.cm.contract.model.dto.MilestoneHasCreatePefDto;
import com.midea.cloud.srm.feign.ContractExtClient;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.model.perf.projectscoreitem.dto.ProjectScoreItemsDTO;
import com.midea.cloud.srm.model.perf.projectscoreitem.dto.ProjectScoreItemsQueryDTO;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItemsPerson;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManDetail;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManRejectInfo;
import com.midea.cloud.srm.model.perf.projectscoreman.enums.ProjectScoreItemStatusEnum;
import com.midea.cloud.srm.perf.projectscoreitem.service.ProjectScoreItemsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Api(value = "ProjectScoreItemsController", tags = {"项目化绩效项目主表"})
@RestController
@RequestMapping("/pj/projectScoreItems")
public class ProjectScoreItemsController extends BaseController {

    @Autowired
    private ProjectScoreItemsService projectScoreItemsService;

    @Autowired
    private ContractExtClient contractExtClient;

    @ApiOperation(value = "分页查询", notes = "分页查询", httpMethod = "POST")
    @PostMapping("/listPage")
    public PageInfo<ProjectScoreItems> listPage(@RequestBody ProjectScoreItemsQueryDTO queryDTO) {
        PageUtil.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        return new PageInfo<ProjectScoreItems>(projectScoreItemsService.listPage(queryDTO));
    }

    @ApiOperation(value = "根据id获取详情", notes = "根据id获取详情")
    @GetMapping("/getDetailById")
    public ProjectScoreItemsDTO getDetailById(@RequestParam Long projectScoreItemsId) {
        return projectScoreItemsService.getDetailById(projectScoreItemsId);
    }

    @ApiOperation(value = "保存/更新", notes = "保存/更新", httpMethod = "POST")
    @PostMapping("/saveOrUpdate")
    public Long saveOrUpdate(@RequestBody ProjectScoreItemsDTO dto) {
        Long id = projectScoreItemsService.saveOrUpdateDetail(dto);
        // 更新合同履约信息
        try {
            MilestoneHasCreatePefDto milestoneHasCreatePefDto = new MilestoneHasCreatePefDto();
            milestoneHasCreatePefDto.setContractNo(dto.getContractNo());
            milestoneHasCreatePefDto.setMilestoneType(dto.getPerformanceCode());
            milestoneHasCreatePefDto.setEnable(Enable.Y);
            contractExtClient.setHasCreatePerf(milestoneHasCreatePefDto);
        } catch (Exception e) {
            Log.error("更新合同履约信息失败");
            Log.error("更新合同履约信息失败:" + e);
            throw new BaseException("更新合同履约信息失败.");
        }
        return id;
    }

    @ApiOperation(value = "通知评分人", notes = "通知评分人", httpMethod = "POST")
    @PostMapping("/notifyScorers")
    public void notifyScorers(@RequestBody ProjectScoreItemsDTO dto) {
        Long projectScoreItemsId = dto.getProjectScoreItemsId();
        projectScoreItemsService.notifyScorers(projectScoreItemsId);
    }


    @ApiOperation(value = "复核驳回", notes = "复核驳回", httpMethod = "POST")
    @PostMapping("/reject")
    public void reject(@RequestBody ProjectScoreItemsDTO dto) {
        projectScoreItemsService.reject(dto);
    }

    @ApiOperation(value = "计算得分", notes = "计算得分")
    @GetMapping("/calcScore")
    public void calcScore(@RequestParam Long projectScoreItemsId) {
        projectScoreItemsService.calcScore(projectScoreItemsId);
    }

    @ApiOperation(value = "分页查询-复核", notes = "分页查询-复核", httpMethod = "POST")
    @PostMapping("/listPageForCheck")
    public PageInfo<ProjectScoreItems> listPageForCheck(@RequestBody ProjectScoreItemsQueryDTO queryDTO) {
        PageUtil.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        queryDTO.setProjectStatusList(Arrays.asList(ProjectScoreItemStatusEnum.SCORE_NOTIFIED.name()
        ,ProjectScoreItemStatusEnum.WITHOUT_CHECK.name()
        ,ProjectScoreItemStatusEnum.CALCULATED_SCORE.name()));
        return new PageInfo<ProjectScoreItems>(projectScoreItemsService.listPage(queryDTO));
    }

    @ApiOperation(value = "根据条件,获取绩效复核打分明细", notes = "根据条件,获取绩效复核打分明细-复核", httpMethod = "POST")
    @PostMapping("/listScoreManDetailList")
    public List<ProjectScoreManDetail> listScoreManDetailList(@RequestBody ProjectScoreMan projectScoreMan) {
        return projectScoreItemsService.listScoreManDetailList(projectScoreMan);
    }

    @ApiOperation(value = "根据合同编码获取项目化绩效项目主信息", notes = "根据合同编码获取项目化绩效项目主信息", httpMethod = "POST")
    @PostMapping("/getInfoByContractNo")
    public ProjectScoreItems getInfoByContractNo(@RequestBody ContractHead contractHead) {
        return projectScoreItemsService.getInfoByContractNo(contractHead);
    }

    @ApiOperation(value = "获取评分人驳回信息", notes = "获取评分人驳回信息", httpMethod = "GET")
    @GetMapping("/listRejectInfo/{projectScoreManId}")
    public List<ProjectScoreManRejectInfo> listScoreManDetailList(@PathVariable("projectScoreManId") Long projectScoreManId) {
        ProjectScoreItemsPerson projectScoreItemsPerson = new ProjectScoreItemsPerson();
        projectScoreItemsPerson.setScoreManId(projectScoreManId);
        return projectScoreItemsService.queryProjectScoreManRejectInfo(projectScoreItemsPerson);
    }


    @ApiOperation(value = "更新信息", notes = "更新信息", httpMethod = "POST")
    @PostMapping("/update")
    public void update(@RequestBody String contractNo){
        projectScoreItemsService.lambdaUpdate()
                .eq(ProjectScoreItems::getContractNo, contractNo)
                .set(ProjectScoreItems::getExtCancelStatus, 1)
                .update();
    }

}
