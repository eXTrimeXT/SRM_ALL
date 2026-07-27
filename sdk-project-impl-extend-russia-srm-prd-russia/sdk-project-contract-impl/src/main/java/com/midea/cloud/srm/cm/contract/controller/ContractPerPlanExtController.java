package com.midea.cloud.srm.cm.contract.controller;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.cm.contract.model.dto.MilestoneHasCreatePefDto;
import com.midea.cloud.srm.cm.contract.service.IContractPerPlanExtService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.contract.dto.PerPlanExt;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author 100014336 ganyh19
 * 履约计划
 */
@RestController
@RequestMapping("perPlan/ext/")
public class ContractPerPlanExtController extends BaseController {

    @Autowired
    private IContractPerPlanExtService contractPerPlanExtService;

    @ApiOperation("是否需要评分")
    @GetMapping("/isNeedPerfEvalByMilestoneId")
    public Boolean isNeedPerfEvalByMilestoneId(@RequestParam("milestoneId") Long milestoneId){
        return contractPerPlanExtService.isNeedPerfEvalByMilestoneId(milestoneId);
    }

    @ApiOperation("写入是否已经创建项目")
    @PostMapping("/setHasCreatePerf")
    public List<Serializable> setHasCreatePerf(@RequestBody MilestoneHasCreatePefDto milestoneHasCreatePefDto){
        if(ObjectUtil.isEmpty(milestoneHasCreatePefDto.getContractNo())){
            throw new BaseException("合同编号不能为空");
        }
        if(ObjectUtil.isEmpty(milestoneHasCreatePefDto.getMilestoneType())){
            throw new BaseException("里程碑类型不能为空");
        }
        return contractPerPlanExtService.setHasCreatePerf(milestoneHasCreatePefDto.getContractNo(), milestoneHasCreatePefDto.getMilestoneType(), milestoneHasCreatePefDto.getEnable());
    }

    @ApiOperation("取消")
    @PostMapping("/cancel")
    public void cancel(@RequestBody PerPlanExt perPlanExt){
        contractPerPlanExtService.cancel(perPlanExt);
    }

}
