package com.midea.cloud.srm.biz.pj.changchengapi.bpm.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmNewFlagService;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import io.seata.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangbf3
 * 新BPM审批流标志
 */
@Slf4j
@RestController
@RequestMapping("/bpmFlow/bpmNewFlag")
public class BpmNewFlagController {
    @Autowired
    private IBpmNewFlagService iBpmNewFlagService;

    @PostMapping("/get")
    public BpmNewFlag get(@RequestBody BpmNewFlag bpmNewFlag){
        return iBpmNewFlagService.getOne(Wrappers.lambdaQuery(bpmNewFlag));
    }

    @PostMapping("/saveOrUpdate")
    public void saveOrUpdate(@RequestBody BpmNewFlag bpmNewFlag){
        BpmNewFlag dbBpmNewFlag = iBpmNewFlagService.lambdaQuery()
                .eq(BpmNewFlag::getBusinessId,bpmNewFlag.getBusinessId())
                .eq(StringUtils.isNotBlank(bpmNewFlag.getBussinessType()),BpmNewFlag::getBussinessType,bpmNewFlag.getBussinessType())
                .one();
        if(dbBpmNewFlag==null){
            bpmNewFlag.setBpmNewFlagId(IdGenrator.generate());
            bpmNewFlag.setNewBpmFlag(YesOrNo.YES.getValue());
            iBpmNewFlagService.save(bpmNewFlag);
        }
    }
}