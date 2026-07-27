package com.midea.cloud.srm.feign.pj.bpm;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 业务单据需要实现的BPM回调接口，由回调入口动态调用
 * @author huangbf3
 *
 */
public interface BpmCallbackClient {
	/**
	 * getDataPushFlow
	 * @param serviceBean
	 * @param businessId
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/ext/bpm/getDataPushFlow")
	public JSONObject getDataPushFlow(@RequestParam("serviceBean") String serviceBean, @RequestParam("businessId")Long businessId) throws Exception;

}
