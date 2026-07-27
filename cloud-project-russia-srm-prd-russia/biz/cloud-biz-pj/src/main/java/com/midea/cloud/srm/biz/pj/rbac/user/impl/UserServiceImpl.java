package com.midea.cloud.srm.biz.pj.rbac.user.impl;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.biz.pj.rbac.user.IUserService;
import com.midea.cloud.srm.biz.pj.rbac.user.mapper.UserMapper;
import com.midea.cloud.srm.feign.pj.rbac.RbacExtClient;
import com.midea.cloud.srm.model.pj.changchengapi.dto.UserApiDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huangbf3
 * 人员业务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private RbacExtClient rbacExtClient;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void saveOrUpdateUserBatch(List<UserApiDTO> userApiDTOList) {
        //查询用户
//        rbacExtClient.listByBuyer(null);
        //新增用户
//        rbacExtClient.addBuyer(null);
        //更新用户
//        rbacExtClient.modifyBuyer(null);
    }

    @Override
    public void updateUserCompanyName() {
        String username = AppUserUtil.getLoginAppUser().getUsername();
        log.info("updateUserCompanyName username="+username);
        userMapper.updateUserCompanyName(username);
    }
}
