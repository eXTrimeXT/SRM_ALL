package com.midea.cloud.srm.biz.pj.sou.comp.init.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.comp.init.service.CompSouInitEventWebService;
import com.midea.cloud.srm.biz.pj.sou.comp.init.service.CompSouInitQueryWebService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.*;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouInitProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouCancelDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pm.pr.requirement.dto.RequirementManageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 竞价.询价信息 - 接口层
 *
 * @author ex_yipeng@partner.midea.com
 * @since 2023/09/25
 */
@RestController
@RequestMapping("/buyer/comp/init")
@Api(tags = "项目式询价立项(采购商端)")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class WebCompSouInitForBuyerController extends BaseController {

    @Autowired
    private CompSouInitQueryWebService compInitQueryService;
    @Autowired
    private CompSouInitEventWebService compInitEventService;

    /**
     * 询价项目管理-分页查询
     */
    @PostMapping("/page")
    @ApiOperation("分页查询询价项目信息")
    public PageInfo<ApiCompSouProjectVO> pageProjects(@RequestBody ApiSouProjectQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return new PageInfo<>(compInitQueryService.listProject(queryParam));
    }

    /**
     * 询价立项 - 项目信息【查询】
     */
    @GetMapping("/getProjectInfo/{projectId}")
    @ApiOperation("查询项目信息")
    public ApiCompSouInitProjectVO getProjectInfo(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return compInitQueryService.getProjectInfo(projectId);
    }

    /**
     * 询价立项 - 项目信息【编辑/提交】
     */
    @PostMapping("/editProjectInfo")
    @ApiModelProperty("暂存/提交项目信息")
    public long/* projectId */ editProjectInfo(@RequestBody ApiCompSouProjectInfoDTO params) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return compInitEventService.editProjectInfo(params, false);
    }

    /**
     * 竞价立项 - 需求池拟定生成竞价单
     */
    @PostMapping("/editSouBidInfo")
    @ApiModelProperty("需求池拟定生成竞价单")
    public SouProject editSouBidInfo(@RequestBody Map<String, Object> params) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return compInitEventService.editSouBidInfo(params);
    }


    @PostMapping("/tempSaveCompFromReq")
    @ApiOperation("暂存询价单信息(该接口仅用于需求池转询价使用)")
    public SouProject tempSaveCompFromReq(@RequestBody List<RequirementManageDTO> reqParams) {
        AssertUtils.notEmpty(reqParams, "缺少需求池数据");
        return compInitEventService.tempSaveCompFromReq(reqParams);
    }

    /**
     * 询价立项 - 项目需求【查询】
     */
    @GetMapping("/getRequireInfo/{projectId}")
    @ApiOperation("查询项目需求")
    public List<ApiCompSouItemVO> getRequireInfo(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return compInitQueryService.getRequireInfo(projectId);
    }

    /**
     * 询价立项 - 项目需求【编辑/提交】
     */
    @PostMapping("/editRequireInfo")
    @ApiOperation("暂存/提交项目需求")
    public List<ApiCompSouItemVO> editRequireInfo(@RequestBody ApiCompSouRequireInfoDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        Long currentUserId = AppUserUtil.getLoginAppUser() != null ? AppUserUtil.getLoginAppUser().getUserId() : null;
        compInitEventService.editRequireInfo(param, false, currentUserId);
        return this.getRequireInfo(param.getProjectId());
    }

    /**
     * 询价立项 - 邀请供应商【查询】
     */
    @GetMapping("/getInviteSupplier/{projectId}")
    @ApiOperation("查询邀请供应商信息")
    public List<ApiSouVendorVO> getInviteSupplier(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return compInitQueryService.getInviteSupplier(projectId);
    }

    /**
     * 询价立项 - 邀请供应商【编辑/提交】
     */
    @PostMapping("/editInviteSupplier")
    @ApiOperation("暂存/提交邀请供应商信息")
    public List<ApiSouVendorVO> editInviteSupplier(@RequestBody ApiCompSouVendorInfoDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compInitEventService.editInviteSupplier(param, false);
        return this.getInviteSupplier(param.getProjectId());
    }

    /**
     * 询价立项 - 评分规则【编辑/提交】
     */
    @PostMapping("/editScoreRule")
    @ApiOperation("暂存/提交评分规则")
    public void editScoreRule(@RequestBody ApiCompSouScoreInfoDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compInitEventService.editScoreRule(param);
    }

    /**
     * 删除询价单
     */
    @DeleteMapping("/remove/{projectId}")
    @ApiOperation("删除询价单")
    public void removeComp(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compInitEventService.removeComp(projectId);
    }

    /**
     * 作废询价单
     */
    @PostMapping("/cancel")
    @ApiOperation("废弃询价单")
    public void cancelComp(@RequestBody ApiSouCancelDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compInitEventService.cancelComp(param);
    }

}
