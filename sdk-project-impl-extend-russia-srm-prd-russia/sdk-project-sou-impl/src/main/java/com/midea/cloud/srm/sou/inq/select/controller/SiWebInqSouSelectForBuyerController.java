package com.midea.cloud.srm.sou.inq.select.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouStartNewRoundDTO;
import com.midea.cloud.srm.sou.inq.select.dto.ApiSiSouStartNewRoundDTO;
import com.midea.cloud.srm.sou.inq.select.service.SiInqSouSelectEventWebService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ex_liuxy46
 */
@Api(value = "/buyer/inq/select", tags = {"寻源-简易询价-供应商评选"})
@RestController
@RequestMapping("/si/buyer/inq/select")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SiWebInqSouSelectForBuyerController {

    @Autowired
    private SiInqSouSelectEventWebService inqSouSelectEventWebService;

    @PostMapping("/startNewRound")
    @ApiOperation("发起新一轮")
    public void startNewRound(@RequestBody ApiSiSouStartNewRoundDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        ApiSouStartNewRoundDTO nr = BeanUtil.copyProperties(param, ApiSouStartNewRoundDTO.class);
        String re = param.getReason();
        inqSouSelectEventWebService.startNewRound(nr, re);
    }
}
