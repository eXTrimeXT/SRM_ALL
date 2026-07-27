package com.midea.cloud.srm.rbac.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.RbacPjClient;
import com.midea.cloud.srm.feign.RbacSupClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.siss.dto.SunHonestyReturnDto;
import com.midea.cloud.srm.model.pj.siss.dto.SunHonestySupDto;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.rbac.ExtUserDto;
import com.midea.cloud.srm.model.rbac.ExtUserPermissionDTO;
import com.midea.cloud.srm.model.rbac.role.entity.RoleUser;
import com.midea.cloud.srm.model.rbac.user.dto.*;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.rbac.user.entity.UserThird;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.rbac.iam.IamUtilService;
import com.midea.cloud.srm.rbac.role.service.IRoleService;
import com.midea.cloud.srm.rbac.role.service.IRoleUserService;
import com.midea.cloud.srm.rbac.security.service.crypto.PasswordCreateFactory;
import com.midea.cloud.srm.rbac.user.enums.ThirdSourceEnum;
import com.midea.cloud.srm.rbac.user.mapper.ExtUserMapper;
import com.midea.cloud.srm.rbac.user.service.ExtUserService;
import com.midea.cloud.srm.rbac.user.service.UserThirdService;
import com.midea.cloud.srm.third.anti.AntiUserProxy;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.midea.cloud.srm.rbac.enums.RbacSystemCodeEnum.SRM_RBAC1021000162;

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
 *  修改日期: 2023/10/23 18:19
 *  修改内容:
 * </pre>
 */
@Service
public class ExtUserServiceImpl extends BaseServiceImpl<ExtUserMapper, ExtUser> implements ExtUserService {
    @Autowired
    private IamUtilService iamUtilService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private IRoleUserService iRoleUserService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Resource
    private BaseExtClient baseExtClient;

    @Autowired
    private IRoleService iRoleService;
    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private UserThirdService userThirdService;

    @Autowired
    private RbacPjClient rbacPjClient;
    @Autowired
    private RbacSupClient rbacSupClient;
    @Autowired
    private PasswordCreateFactory passwordCreateFactory;
    @Autowired
    private AntiUserProxy antiUserProxy;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(ExtUserPermissionDTO userPermissionDTO) throws Exception {
        ExtUser user = userPermissionDTO.getUser();
        Long userId = user.getUserId();
        validRoleUser(userPermissionDTO.getRoleUsers(), userId);
        validOrganizationUser(userPermissionDTO.getOrganizationUsers(), userId);
        this.updateById(user); // 修改用户信息
        //同步供应商到iam
//        iamUtilService.syncIamUser(user);
        // 先删除所有的用户与组织关系记录，再添加所有记录
        List<OrganizationUser> organizationUsers = userPermissionDTO.getOrganizationUsers();
        // 先删除所有的用户与组织关系记录，再添加所有记录
        // 通过用户ID删除组织与用户关系记录
        /* 已经分页改造，不删除数据
        baseClient.deleteOrganUserByUserId(user.getUserId());*/
        if (CollectionUtils.isNotEmpty(organizationUsers)) {
            // 批量保存组织与用户关系信息
            /*baseClient.addOrganUserBatch(organizationUsers);*/
            //查询已有得组织用户关系
            /*List<RecordDTO> orgUserList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query(MqlType.ORGANIZATION_USER).in(OrganizationUser::getOrganizationId, organizationUsers.stream().map(o -> o.getOrganizationId()).distinct().collect(Collectors.toList())).eq(OrganizationUser::getUserId, userId));
            Map<Long, RecordDTO> orgUserMap = orgUserList.stream().collect(Collectors.toMap(o -> o.get(OrganizationUser::getOrganizationId), Function.identity(), (k1, k2) -> k2));
            organizationUsers.stream().forEach(organizationUser -> {
                if(orgUserMap.containsKey(organizationUser.getOrganizationId())) {
                    organizationUser.setOrganizationUserRelId(orgUserMap.get(organizationUser.getOrganizationId()).get(OrganizationUser::getOrganizationUserRelId));
                } else {
                    organizationUser.setOrganizationUserRelId(null);
                }
                qlOpenClient.save(ContextPath.BASE, MqlType.ORGANIZATION_USER, organizationUsers);
            });*/
            organizationUsers.forEach(e -> e.setUserId(userId));
            baseExtClient.insertOrUpdateOrgUser(organizationUsers);
        } else {
            qlOpenClient.delete(ContextPath.BASE, QlOpenWrappers.update(MqlType.ORGANIZATION_USER).eq("userId", userId));
        }
        List<RoleUser> roleUsers = userPermissionDTO.getRoleUsers();
        // 先删除所有的角色用户权限关系记录，再添加所有记录
        QueryWrapper<RoleUser> queryRoleUserWrapper
                = new QueryWrapper<RoleUser>(new RoleUser().setUserId(user.getUserId()));
        iRoleUserService.remove(queryRoleUserWrapper); // 通过用户ID删除角色与用户关系记录
        if (CollectionUtils.isNotEmpty(roleUsers)) {
            iRoleUserService.saveBatch(roleUsers); // 批量保存角色用户关系信息
        }

    }

