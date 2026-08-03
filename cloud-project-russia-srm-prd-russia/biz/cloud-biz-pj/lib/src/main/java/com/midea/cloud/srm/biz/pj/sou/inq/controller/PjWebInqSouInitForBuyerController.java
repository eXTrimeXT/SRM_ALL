package com.midea.cloud.srm.biz.pj.sou.inq.controller;

import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.inq.service.PjInqSouInitEventWebService;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouInitDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
@Api(
        value = "/pj/buyer/inq/init",
        tags = {"寻源-简易询价-项目发布-采购商"}
)
@RestController
@RequestMapping({"/pj/buyer/inq/init"})
public class PjWebInqSouInitForBuyerController {

    @Autowired
    private PjInqSouInitEventWebService pjInqSouInitEventWebService;

    @PostMapping({"/editInq"})
    @ApiOperation("暂存询价单")
    public long editInq(@RequestBody ApiInqSouInitDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        AssertUtils.notNull(loginAppUser, "无当前登录人信息", new Object[0]);
        param.setCurrentUserId(loginAppUser.getUserId());
        return this.pjInqSouInitEventWebService.editInq(param);
    }
}
