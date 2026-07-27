package com.midea.cloud.srm.rbac.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.rbac.user.dto.UserQueryDto;
import com.midea.cloud.srm.model.rbac.user.dto.UserRoleRespDTO;
import com.midea.cloud.srm.model.rbac.user.entity.User;

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
 *  修改日期: 2023/10/23 18:21
 *  修改内容:
 * </pre>
 */
public interface ExtUserMapper extends BaseMapper<ExtUser> {

    /**
     * listUserRoleByParam
     * @param userQueryDto
     * @return
     */
    List<UserRoleRespDTO> listUserRoleByParam(UserQueryDto userQueryDto);
    /**
     * 供应商子账号修改
     * @param user
     */
    public void updateCompanyId(User user);

}
