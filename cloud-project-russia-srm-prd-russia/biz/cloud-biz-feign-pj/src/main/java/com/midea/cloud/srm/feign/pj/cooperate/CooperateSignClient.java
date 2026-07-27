package com.midea.cloud.srm.feign.pj.cooperate;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.feign.pj.sign.SignCallbackClient;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
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
 */
@FeignClient(value = "cloud-biz-supplier-cooperate", contextId = "scSign",path = "/api-sup-ce")
public interface CooperateSignClient extends SignCallbackClient {


    /**
     * 获取招标计划负责人
     * @param requirementHeadId
     * @return
     */
    @ApiOperation("/获取招标计划负责人")
    @GetMapping("/npm/pr/requirement/sou/getBidFuZeRen")
    JSONObject getBidFuZeRen(@RequestParam("requirementHeadId") Long requirementHeadId);
}
