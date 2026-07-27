package com.midea.cloud.srm.feign.pj.sign;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 业务单据需要实现的契约锁回调接口，由回调入口动态调用
 * @author huangbf3
 *
 */
public interface SignCallbackClient {

	/**
	 * 契约锁回调，需要各个模块实现本模块下的各个功能的动态调用service
	 * @param serviceBean
	 * @param callbackMethod
	 * @param businessId
	 * @param param
	 * @param fileuploads
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/external/sign/callbackSign")
	String callbackSign(@RequestParam("serviceBean") String serviceBean,
						@RequestParam("callbackMethod") String callbackMethod,
						@RequestParam("businessId") Long businessId ,
						@RequestParam("param")String param,
						@RequestBody List<Fileupload> fileuploads
						) throws Exception;

}
