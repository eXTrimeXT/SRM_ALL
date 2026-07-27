package com.midea.cloud.srm.biz.pj.changchengapi.bpm.controller;


import cn.hutool.core.lang.Assert;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.BusinessCallBpmService;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmFlowList;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author huangbf3
 */
@Slf4j
@RestController
@RequestMapping("/external/business")
public class BusinessCallBpmController {

    @Autowired
    BusinessCallBpmService businessCallBpmService;



    @ApiOperation(value = "页面调用发起人撤回流程接口")
    @PostMapping("/public/business/rollBackProcess")
    public BpmResultDTO rollBackProcess(@RequestParam("businessId") Long businessId, @RequestParam("businessType") String businessType,@RequestParam("commentmsg") String commentmsg) {

        Assert.isTrue(businessType!=null,"业务单据类型不能为空");
        Assert.isTrue(businessId!=null,"业务单据id不能为空");
        Assert.isTrue(commentmsg!=null,"业务单据撤销原因不能为空");

        // 根据单据id和模板查询该单据的流程实例最近的一条
        return businessCallBpmService.rollBackProcess(businessId,businessType,commentmsg);

    }


    @ApiOperation(value = "页面查询流程审批记录接口")
    @PostMapping("/public/business/approvalRecord")
    public BpmResultDTO<List<BpmFlowList>> approvalRecord(@RequestParam("businessId") Long businessId, @RequestParam("businessType") String businessType) {

        Assert.isTrue(businessType!=null,"业务单据类型不能为空");
        Assert.isTrue(businessId!=null,"业务单据id不能为空");

        // 根据单据id和模板查询该单据的流程实例最近的一条
        return businessCallBpmService.approvalRecord(businessId,businessType);

    }


}
