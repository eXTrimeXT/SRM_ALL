package com.midea.cloud.srm.biz.pj.changchengapi.eas.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.dto.LoginResultDto;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasOrgUnitService;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.pj.base.BaseExtClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.springframework.beans.factory.annotation.Autowired;

import java.rmi.RemoteException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Slf4j
@org.springframework.stereotype.Service
public class EasOrgUnitServiceImpl implements EasOrgUnitService {

    @Autowired
    private BaseExtClient baseExtClient;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private EasService easService;


    /**
     * 推送所有的组织
     * @throws Exception
     */
    @Override
    public void  pushAllOrg() throws Exception {
        LoginResultDto loginResultDto = easService.getLoginResultDto();
        Call call = easService.getOrgCall(loginResultDto);

        //推送板块
        pushOrganizationByCodeType(call,"BU");
        //推送公司
        pushOrganizationByCodeType(call,"OU");
        //推送库存组织
        pushOrganizationByCodeType(call,"INV");
        //推送部门
        pushOrganizationByCodeType(call,"DEP");
    }

    /**
     * 推送组织类型组织
     * @param call
     * @param organizationTypeCode
     * @throws RemoteException
     */
    private void pushOrganizationByCodeType(Call call,String organizationTypeCode) throws RemoteException {
        Organization organization =  new Organization();
        organization.setPageSize(100);
        organization.setPageNum(1);
        organization.setOrganizationTypeCode(organizationTypeCode);

        PageInfo<Organization> pageInfo = baseExtClient.listAllOrganization(organization);
        pushOrgList(pageInfo.getList(), call);
        for(int pageNum = 2;pageInfo.getPageNum()<pageInfo.getPages();pageNum++){
            organization.setPageNum(pageNum);
            pageInfo = baseExtClient.listAllOrganization(organization);
            pushOrgList(pageInfo.getList(), call);
        }
    }
    @Override
    public void pushOrgList(List<Organization> organizations, Call call) throws RemoteException {
        if(organizations==null||organizations.size()==0){
            return;
        }
        Map<Long,Organization> parentOrgMap = new HashMap<>(50);
        Set<Long> parentIdSet = getParentOrganizationIds(organizations);
        List<Organization> parentOrganizationList = null;
        while (parentIdSet.size()>0){
            parentOrganizationList = baseClient.getOrganizationsByIds(new ArrayList<>(parentIdSet));
            parentOrgMap.putAll(parentOrganizationList.stream().collect(Collectors.toMap(Organization::getOrganizationId, Function.identity())));
            parentIdSet = getParentOrganizationIds(parentOrganizationList);
        }

        JSONArray paramArr = new JSONArray();
        for(Organization organization: organizations){
            if(StringUtils.equals(organization.getOrganizationCode(),"GROUP")){
                continue;
            }
            JSONObject param = new JSONObject();
            param.put("BM",organization.getOrganizationCode());
            param.put("MC",organization.getOrganizationName());
            param.put("JC",organization.getOrganizationName());
            try{
                Long parentId = Long.valueOf(organization.getParentOrganizationIds());
                if(parentOrgMap.containsKey(parentId)&&!StringUtils.equals(parentOrgMap.get(parentId).getParentOrganizationIds(),"-1")){
                    Organization parentOrg = parentOrgMap.get(parentId);
                    param.put("SJZZ",parentOrg.getOrganizationCode());
                }else{
                    param.put("SJZZ","01");
                }
            }catch (Exception e){
                log.info("组织父ID数据有误");
                continue;
            }
            param.put("BMFZR","");
            {
                int i=1;
                Long parentId = Long.valueOf(organization.getParentOrganizationIds());
                while (parentOrgMap.containsKey(parentId)){
                    i++;
                    if(parentOrgMap.containsKey(parentId)&&StringUtils.isNotBlank(parentOrgMap.get(parentId).getParentOrganizationIds())){
                        parentId = Long.valueOf(parentOrgMap.get(parentId).getParentOrganizationIds());
                    }else{
                        break;
                    }
                }
                param.put("SZCJ",i);
            }
            if(StringUtils.equals(organization.getOrganizationTypeCode(),"BU")||
                    StringUtils.equals(organization.getOrganizationTypeCode(),"COMPANY")){
                param.put("ZZLX","company");
            }else{
                param.put("ZZLX","department");
            }
            param.put("ISDEF",true);

            paramArr.add(param);
        }
        try {
            String result = (String) call.invoke(new Object[]{paramArr.toJSONString()} );
            log.info("result:"+result);
            log.info(result);
        }catch (Exception e){
            log.info("组织推送EAS失败:"+e.getMessage());
        }
    }

    private static Set<Long> getParentOrganizationIds(List<Organization> organizations) {
        Set<Long> parentIdSet = new HashSet<>();
        for(Organization organization: organizations){
            try{
                Long parentId = Long.valueOf(organization.getParentOrganizationIds());
                if(parentId>0){
                    parentIdSet.add(parentId);
                }
            }catch (Exception e){
                log.info("组织父ID数据有误");
            }
        }
        return parentIdSet;
    }

    @Override
    public void pushOrgListToEas(List<Organization> organizationList) throws Exception {
        LoginResultDto loginResultDto = easService.getLoginResultDto();
        Call call = easService.getOrgCall(loginResultDto);
        this.pushOrgList(organizationList,call);
    }
}