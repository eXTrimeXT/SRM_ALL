package com.midea.cloud.srm.perf.projectscoreman.contorller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.model.perf.projectscoreman.dto.ProjectScoreManDTO;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 备注
 * @author huangbf3
 */
@Api(value = "ProjectScoreManController", tags = {"项目化绩效评分主表"})
@RestController
@RequestMapping("/projectScoreMan")
public class ProjectScoreManController extends BaseController {

    @Autowired
    private ProjectScoreManService projectScoreManService;

    @ApiOperation(value = "分页查询", notes = "分页查询", httpMethod = "POST")
    @PostMapping("/listPage")
    public PageInfo<ProjectScoreMan> listPage(@RequestBody ProjectScoreMan projectScoreMan) {
        PageUtil.startPage(projectScoreMan.getPageNum(), projectScoreMan.getPageSize());
        return new PageInfo<ProjectScoreMan>(projectScoreManService.listPage(projectScoreMan));
    }

    @ApiOperation(value = "根据id获取详情", notes = "根据id获取详情")
    @GetMapping("/getDetailById")
    public ProjectScoreManDTO getDetailById(@RequestParam Long id) {
        return projectScoreManService.getDetailById(id);
    }

    @ApiOperation(value = "保存/更新", notes = "保存/更新")
    @PostMapping("/saveOrUpdateDetail")
    public Long saveOrUpdateDetail(@RequestBody ProjectScoreManDTO dto) {
        return projectScoreManService.saveOrUpdateDetail(dto);
    }

    @ApiOperation(value = "更新取消状态", notes = "更新取消状态", httpMethod = "POST")
    @PostMapping("/update")
    public void cancel(@RequestBody String contractNo){
        projectScoreManService.lambdaUpdate()
                .eq(ProjectScoreMan::getContractNo, contractNo)
                .set(ProjectScoreMan::getExtCancelStatus, 1)
                .update();
    }

}
