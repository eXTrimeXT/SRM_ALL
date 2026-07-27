package com.midea.cloud.srm.biz.pj.sou.comp.order.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IPUtil;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.biz.pj.sou.comp.order.service.CompSouOrderEventWebService;
import com.midea.cloud.srm.biz.pj.sou.comp.order.service.CompSouOrderQueryWebService;
import com.midea.cloud.srm.model.base.formula.vo.EssentialFactorVO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.pj.sou.comp.dto.webapi.order.CompSouVendorViewOrderDetailQueryWebDTO;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order.CompSouOrderDetailWebVO;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order.CompSouOrderItemWebVO;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order.CompSouVendorViewOrderDetailsWebVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.order.ApiCompSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouInitProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderResultQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouOrderSignUpInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 竞价 - 供应商报价
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@RestController
@RequestMapping("/vendor/comp/order")
@Api(tags = {"寻源-简易询价-供应商报价(供应商端)"})
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class WebCompSouOrderForVendorController {

    @Autowired
    private CompSouOrderQueryWebService compOrderQueryService;
    @Autowired
    private CompSouOrderEventWebService compOrderEventService;

    @PostMapping("/pageOrder")
    @ApiOperation("查询询价单列表")
    public PageInfo<ApiCompSouOrderQueryVO> pageCompOrders(@RequestBody ApiSouOrderQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsVendor();
        queryParam.setVendorId(AppUserUtil.getLoginAppUser().getCompanyId());
        return new PageInfo<>(compOrderQueryService.listCompOrders(queryParam));
    }

    @GetMapping("/projectInfo/{projectId}")
    @ApiOperation("查看项目信息")
    public ApiCompSouInitProjectVO getCompProjectInfo(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsVendor();
        return compOrderQueryService.getCompProjectInfo(projectId, AppUserUtil.getLoginAppUser().getCompanyId());
    }

    @GetMapping("/requireInfo/{projectId}")
    @ApiOperation("查看项目需求")
    public List<ApiCompSouItemVO> getCompRequireInfo(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsVendor();
        return compOrderQueryService.getCompRequireInfo(projectId, AppUserUtil.getLoginAppUser().getCompanyId());
    }

    @GetMapping("/signUpInfo/{projectId}")
    @ApiOperation("查看报名信息")
    public ApiSouOrderSignUpInfoVO getCompSignUpInfo(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsVendor();
        return compOrderQueryService.getCompSignUpInfo(projectId, AppUserUtil.getLoginAppUser().getCompanyId());
    }

    @PostMapping("/getOrderDetails")
    @ApiOperation("查看投标明细")
    public CompSouVendorViewOrderDetailsWebVO getOrderDetails(@RequestBody CompSouVendorViewOrderDetailQueryWebDTO queryParam) {
        SouUserTypeCheckUtils.checkIsVendor();
        return compOrderQueryService.getOrderDetails(queryParam, AppUserUtil.getLoginAppUser().getCompanyId());
    }

    @PostMapping("/pageOrderResult")
    @ApiOperation("查看结果")
    public PageInfo<CompSouOrderItemWebVO> pageOrderResult(@RequestBody ApiSouOrderResultQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsVendor();
        queryParam.setVendorId(AppUserUtil.getLoginAppUser().getCompanyId());
        return new PageInfo<>(compOrderQueryService.listOrderResult(queryParam, false));
    }

    @GetMapping("/getOrderInfo/{projectId}")
    @ApiOperation("查询询价单详情(用于报价)")
    public CompSouOrderDetailWebVO getCompSouOrderInfo(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsVendor();
        return compOrderQueryService.getCompSouOrderInfo(projectId, AppUserUtil.getLoginAppUser().getCompanyId(), false);
    }

    @GetMapping("/getOrderFormulaPrices")
    @ApiOperation("单独的查看公式报价的接口")
    public List<EssentialFactorVO> getOrderFormulaPrices(Long souItemId, @Nullable Long orderItemId, String currencyCode) {
        SouUserTypeCheckUtils.checkIsVendor();
        AssertUtils.notNull(souItemId, "缺少souItemId参数");
        currencyCode = StringUtils.trimToNull(currencyCode);
        AssertUtils.notNull(currencyCode, "缺少currencyCode参数");
        return compOrderQueryService.getOrderFormulaPrices(souItemId, orderItemId, currencyCode, AppUserUtil.getLoginAppUser().getCompanyId(), false);
    }

    @GetMapping("/computeFormulaPrice")
    @ApiOperation("计算公式报价")
    public ApiCompSouOrderItemVO computeFormulaPrice(Long souItemId, String currency, String taxKey,
                                                     String formulaValue) {
        SouUserTypeCheckUtils.checkIsVendor();
        AssertUtils.notNull(souItemId, "缺少souItemId参数");
        currency = StringUtils.trimToNull(currency);
        AssertUtils.notNull(currency, "缺少currency参数");
        taxKey = StringUtils.trimToNull(taxKey);
        AssertUtils.notNull(taxKey, "缺少taxKey参数");
        formulaValue = StringUtils.trimToNull(formulaValue);
        AssertUtils.notNull(formulaValue, "缺少formulaValue参数");
        return compOrderEventService.computeFormulaPrice(souItemId, currency, taxKey, formulaValue,
                AppUserUtil.getLoginAppUser().getCompanyId());
    }

    @PostMapping("/editOrder")
    @ApiOperation("暂存/提交报价")
    public long/* orderId */ editOrder(@RequestBody ApiCompSouOrderDTO param) {
        SouUserTypeCheckUtils.checkIsVendor();
        param.setVendorId(AppUserUtil.getLoginAppUser().getCompanyId());
        param.setIsProxy(Enable.N);
        if (!param.isTempSave()) {
            LoginAppUser appUser = AppUserUtil.getLoginAppUser();
            param.setSubmitById(appUser.getUserId());
            param.setSubmitBy(appUser.getUsername());
            param.setSubmitByIp(IPUtil.getRemoteIpAddr(HttpServletHolder.getRequest()));
            param.setSubmitFullName(appUser.getNickname());
        }
        return compOrderEventService.editOrder(param);
    }

}