    @Override
    public void validUser(ExtUser user, boolean validPassword) {
        String text = "|";
        if (StringUtils.isBlank(user.getUsername())) {
            throw new BaseException("账号不能为空");
        }
        if (user.getUsername().contains(text)) {
            throw new BaseException("账号不能包含|字符");
        }
        if (StringUtils.isBlank(user.getPassword()) && validPassword) {
            throw new BaseException("密码不能为空");
        }
        if (StringUtils.isBlank(user.getNickname())) {
            throw new BaseException("用户名不能为空");
        }
        if (user.getStartDate() == null) {
            throw new BaseException("生效时间不能为空");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(ExtUserPermissionDTO userPermissionDTO, boolean syncToIam) throws Exception {
        ExtUser user = userPermissionDTO.getUser();

        LambdaQueryWrapper<ExtUser> extUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        extUserLambdaQueryWrapper.eq(ExtUser::getUsername, user.getUsername());

        ExtUser selectOne = this.getOne(extUserLambdaQueryWrapper);
        if (selectOne!= null) {
            throw new BaseException(SRM_RBAC1021000162);
        }
        Long userId = user.getUserId();
        validRoleUser(userPermissionDTO.getRoleUsers(), userId);
        validOrganizationUser(userPermissionDTO.getOrganizationUsers(), userId);
        List<UserMsgDTO> msgList = new ArrayList<>();
        try {
            User prdUser = new User();
            BeanUtil.copyProperties(user, prdUser);
            PwdEncoderDTO pwdEncoderDTO = passwordCreateFactory.createEncoder(prdUser);
            prdUser.setPassword(pwdEncoderDTO.getEncoder());
            prdUser.setInitPassword(prdUser.getPassword());

            UserMsgDTO msgDTO = UserMsgDTO.initByUser(prdUser);
            msgDTO.setPassword(pwdEncoderDTO.getSrc());
            msgList.add(msgDTO);
        } catch (Exception e) {
            throw new BaseException(ResultCode.UNKNOWN_ERROR, "保存用户信息获取密码时报错", e);
        }
        this.save(user); // 保存用户信息

        //同步供应商到iam
//        if(syncToIam){
//            iamUtilService.syncIamUser(user);
//        }

        List<OrganizationUser> organizationUsers = userPermissionDTO.getOrganizationUsers();
        if (CollectionUtils.isNotEmpty(organizationUsers)) {
            // 批量保存组织与用户关系信息
            /*分页保存，需要替换保存方法
            baseClient.addOrganUserBatch(organizationUsers);*/
            //查询已有得组织用户关系
            List<RecordDTO> orgUserList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query(MqlType.ORGANIZATION_USER).in(OrganizationUser::getOrganizationId, organizationUsers.stream().map(o -> o.getOrganizationId()).distinct().collect(Collectors.toList())).eq(OrganizationUser::getUserId, userId));
            Map<Long, RecordDTO> orgUserMap = orgUserList.stream().collect(Collectors.toMap(o -> o.get(OrganizationUser::getOrganizationId), Function.identity(), (k1, k2) -> k2));
            organizationUsers.stream().forEach(organizationUser -> {
                if (orgUserMap.containsKey(organizationUser.getOrganizationId())) {
                    organizationUser.setOrganizationUserRelId(orgUserMap.get(organizationUser.getOrganizationId()).get(OrganizationUser::getOrganizationUserRelId));
                } else {
                    organizationUser.setOrganizationUserRelId(null);
                }
                qlOpenClient.save(ContextPath.BASE, MqlType.ORGANIZATION_USER, organizationUsers);
            });
        }
        List<RoleUser> roleUsers = userPermissionDTO.getRoleUsers();
        if (CollectionUtils.isNotEmpty(roleUsers)) {
            iRoleUserService.saveBatch(roleUsers); // 批量保存角色用户关系信息
        }

        antiUserProxy.batchNoticePwd(msgList);
    }

    public void validRoleUser(List<RoleUser> roleUsers, Long userId) {
        if (CollectionUtils.isNotEmpty(roleUsers)) {
            roleUsers.forEach(roleUser -> {
                Long roleUserId = IdGenrator.generate();
                roleUser.setRoleUserId(roleUserId);
                roleUser.setUserId(userId);
            });
        }
    }


    public void validOrganizationUser(List<OrganizationUser> organizationUsers, Long userId) {
        if (CollectionUtils.isNotEmpty(organizationUsers)) {
            organizationUsers.forEach(organizationUser -> {
                organizationUser.setUserId(userId);
            });
        }
    }

    @Override
    public PageInfo<UserRoleRespDTO> extListUserRoleByParam(UserQueryDto userQueryDto) {
        PageUtil.startPage(userQueryDto.getPageNum(), userQueryDto.getPageSize());
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        Long companyId = loginAppUser.getCompanyId();
        if(!UserType.BUYER.name().equals(loginAppUser.getUserType())){
            // 限制查询本公司的数据
            userQueryDto.setCompanyId(companyId);
        }
        List<UserRoleRespDTO> roleRespDTOList= this.getBaseMapper().listUserRoleByParam(userQueryDto);

        if(CollectionUtils.isNotEmpty(roleRespDTOList)){
            //多语言名称设置
            List<Long> roleIds = roleRespDTOList.stream().filter(r -> null != r.getRoleId()).map(r -> Long.valueOf(r.getRoleId())).distinct().collect(Collectors.toList());
            Map<Long,String> roleNameMap = iRoleService.getRoleNameMap(roleIds);
            for(UserRoleRespDTO dto : roleRespDTOList) {
                if (null == dto.getRoleId()) {
                    continue;
                }
                Long roleId = Long.valueOf(dto.getRoleId());
                if (roleNameMap.containsKey(roleId)) {
                    dto.setRoleName(roleNameMap.get(roleId));
                }
            }

            List<DictItemDTO> dtoList=baseClient.listAllByDictCode("ROLE_TYPE");
            if(CollectionUtils.isNotEmpty(dtoList)){
                Map<String,String> roleTypeDictMap=dtoList.stream().collect(Collectors.toMap(DictItemDTO::getDictItemCode,DictItemDTO::getDictItemName));
                for(UserRoleRespDTO userRoleRespDTO:roleRespDTOList){
                    StringBuilder roleTypeName=new StringBuilder();
                    if(StringUtils.isNotBlank(userRoleRespDTO.getRoleType())){
                        String[] roleTypes=userRoleRespDTO.getRoleType().split(",");
                        for(int i=0;i<roleTypes.length;i++){
                            String roleType=roleTypes[i];
                            if(roleTypeName.length()>0){
                                roleTypeName.append(",");
                            }
                            if(roleTypeDictMap.containsKey(roleType)){
                                roleTypeName.append(roleTypeDictMap.get(roleType));
                            }else{
                                roleTypeName.append(roleType);
                            }
                        }
                        userRoleRespDTO.setRoleTypeName(roleTypeName.toString());
                    }
                }
            }
        }
        return new PageInfo<>(roleRespDTOList);
    }
    @Override
    public ExtUserDto lcCodeVerify(String lcCode,String isPersonalAccount) {
        ExtUserDto userDto=new ExtUserDto();
        List<String> statusCodeList = new ArrayList<>();
        statusCodeList.add(ApproveStatusType.WITHDRAW.getValue());
        statusCodeList.add(ApproveStatusType.REJECTED.getValue());
        //先根据社会信用码查询是否存在
        List<CompanyInfo> authenticationScreens =new ArrayList<>();
        if(isPersonalAccount.equals(YesOrNo.NO.getValue())){
            authenticationScreens = qlOpenClient.query(ContextPath.SUP,
                    QlOpenWrappers.query("CompanyInfo").eq(CompanyInfo::getLcCode, lcCode)
                            .notIn(CompanyInfo::getStatus, statusCodeList)
                    , CompanyInfo.class);
        }else{
            authenticationScreens = qlOpenClient.query(ContextPath.SUP,
                    QlOpenWrappers.query("CompanyInfo").eq(CompanyInfo::getIdNumber, lcCode)
                            .notIn(CompanyInfo::getStatus, statusCodeList)
                    , CompanyInfo.class);
        }
        if(authenticationScreens==null || authenticationScreens.isEmpty()){
            userDto.setIsRemind(YesOrNo.NO.getValue());
        }else{
            //否则，根据公司ID查询用户表，如果存在则提醒，不存在不提醒
            User user = rbacClient.queryByCompanyId(authenticationScreens.get(0).getCompanyId());
            if(user!=null){
                userDto.setIsRemind(YesOrNo.YES.getValue());
                userDto.setPhone(user.getPhone());
                userDto.setNickname(user.getNickname());
            }else{
                userDto.setIsRemind(YesOrNo.NO.getValue());
            }
        }
        return userDto;
    }
    @Override
    public void synPermissions(UserPermissionDTO userPermissionDTO) throws Exception {
        List<OrganizationUser> organizationUsers = userPermissionDTO.getOrganizationUsers();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(organizationUsers)) {
            this.baseClient.addOrganUserBatch(organizationUsers);
        }

        List<RoleUser> roleUsers = userPermissionDTO.getRoleUsers();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(roleUsers)) {
            this.iRoleUserService.saveBatch(roleUsers);
        }
    }

    @Override
    public void userPushSiss(List<User> pageUser) {
        //查询是否在第三方用户表
        List<UserThird> existUserThirds = userThirdService.list(Wrappers.lambdaQuery(UserThird.class)
                .in(UserThird::getUniqueId, pageUser.stream().map(User::getUserId).map(String::valueOf).collect(Collectors.toList()))
                .eq(UserThird::getThirdSource, ThirdSourceEnum.SISS.getCode()));
        List<String> existUniqueIds = existUserThirds.stream().map(UserThird::getUniqueId).collect(Collectors.toList());

        //待推送数据
        List<SunHonestySupDto> sunHonestySupDtos = pageUser.stream()
                .filter(e -> Objects.nonNull(e.getCompanyId()) && !existUniqueIds.contains(e.getUserId().toString()))
                .map(this::userCoverSunHonestySunDto)
                .filter(dto -> StringUtils.isNotEmpty(dto.getUsername()) && StringUtils.isNotEmpty(dto.getNickName())
                        && StringUtils.isNotEmpty(dto.getCompanyName()) && StringUtils.isNotEmpty(dto.getEmail())
                        && StringUtils.isNotEmpty(dto.getContact()))
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(sunHonestySupDtos)){
            return;
        }

        //推送数据 返回结果
        List<User> finalPageUser = pageUser;
        List<UserThird> insertList = rbacPjClient.pushCompanyUser(sunHonestySupDtos).stream()
                .map(e -> {return sunHonestySupDtoCoverUserThird(finalPageUser, e);})
                .collect(Collectors.toList());

        //新增数据
        userThirdService.saveBatch(insertList);
    }


    @NotNull
    private UserThird sunHonestySupDtoCoverUserThird(List<User> finalPageUser, SunHonestyReturnDto e) {
        UserThird userThird = new UserThird();
        // ID
        userThird.setUserThirdId(IdGenrator.generate());
        //唯一键 userId
        userThird.setUniqueId(finalPageUser.stream()
                .filter(o -> Objects.equals(o.getUsername(), e.getUsername()))
                .map(User::getUserId)
                .map(String::valueOf)
                .findFirst().orElse(null));
        //USER_ACCOUNT 用户账号 --->登录账号
        userThird.setUserAccount(e.getUsername());
        userThird.setThirdAccountType(UserType.VENDOR.name());
        //第三方账户 唯一键
        userThird.setThirdUnionId(e.getResultId().toString());

        userThird.setThirdOpenId(e.getCompanyName());
        //第三方账户来源
        userThird.setThirdSource(ThirdSourceEnum.SISS.getCode());
        userThird.setBindTime(new Date());
        return userThird;
    }

    @NotNull
    private SunHonestySupDto userCoverSunHonestySunDto(User e) {
        SunHonestySupDto sunHonestySupDto = new SunHonestySupDto();
        sunHonestySupDto.setUsername(e.getUsername());
        CompanyInfo companyInfo = rbacSupClient.getCompanyInfoById(e.getCompanyId());
        if(Objects.nonNull(companyInfo)){
            sunHonestySupDto.setNickName(companyInfo.getLcCode());
        }
        sunHonestySupDto.setCompanyName(e.getCompanyName());
        sunHonestySupDto.setEmail(e.getEmail());
        sunHonestySupDto.setContact(e.getNickname());
        sunHonestySupDto.setSourceType("3");
        return sunHonestySupDto;
    }

    @Override
    public List<UserThird> selectUserThird(UserThird userThird) {
        //查询是否在第三方用户表
        List<UserThird> existUserThirds = userThirdService.list(Wrappers.lambdaQuery(UserThird.class)
                .eq(userThird.getThirdUnionId()!=null,UserThird::getThirdUnionId, userThird.getThirdUnionId())
                .eq(userThird.getUserAccount()!=null,UserThird::getUserAccount, userThird.getUserAccount())
                .eq(userThird.getThirdSource()!=null,UserThird::getThirdSource, userThird.getThirdSource()));
        return existUserThirds;
    }
}
