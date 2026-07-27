package com.midea.cloud.srm.rbac.user.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.midea.cloud.common.enums.MainType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.rbac.ExtUserDto;
import com.midea.cloud.srm.model.rbac.ExtUserPermissionDTO;
import com.midea.cloud.srm.model.rbac.role.entity.Role;
import com.midea.cloud.srm.model.rbac.role.entity.RoleUser;
import com.midea.cloud.srm.model.rbac.user.dto.UserPermissionDTO;
import com.midea.cloud.srm.model.rbac.user.dto.UserQueryDto;
import com.midea.cloud.srm.model.rbac.user.dto.UserRoleRespDTO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.rbac.user.entity.UserThird;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.rbac.anon.controller.RbacAnonController;
import com.midea.cloud.srm.rbac.role.service.IRoleUserService;
import com.midea.cloud.srm.rbac.user.mapper.ExtUserMapper;
import com.midea.cloud.srm.rbac.user.service.ExtUserService;
import com.midea.cloud.srm.rbac.user.service.IUserService;
import com.midea.cloud.srm.rbac.user.service.LoginCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
 *  修改日期: 2023/10/23 16:25
 *  修改内容:
 * </pre>
 */
@RestController
@RequestMapping("/extUser")
@Slf4j
public class ExtUserController {

    @Autowired
    private IRoleUserService iRoleUserService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private LoginCacheService loginCacheService;

    @Autowired
    private IUserService userService;


    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private ExtUserMapper extUserMapper;

    @Autowired
    private ExtUserService extUserService;

    @Autowired
    private RbacAnonController rbacAnonController;

    @Autowired
    private UserController userController;
    /**
     * 通过用户ID获取采购商用户相关信息
     *
     * @param id 参数
     * @return 结果
     */
    @GetMapping(value = "/getByBuyer")
    public ExtUserPermissionDTO getByBuyer(Long id) {
        ExtUser user = extUserService.getById(id);
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        QueryWrapper<RoleUser> roleUserQueryWrapper = new QueryWrapper<>(new RoleUser().setUserId(id));

        List<RoleUser> roleUserList = iRoleUserService.list(roleUserQueryWrapper);
        List<OrganizationUser> organizationUsers = baseClient.listOrganUserByParam(new OrganizationUser().setUserId(id));
        ExtUserPermissionDTO userPermissionDTO = new ExtUserPermissionDTO();
        // 密码置空
        user.setPassword(null);
        userPermissionDTO.setUser(user);
        userPermissionDTO.setRoleUsers(roleUserList);
        userPermissionDTO.setOrganizationUsers(organizationUsers);
        return userPermissionDTO;
    }

    /**
     * 修改采购商账号
     *
     * @param userPermissionDTO 参数
     */
    @PostMapping("/modifyBuyer")
    public void modifyBuyer(@RequestBody ExtUserPermissionDTO userPermissionDTO) throws Exception {
        ExtUser user = userPermissionDTO.getUser();

        user.setCompanyId(null); // 公司ID置空，采购商没有公司信息
//        user.setUserType(UserType.BUYER.name()); // 设置用户类型： 采购商
//        user.setMainType(MainType.N.name()); // 设置子账号
        user.setPassword(null); // 不更新密码
        extUserService.validUser(user, false); // 校验用户信息
        extUserService.update(userPermissionDTO);

        loginCacheService.clearOrgUserByUserId(user.getUserId().toString());
        loginCacheService.removeUserAccessCacheByUsername(user.getUsername());
        loginCacheService.loadDataPermission();
    }

    @PostMapping("/addBuyer")
    public void addBuyer(@RequestBody ExtUserPermissionDTO userPermissionDTO) throws Exception {
        Long userId = IdGenrator.generate();
        ExtUser user = userPermissionDTO.getUser();
        // 设置用户ID
        user.setUserId(userId);
        // 公司ID置空，采购商没有公司信息
        user.setCompanyId(null);
        // 设置用户类型： 采购商
        user.setUserType(UserType.BUYER.name());
        // 设置子账号
        user.setMainType(MainType.N.name());
        // 生成8位随机密码
//        String password = StringUtil.genPwdChar(8);
        // 设置随机密码
//        user.setPassword(password);
        // 校验用户信息
        extUserService.validUser(user, false);
        extUserService.save(userPermissionDTO, true);
    }

    @PostMapping("/checkIfOverThree")
    public Integer checkIfOverThree(@RequestBody User user) {
        Assert.isTrue(user.getCompanyId() != null, "供应商id不能为空");
        int count = (int) userService.count(Wrappers.lambdaQuery(User.class)
                .eq(User::getCompanyId, user.getCompanyId())
                .ne(user.getUserId() != null, User::getUserId, user.getUserId()));
        return count;
    }

