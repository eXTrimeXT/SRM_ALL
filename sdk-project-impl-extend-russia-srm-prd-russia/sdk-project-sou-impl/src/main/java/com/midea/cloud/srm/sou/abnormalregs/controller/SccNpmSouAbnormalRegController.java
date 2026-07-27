package com.midea.cloud.srm.sou.abnormalregs.controller;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/21
 */

import com.midea.cloud.srm.model.sou.abnormalregs.vo.SccNpmSouAbnormalRegVo;
import com.midea.cloud.srm.sou.abnormalregs.service.SccNpmSouAbnormalRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: for srm
 *异常登记控制层
 * @author srm
 * @date 2024-08-08
 */
@RestController
@RequestMapping("/sccNpmSouAbnormalReg")
public class SccNpmSouAbnormalRegController {
    @Autowired
    SccNpmSouAbnormalRegService sccNpmSouAbnormalRegService;

    /**
     * 更新状态为已作废，传入参数为异常报表主键id和作废说明
     */
    @PostMapping("/updateAbandon")
    public void UpdateAbandon(@RequestParam("id")Long id,@RequestParam("instruction")String instruction){
            sccNpmSouAbnormalRegService.UpdateAbandon(id,instruction);
    }
    @GetMapping("/list")
    public List<SccNpmSouAbnormalRegVo>List(){
        return sccNpmSouAbnormalRegService.List();
    }
}
