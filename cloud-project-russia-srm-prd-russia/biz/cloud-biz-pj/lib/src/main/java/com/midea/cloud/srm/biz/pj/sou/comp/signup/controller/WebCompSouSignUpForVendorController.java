package com.midea.cloud.srm.biz.pj.sou.comp.signup.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.comp.signup.service.CompSouSignUpEventWebService;
import com.midea.cloud.srm.biz.pj.sou.comp.signup.service.CompSouSignUpQueryWebService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpVendorDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpVendorVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 竞价 - 报名
 *
 * @author zhangwk12@midea.com
 * @since 2022/12/16
 */
@RestController
@RequestMapping("/vendor/comp/sign-up")
@Api("询价报名(供应商端)")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class WebCompSouSignUpForVendorController extends BaseController {

    @Autowired
    private CompSouSignUpQueryWebService compSouSignUpQueryService;
    @Autowired
    private CompSouSignUpEventWebService compSouSignUpEventService;

    /**
     * 查询供应商报名附件信息
     */
    @GetMapping("/getSignUpInfo/{projectId}")
    public ApiSouSignUpVendorVO listVendorSignUp(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsVendor();
        AssertUtils.notNull(projectId, "缺少projectId参数");
        return compSouSignUpQueryService.getVendorSignUpDetail(projectId, AppUserUtil.getLoginAppUser().getCompanyId());
    }

    /**
     * 供应商报名
     */
    @PostMapping("/vendorSignUp")
    public void vendorSignUp(@RequestBody ApiSouSignUpVendorDTO param) {
        SouUserTypeCheckUtils.checkIsVendor();
        param.setVendorId(AppUserUtil.getLoginAppUser().getCompanyId());
        compSouSignUpEventService.vendorSignUp(param);
    }

    /**
     * 查询招标资料
     * **/
    @GetMapping("/getSignOuter/{projectId}")
    public List<SouFile>  getSignOuter(@PathVariable("projectId") Long projectId) {
        return compSouSignUpQueryService.getSignOuter(projectId);
    }

}