    /**
     * 新增供应商
     *
     * @param userPermissionDTO 参数
     */
    @PostMapping("/addVendor")
    public void addVendor(@RequestBody UserPermissionDTO userPermissionDTO) throws Exception {
        int island=1;
        User user = userPermissionDTO.getUser();
        User selectUser = userService.selectFirst(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, user.getUsername()));
        if(selectUser!=null){
            if(selectUser.getCompanyId()==null){
                island=0;
                user.setMainType(Enable.N.name());
                extUserMapper.updateCompanyId(user);
            }else {
                throw new BaseException("该用户名已存在");
            }
        }
        if(island==1) {
            // 前置,如果当前maintype为Y.需先根据供应商把所有的用户更新成N,再执行接口
            if (user.getCompanyId() != null && Enable.Y.name().equals(user.getMainType())) {
                userService.update(Wrappers.lambdaUpdate(User.class)
                        .set(User::getMainType, Enable.N.name())
                        .eq(User::getCompanyId, user.getCompanyId()));
            }
            userController.addVendor(userPermissionDTO);
            // 需要把当前账户设置成主账号,rbacExtClient.addVendor默认都是子账号
            if (Enable.Y.name().equals(user.getMainType())) {
                userService.update(Wrappers.lambdaUpdate(User.class)
                        .set(User::getMainType, Enable.Y.name())
                        .eq(User::getUsername, user.getUsername()));
            }
        }
        User addUser = userService.selectFirst(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, user.getUsername()));
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        List<Role> rolePermissions = loginAppUser.getRolePermissions();
        if (CollectionUtils.isNotEmpty(rolePermissions)) {
            Set<String> roleTypeSet = rolePermissions.stream().map(Role::getRoleType).collect(Collectors.toSet());
            // 初始化用户角色
            for (String roleType : roleTypeSet) {
                rbacAnonController.initUserRole(addUser.getUserId(), roleType);
            }
        }
        if(island==0){
            extUserService.synPermissions(userPermissionDTO);
        }
        //推送用户
        user.setUserId(addUser.getUserId());
        user.setCompanyName(user.getCeeaCompany());
        extUserService.userPushSiss(Lists.newArrayList(user));


    }

    /**
     * 编辑供应商主账号
     *
     * @param userPermissionDTO 参数
     */
    @PostMapping("/modifyVendor")
    public void modifyVendor(@RequestBody UserPermissionDTO userPermissionDTO) throws Exception {
        User user = userPermissionDTO.getUser();
        // 前置,如果当前maintype为Y.需先根据供应商把所有的用户更新成N,再执行接口
        if (user.getCompanyId() != null && Enable.Y.name().equals(user.getMainType())) {
            userService.update(Wrappers.lambdaUpdate(User.class)
                    .set(User::getMainType, Enable.N.name())
                    .eq(User::getCompanyId, user.getCompanyId()));
        }
        userController.modifyVendor(userPermissionDTO);
    }

    @GetMapping(value = "/getByUserId")
    public ExtUser getByUserId(Long id) {
        return extUserService.getById(id);
    }

    /**
     * 删除供应商信息
     */
    @GetMapping("/deleteUser")
    public void deleteUser(@RequestParam Long userId) {
        userService.removeById(userId);
    }

    /**
     * 查询用户与角色平铺结果
     * @param userQueryDto 参数
     * @return 结果
     */
    @PostMapping("/extListUserRoleByParam")
    public PageInfo<UserRoleRespDTO> extListUserRoleByParam(@RequestBody UserQueryDto userQueryDto) {
        return extUserService.extListUserRoleByParam(userQueryDto);
    }
    /**
     * 填写认证信息时，输入统一社会信用代码触发验证供应商是否存在
     *             contactInformation
     * **/
    @GetMapping("/lcCodeVerify")
    public ExtUserDto lcCodeVerify(@RequestParam("lcCode") String lcCode,@RequestParam("isPersonalAccount") String  isPersonalAccount) {
        ExtUserDto userDto;
        userDto=extUserService.lcCodeVerify(lcCode,isPersonalAccount);
        return userDto;
    }

    @PostMapping("/pushVendorSiss")
    public void pushVendorSiss(@RequestBody List<User> uList) {
        extUserService.userPushSiss(uList);
    }

    @PostMapping("/selectUserThird")
    public List<UserThird> selectUserThird(@RequestBody UserThird userThird) {
       return extUserService.selectUserThird(userThird);
    }
}
