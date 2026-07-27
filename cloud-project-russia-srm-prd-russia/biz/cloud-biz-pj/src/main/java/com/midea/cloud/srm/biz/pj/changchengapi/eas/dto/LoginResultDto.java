package com.midea.cloud.srm.biz.pj.changchengapi.eas.dto;

import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.axis.client.Call;

/**
 * @author huangbf3
 */
@ApiOperation("登录结果返回对象")
@Data
@Accessors(chain = true)
public class LoginResultDto {

    private Call call;

    private WsContext wsContext;
}
