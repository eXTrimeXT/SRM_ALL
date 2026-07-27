package com.midea.cloud.srm.model.pj.sign.service;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author huangbf3
 */
@ApiModel("契约锁状态回调更新业务逻辑，由回调入口动态调用")
public interface ISignCallbackService {
	/**
	 * 备注
	 * @param businessId
	 * @param param
	 * @param fileuploads
	 * @throws Exception
	 */
	@ApiModelProperty("契约锁状态回调状态：正常结束，触发业务的动作")
	public void complete(Long businessId, String param,List<Fileupload> fileuploads) throws Exception;
	
	
}
