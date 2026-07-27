package com.midea.cloud.srm.perf.projectscore.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.perf.projectscore.dto.ProjectScoreHeaderDTO;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.model.perf.projectscoreitem.dto.ProjectScoreItemsDTO;
import com.midea.cloud.srm.model.perf.projectscoreitem.dto.ProjectScoreItemsQueryDTO;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.perf.projectscore.service.ProjectScoreHeaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 备注
 * @author huangbf3
 */
@Api(value = "ProjectScoreHeaderController", tags = {"项目化绩效评分结果-头表"})
@RestController
@RequestMapping("/projectScoreHeader")
public class ProjectScoreHeaderController extends BaseController {

    @Autowired
    private ProjectScoreHeaderService projectScoreHeaderService;

    @ApiOperation(value = "分页查询", notes = "分页查询", httpMethod = "POST")
    @PostMapping("/listPage")
    public PageInfo<ProjectScoreHeader> listPage(@RequestBody ProjectScoreHeader queryDTO) {
        PageUtil.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        return new PageInfo<ProjectScoreHeader>(projectScoreHeaderService.listPage(queryDTO));
    }

    @ApiOperation(value = "根据id获取详情", notes = "根据id获取详情")
    @GetMapping("/getDetailById")
    public ProjectScoreHeaderDTO getDetailById(@RequestParam Long scoreHeaderId) {
        return projectScoreHeaderService.getDetailById(scoreHeaderId);
    }

    @ApiOperation(value = "供应商处理", notes = "供应商处理", httpMethod = "POST")
    @PostMapping("/supplierReply")
    public void supplierReply(@RequestBody ProjectScoreHeader header) {
        projectScoreHeaderService.update(Wrappers.lambdaUpdate(ProjectScoreHeader.class)
                .eq(ProjectScoreHeader::getScoreHeaderId,header.getScoreHeaderId())
                .set(ProjectScoreHeader::getSupplierRemark,header.getSupplierRemark()));
    }

    @ApiOperation(value = "更新信息", notes = "更新信息", httpMethod = "POST")
    @PostMapping("/update")
    public void update(@RequestBody String contractNo){
        projectScoreHeaderService.lambdaUpdate()
                .eq(ProjectScoreHeader::getContractNo, contractNo)
                .set(ProjectScoreHeader::getExtCancelStatus, 1)
                .update();
    }

}