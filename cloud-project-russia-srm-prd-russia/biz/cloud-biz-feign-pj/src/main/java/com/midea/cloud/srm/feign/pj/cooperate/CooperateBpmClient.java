package com.midea.cloud.srm.feign.pj.cooperate;

import com.midea.cloud.srm.feign.pj.bpm.BpmCallbackClient;
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
@FeignClient(value = "cloud-biz-supplier-cooperate", contextId = "scBpm",path = "/api-sup-ce")
public interface CooperateBpmClient extends BpmCallbackClient {
}
