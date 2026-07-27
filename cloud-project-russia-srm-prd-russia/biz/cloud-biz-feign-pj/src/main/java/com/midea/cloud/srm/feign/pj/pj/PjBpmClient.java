package com.midea.cloud.srm.feign.pj.pj;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.feign.pj.bpm.BpmCallbackClient;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCreateResult;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/11/19 17:36:28
 *  修改内容:
 * </pre>
 */@FeignClient(value = "cloud-biz-pj", contextId = "pjBpm", path = "/api-pj")
public interface PjBpmClient extends BpmCallbackClient {


    /**
     * 发起流程
     * @param businessId
     * @param businessType
     * @param bpmParam
     * @return
     */
    @ApiOperation(value = "发起流程")
    @PostMapping("/bpmFlow/startProcessByCategoty")
    BpmResultDTO<BpmCreateResult> startProcessByCategoty(@RequestParam("businessId") Long businessId
            , @RequestParam("businessType") String businessType, @RequestBody BpmStartProcessParam bpmParam);

    /**
     * 按流程分组发起流程
     * @param requestJsn
     * @param dataId
     * @param businessType
     * @return
     */
    @ApiOperation(value = "按流程分组发起流程")
    @PostMapping("/external/bpm/public/flow/native/createProcessByCategory")
    BpmResultDTO<BpmCreateResult> createProcessByCategory(@RequestBody JSONObject requestJsn, @RequestParam("dataId") String dataId,@RequestParam("businessType") String businessType);

}
