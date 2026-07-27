package com.midea.cloud.srm.feign.pj.pj;

import com.alibaba.fastjson.JSONArray;
import com.midea.cloud.srm.feign.pj.sign.SignCallbackClient;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCommitTaskVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
 */
@FeignClient(value = "cloud-biz-pj", contextId = "pjSign", path = "/api-pj")
public interface PjSignClient extends SignCallbackClient {

    /**
     * predict
     * @param commitTaskVo
     * @return
     * @throws Exception
     */
    @PostMapping("/bpmFlow/predict")
    JSONArray predict(@RequestBody BpmCommitTaskVo commitTaskVo) throws Exception;
}
