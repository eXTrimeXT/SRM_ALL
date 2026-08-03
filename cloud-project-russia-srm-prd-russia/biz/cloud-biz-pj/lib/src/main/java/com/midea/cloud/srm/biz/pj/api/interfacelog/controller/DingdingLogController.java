package com.midea.cloud.srm.biz.pj.api.interfacelog.controller;

import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IDingdingLogService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.api.interfacelog.entity.DingdingLog;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
*  <pre>
 *  接口日志表 前端控制器
 * </pre>
*
* @author kuangzm@meicloud.com
* @version 1.00.00
*
*  <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-05-28 10:58:43
 *  修改内容:
 * </pre>
*/
@RestController
@RequestMapping("/external/dingDingLog")
public class DingdingLogController extends BaseController {

    @Autowired
    IDingdingLogService iDingdingLogService;

    @ApiOperation("新增")
    @PostMapping("/saveDingDing")
    public void saveDingDing(@RequestBody DingdingLog dingdingLog) {
        dingdingLog.setDingdingLogId(IdGenrator.generate());
        iDingdingLogService.save(dingdingLog);
        dingdingLog.setReturnStr("{}");
        iDingdingLogService.updateById(dingdingLog);
    }

 
}
