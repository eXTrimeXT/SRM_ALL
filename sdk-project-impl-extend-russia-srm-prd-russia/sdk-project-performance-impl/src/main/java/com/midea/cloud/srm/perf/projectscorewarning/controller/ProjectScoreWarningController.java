package com.midea.cloud.srm.perf.projectscorewarning.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.perf.projectscorewarning.entity.ProjectScoreWarning;
import com.midea.cloud.srm.model.perf.projectscorewarning.enums.ProjectScoreWarningStatusEnum;
import com.midea.cloud.srm.perf.projectscorewarning.service.ProjectScoreWarningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Api(value = "ProjectScoreWarningController", tags = {"供应商预警单"})
@RestController
@RequestMapping("/projectScoreWarning")
public class ProjectScoreWarningController extends BaseController {

    @Autowired
    private ProjectScoreWarningService projectScoreWarningService;

    @Autowired
    private BaseClient baseClient;


    /**
     * 分页查询供应商预警单
     *
     * @param projectScoreWarning 查询条件
     * @return 分页结果
     */
    @ApiOperation(value = "分页查询供应商预警单")
    @PostMapping("/listPage")
    public PageInfo<ProjectScoreWarning> listPage(@RequestBody ProjectScoreWarning projectScoreWarning) {
        PageUtil.startPage(projectScoreWarning.getPageNum(), projectScoreWarning.getPageSize());
        List<ProjectScoreWarning> list = projectScoreWarningService.listPage(projectScoreWarning);
        return new PageInfo<>(list);
    }

    /**
     * 保存/更新供应商预警单
     *
     * @param projectScoreWarning 供应商预警单
     * @return 保存/更新结果
     */
    @ApiOperation(value = "保存/更新供应商预警单")
    @PostMapping("/saveOrUpdateDetail")
    public Long saveOrUpdateDetail(@RequestBody ProjectScoreWarning projectScoreWarning) {
        projectScoreWarning.setWarningStatus(ProjectScoreWarningStatusEnum.DRAFT.name());
        if (StringUtils.isEmpty(projectScoreWarning.getWarningCode())) {
            projectScoreWarning.setWarningCode(baseClient.seqGen("SEQ_PROJECT_SCORE_WARNING_CODE"));
        }
        projectScoreWarningService.saveOrUpdate(projectScoreWarning);
        return projectScoreWarning.getWarningId();
    }

    /**
     * 发布
     *
     * @param projectScoreWarning 发布
     * @return 发布
     */
    @ApiOperation(value = "发布")
    @PostMapping("/publish")
    public Long publish(@RequestBody ProjectScoreWarning projectScoreWarning) {
        projectScoreWarning.setWarningStatus(ProjectScoreWarningStatusEnum.PUBLISHED.name());
        if (StringUtils.isEmpty(projectScoreWarning.getWarningCode())) {
            projectScoreWarning.setWarningCode(baseClient.seqGen("SEQ_PROJECT_SCORE_WARNING_CODE"));
        }
        projectScoreWarningService.saveOrUpdate(projectScoreWarning);
        return projectScoreWarning.getWarningId();
    }

    /**
     * 根据id查询供应商预警单
     *
     * @param warningId 供应商预警单id
     * @return 供应商预警单
     */
    @ApiOperation(value = "根据id查询供应商预警单")
    @GetMapping("/getDetailById")
    public ProjectScoreWarning getById(@RequestParam Long warningId) {
        return projectScoreWarningService.getById(warningId);
    }

    /**
     * 根据id删除供应商预警单
     *
     * @param warningId 供应商预警单id
     * @return 删除结果
     */
    @ApiOperation(value = "根据id删除供应商预警单")
    @DeleteMapping("/delete")
    public void delete(@RequestParam Long warningId) {
        projectScoreWarningService.removeById(warningId);
    }


    /**
     * 根据id查询供应商预警单-供应商端
     *
     * @param warningId 供应商预警单id
     * @return 供应商预警单
     */
    @ApiOperation(value = "根据id查询供应商预警单-供应商端")
    @GetMapping("/sup/getDetailById")
    public ProjectScoreWarning getByIdForSup(@RequestParam Long warningId) {
        projectScoreWarningService.update(Wrappers.lambdaUpdate(ProjectScoreWarning.class)
                .set(ProjectScoreWarning::getReadStatus, Enable.Y.name())
                .eq(ProjectScoreWarning::getWarningId, warningId)
        );
        return projectScoreWarningService.getById(warningId);
    }


}
