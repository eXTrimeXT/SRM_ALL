package com.midea.cloud.srm.biz.pj.changchengapi.eas.service;

import com.midea.cloud.srm.biz.pj.changchengapi.eas.dto.LoginResultDto;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import io.swagger.annotations.ApiOperation;
import org.apache.axis.client.Call;

import java.rmi.RemoteException;
import java.util.List;

/**
 * @author huangbf3
 */
@ApiOperation("EAS接口定义")
public interface EasService {
    /**
     * 备注
     * @return
     * @throws Exception
     */
    LoginResultDto getLoginResultDto() throws Exception;

    /**
     * 获取CALL
     * @param loginResultDto
     * @return
     */
    Call getOrgCall(LoginResultDto loginResultDto);

    /**
     * 备注
     * @param loginResultDto
     * @return
     */
    Call getPersonCall(LoginResultDto loginResultDto);
}
