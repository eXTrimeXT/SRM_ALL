package com.midea.cloud.srm.biz.pj.sou.comp.signup.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.comp.signup.service.CompSouSignUpEventWebService;
import com.midea.cloud.srm.biz.pj.sou.comp.signup.service.CompSouSignUpQueryWebService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpChangeEndTimeDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpConfirmDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpVendorVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 项目式询价。报名 - 接口层
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/23
 */
@RestController
@RequestMapping("/buyer/comp/sign-up")
@Api("询价报名(采购商端)")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class WebCompSouSignUpForBuyerController extends BaseController {

    @Autowired
    private CompSouSignUpQueryWebService compSouSignUpQueryService;
    @Autowired
    private CompSouSignUpEventWebService compSouSignUpEventService;

    /**
     * 查询供应商报名信息
     */
    @PostMapping("/page")
    public PageInfo<ApiSouSignUpQueryVO> listVendorSignUp(@RequestBody ApiSouSignUpQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return compSouSignUpQueryService.listVendorSignUp(queryParam);
    }

    /**
     * 查询供应商报名详情  审查
     */
    @GetMapping("/getSignUpInfo")
    public ApiSouSignUpVendorVO getVendorSignUpDetail(Long projectId, Long vendorId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        AssertUtils.notNull(projectId, "缺少projectId参数");
        AssertUtils.notNull(vendorId, "缺少vendorId参数");
        return compSouSignUpQueryService.getVendorSignUpDetail(projectId, vendorId);
    }

    /**
     * 确认/驳回报名
     */
    @PostMapping("/confirmSignUp")
    public void confirmSignUp(@RequestBody ApiSouSignUpConfirmDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compSouSignUpEventService.confirmSignUp(param);
    }

    /**
     * 立即截止报名/延长报名时间
     */
    @PostMapping("/changeSignUpEndTime")
    public void changeSignUpEndTime(@RequestBody ApiSouSignUpChangeEndTimeDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compSouSignUpEventService.changeSignUpEndTime(param);
    }

}
