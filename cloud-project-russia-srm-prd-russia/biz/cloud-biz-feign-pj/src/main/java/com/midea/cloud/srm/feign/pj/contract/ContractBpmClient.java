package com.midea.cloud.srm.feign.pj.contract;

import com.midea.cloud.srm.feign.pj.bpm.BpmCallbackClient;
import com.midea.cloud.srm.model.pj.changchengapi.sign.vo.ContractPartnerVo;
import feign.RequestLine;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
@FeignClient(value = "cloud-biz-contract", contextId = "contractBpm",path = "/api-cm")
public interface ContractBpmClient extends BpmCallbackClient {
    @GetMapping("contractInterface/ext/test")
    public void test();
    @PostMapping("contractInterface/ext/updateStampState")
    public void updateStampState(@RequestParam("contractId") Long contractId,
                                 @RequestParam("tenantName") String tenantName);
    @GetMapping("contractInterface/ext/getById/{id}")
    public List<ContractPartnerVo> getById(@PathVariable("id") Long id);
    @PostMapping("contractInterface/ext/sendDingDing")
    public void sendDingDing(@RequestParam("contractId") Long contractId,@RequestParam("extEmployeeNumber") String extEmployeeNumber);
}
