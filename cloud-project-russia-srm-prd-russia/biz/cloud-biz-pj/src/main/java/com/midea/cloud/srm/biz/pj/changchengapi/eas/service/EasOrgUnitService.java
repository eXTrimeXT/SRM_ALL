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
@ApiOperation("EAS组织接口定义")
public interface EasOrgUnitService {

    /**
     * 备注
     * @throws Exception
     */
    void  pushAllOrg() throws Exception;

    /**
     * 备注
     * @param organizations
     * @param call
     * @throws RemoteException
     */
    void pushOrgList(List<Organization> organizations, Call call) throws RemoteException;

    /**
     * 备注
     * @param organizationList
     * @throws Exception
     */
    void pushOrgListToEas(List<Organization> organizationList) throws Exception;
}
