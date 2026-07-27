package com.midea.cloud.srm.feign.pj.contract;

import com.midea.cloud.srm.feign.pj.sign.SignCallbackClient;
import org.springframework.cloud.openfeign.FeignClient;

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
@FeignClient(value = "cloud-biz-contract", contextId = "contractSign",path = "/api-cm")
public interface ContractSignClient extends SignCallbackClient {

}
