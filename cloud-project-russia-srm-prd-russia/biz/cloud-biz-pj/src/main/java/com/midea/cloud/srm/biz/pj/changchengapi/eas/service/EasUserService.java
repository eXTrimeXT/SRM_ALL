package com.midea.cloud.srm.biz.pj.changchengapi.eas.service;

import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import io.swagger.annotations.ApiOperation;
import org.apache.axis.client.Call;

import java.rmi.RemoteException;
import java.util.List;

/**
 * @author huangbf3
 */
@ApiOperation("EAS用户接口定义")
public interface EasUserService {
    /**
     * 备注
     * @throws Exception
     */
    void pushAllUser() throws Exception;

    /**
     * 备注
     * @param sccPjUsers
     * @param call
     * @throws RemoteException
     */
    void pushUserList(List<SccPjUser> sccPjUsers, Call call) throws RemoteException;

    /**
     * 备注
     * @param sccPjUsers
     * @throws Exception
     */
    void pushUserListToEas(List<SccPjUser> sccPjUsers) throws Exception;

    /**
     * 备注
     * @param u
     * @throws Exception
     */
    void pushRbacUser(User u) throws Exception;
}
