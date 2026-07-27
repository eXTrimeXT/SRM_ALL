package com.midea.cloud.srm.rbac.user.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.rbac.ExtUserDto;
import com.midea.cloud.srm.model.rbac.ExtUserPermissionDTO;
import com.midea.cloud.srm.model.rbac.user.dto.UserPermissionDTO;
import com.midea.cloud.srm.model.rbac.user.dto.UserQueryDto;
import com.midea.cloud.srm.model.rbac.user.dto.UserRoleRespDTO;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.rbac.user.entity.UserThird;

import java.util.List;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/23 18:18
 *  修改内容:
 * </pre>
 */
public interface ExtUserService extends BaseService<ExtUser> {
    /**
     * 备注
     * @param userPermissionDTO 参数
     * @throws Exception 报错
     */
    void update(ExtUserPermissionDTO userPermissionDTO) throws Exception;

    /**
     * 备注
     * @param user 参数
     * @param b 参数
     */
    void validUser(ExtUser user, boolean b);

    /**
     * 备注
     * @param userPermissionDTO 参数
     * @param b 参数
     * @throws Exception 报错
     */
    void save(ExtUserPermissionDTO userPermissionDTO, boolean b) throws Exception;

    /**
     * 角色权限列表
     * @param userQueryDto 参数
     * @return 结果
     */
    public PageInfo<UserRoleRespDTO> extListUserRoleByParam(UserQueryDto userQueryDto);
    /**
     *
     * 验证社会统一验证信用码是否已经注册
     * @param lcCode 社会统一验证信用码
     * @param isPersonalAccount 是否个人账户
     * @return 返回结果
     */
    ExtUserDto lcCodeVerify(String lcCode,String isPersonalAccount);
    /**
     * 备注
     * @param userPermissionDTO 参数
     * @throws Exception 报错
     */
    void synPermissions(UserPermissionDTO userPermissionDTO) throws Exception;


    /**
     * 备注
     * @param users 参数
     */
    void userPushSiss(List<User> users);
    /**
     *
     * 查询第三方用户
     * @param userThird 社会统一验证信用码
     * @return 返回结果
     */
    List<UserThird> selectUserThird(UserThird userThird);
}
