package com.midea.cloud.srm.biz.pj.screen;

import io.swagger.annotations.ApiOperation;
import java.util.Map;
/**
 * @author huangbf3
 */
public interface AuthenticationScreenService {

    /**
     * 备注
     * @return
     */
    @ApiOperation("调用鉴权接口获取令牌")
    Map<String,String> findToken();


    /**
     * 备注
     * @param companyId
     * @return
     */
    @ApiOperation("筛查请求")
    String  importScreening(Long companyId);

}
