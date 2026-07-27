package com.midea.cloud.srm.biz.pj.rbac.user;


import com.midea.cloud.srm.model.pj.changchengapi.dto.UserApiDTO;

import java.util.List;

/**
 * @author huangbf3
 * 人员业务接口
 */
public interface IUserService {
    /**
     * 备注
     * @param userApiDTOList
     */
    void saveOrUpdateUserBatch(List<UserApiDTO> userApiDTOList);

    /**
     * 更新用户的公司名称
     * @param
     */
    void updateUserCompanyName();

}
