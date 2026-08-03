package com.midea.cloud.srm.biz.pj.changchengapi.eas.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.dto.LoginResultDto;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasService;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasUserService;
import com.midea.cloud.srm.biz.pj.hrorganization.service.SccPjOrganizationService;
import com.midea.cloud.srm.biz.pj.hruser.service.ISccPjUserService;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.springframework.beans.factory.annotation.Autowired;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Slf4j
@org.springframework.stereotype.Service
public class EasUserServiceImpl implements EasUserService {
    @Autowired
    private EasService easService;
    @Autowired
    private ISccPjUserService iSccPjUserService;
    @Autowired
    private RbacClient rbacClient;
    @Autowired
    private SccPjOrganizationService sccPjOrganizationService;

    @Override
    public void pushAllUser() throws Exception {
        LoginResultDto loginResultDto = easService.getLoginResultDto();
        Call call = easService.getPersonCall(loginResultDto);
        SccPjUser sccPjUser = new SccPjUser();
        sccPjUser.setPageNum(1);
        sccPjUser.setPageSize(100);

        PageInfo<SccPjUser> pageInfo = iSccPjUserService.listPage(sccPjUser);
        if(pageInfo.getTotal()==0){
            return;
        }
        pushUserList(pageInfo.getList(), call);
        for(int pageNum = 2;pageInfo.getPageNum()<pageInfo.getPages();pageNum++){
            sccPjUser.setPageNum(pageNum);
            pageInfo = iSccPjUserService.listPage(sccPjUser);
            pushUserList(pageInfo.getList(), call);
        }
    }

    @Override
    public void pushUserList(List<SccPjUser> sccPjUsers, Call call) throws RemoteException {
        if(sccPjUsers==null|| sccPjUsers.size()==0){
            return;
        }
        Set<String> usernames = sccPjUsers.stream().map(SccPjUser::getPersonnelNo).collect(Collectors.toSet());
        Set<Long> groupIdSet = sccPjUsers.stream().map(SccPjUser::getGroupId).collect(Collectors.toSet());
        List<User> users = rbacClient.listByUserNames(usernames);
        Map<String,User> userMap = users.stream().collect(Collectors.toMap(User::getUsername, t->t));
        Map<String,SccPjUser> sccPjUserMap = sccPjUsers.stream().collect(Collectors.toMap(SccPjUser::getPersonnelNo, t->t));
        List<SccPjOrganization> sccPjOrganizations = sccPjOrganizationService.lambdaQuery().in(SccPjOrganization::getId,groupIdSet).list();
        Map<Long, SccPjOrganization> sccPjOrganizationMap = new HashMap<>(50);
        for(SccPjOrganization sccPjOrganization:sccPjOrganizations){
            sccPjOrganizationMap.put(sccPjOrganization.getId(),sccPjOrganization);
        }

        JSONArray paramArr = new JSONArray();
        for(User user:users){
            JSONObject param = new JSONObject();
            String xb = "1";
            String ygzt = "001";
            SccPjUser pjUser = sccPjUserMap.get(user.getUsername());
            {
                xb = pjUser.getSex().toString();
                ygzt = new Integer(2).equals(pjUser.getIsFormal())?"002":"001";
            }
            String sszz = null;
            if(sccPjOrganizationMap.containsKey(pjUser.getGroupId())){
                sszz = sccPjOrganizationMap.get(pjUser.getGroupId()).getOrganizationCode();
            }else{
                continue;
            }
            param.put("YGBM",user.getUsername());
            param.put("YGXM",user.getNickname());
            param.put("XB",xb);
            param.put("HKSZD","");
            param.put("JG","");
            param.put("HJDZ","");
            param.put("XJZD","");
            param.put("CSRQ","");
            param.put("HKLX","");
            param.put("SSZZ",sszz);
            param.put("SJ",user.getPhone());
            param.put("YGZT",ygzt);

            paramArr.add(param);
        }
        if(paramArr.size()==0){
            return;
        }
        try {
            log.info("paramArr:"+paramArr.toJSONString());
            String result = (String) call.invoke(new Object[]{paramArr.toJSONString()} );
            log.info("result:"+result);
            log.info(result);
        }catch (Exception e){
            log.info("用户推送EAS失败:"+e.getMessage());
        }
    }

    @Override
    public void pushUserListToEas(List<SccPjUser> sccPjUsers) throws Exception {
        LoginResultDto loginResultDto = easService.getLoginResultDto();
        Call call = easService.getPersonCall(loginResultDto);
        pushUserList(sccPjUsers,call);
    }

    /**
     * 推送用户
     */
    @Override
    public void pushRbacUser(User u) throws Exception {
        LoginResultDto loginResultDto = easService.getLoginResultDto();
        Call call = easService.getPersonCall(loginResultDto);
        pushSelectUser(u, call);
    }

    public void pushSelectUser(User u, Call call) {
        u.setPageNum(1);
        u.setPageSize(500);
        List<User> users = rbacClient.listByUser(u);
        Set<String> usernames = users.stream().map(User::getUsername).collect(Collectors.toSet());
        List<SccPjUser> sccPjUsers = iSccPjUserService.list(new LambdaQueryWrapper<SccPjUser>().in(SccPjUser::getPersonnelNo, usernames));
        Set<Long> groupIdSet = sccPjUsers.stream().map(SccPjUser::getGroupId).collect(Collectors.toSet());
        Map<String,SccPjUser> sccPjUserMap = sccPjUsers.stream().collect(Collectors.toMap(SccPjUser::getPersonnelNo, t->t));
        List<SccPjOrganization> sccPjOrganizations = sccPjOrganizationService.lambdaQuery().in(SccPjOrganization::getId,groupIdSet).list();
        Map<Long, SccPjOrganization> sccPjOrganizationMap = new HashMap<>(50);
        for(SccPjOrganization sccPjOrganization:sccPjOrganizations){
            sccPjOrganizationMap.put(sccPjOrganization.getId(),sccPjOrganization);
        }
        JSONArray paramArr = new JSONArray();
        for(User user : users) {
            JSONObject param = new JSONObject();
            String xb = "1";
            String ygzt = "001";
            SccPjUser pjUser = sccPjUserMap.get(user.getUsername());
            {
                xb = pjUser.getSex().toString();
                ygzt = new Integer(2).equals(pjUser.getIsFormal())?"002":"001";
            }
            String sszz = null;
            if(sccPjOrganizationMap.containsKey(pjUser.getGroupId())){
                sszz = sccPjOrganizationMap.get(pjUser.getGroupId()).getOrganizationCode();
            }else{
                continue;
            }
            param.put("YGBM",user.getUsername());
            param.put("YGXM",user.getNickname());
            param.put("XB",xb);
            param.put("HKSZD","");
            param.put("JG","");
            param.put("HJDZ","");
            param.put("XJZD","");
            param.put("CSRQ","");
            param.put("HKLX","");
            param.put("SSZZ",sszz);
            param.put("SJ",user.getPhone());
            param.put("YGZT",ygzt);
            paramArr.add(param);
        }
        try {
            log.info("paramArr:"+paramArr.toJSONString());
            String result = (String) call.invoke(new Object[]{paramArr.toJSONString()} );
            log.info("result:"+result);
            log.info(result);
        }catch (Exception e){
            log.info("用户推送EAS失败:"+e.getMessage());
        }
    }
}