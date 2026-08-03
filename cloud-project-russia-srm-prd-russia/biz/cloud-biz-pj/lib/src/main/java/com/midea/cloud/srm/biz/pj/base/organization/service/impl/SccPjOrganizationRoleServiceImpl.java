package com.midea.cloud.srm.biz.pj.base.organization.service.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.api.ResultStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.listener.AnalysisEventListenerImpl;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.excel.in.aop.ExcelImportCheck;
import com.midea.cloud.common.utils.excel.in.aop.ExcelSheetCheck;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.biz.pj.base.organization.mapper.SccPjOrganizationRoleMapper;
import com.midea.cloud.srm.biz.pj.base.organization.service.ISccPjOrganizationRoleService;
import com.midea.cloud.srm.biz.pj.base.organization.service.ISccPjOrganizationRoleUserService;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.dto.LoginResultDto;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasService;
import com.midea.cloud.srm.biz.pj.hrorganization.service.SccPjOrganizationService;
import com.midea.cloud.srm.biz.pj.hruser.service.ISccPjUserService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.base.organization.dto.BpmSccPjOrganizationRoleDto;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationRoleDto;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationRoleUserDto;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRole;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRoleUser;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import io.seata.common.util.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 * 组织角色实现类
 */
@Slf4j
@Service
public class SccPjOrganizationRoleServiceImpl extends ServiceImpl<SccPjOrganizationRoleMapper, SccPjOrganizationRole> implements ISccPjOrganizationRoleService {

    @Resource
    private ISccPjOrganizationRoleUserService organizationRoleUserService;

    @Value("${gwm.url.organizationRole}")
    private String organizationRoleUrl;
    @Value("${gwm.preappkey}")
    private String preappKey;
    @Value("${gwm.presecret}")
    private String presecret;
    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;
    @Value("${gwm.bpm.api-username}")
    private String apUsername;

    @Value("${eas.targetEndpointAddress}")
    private String targetEndpointAddress;

    @Value("${eas.namespace}")
    private String namespace;

    @Autowired
    private EasService easService;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private ISccPjUserService iSccPjUserService;

    @Autowired
    private SccPjOrganizationService sccPjOrganizationService;

    @Autowired
    private BaseClient baseClient;

    @Resource
    private QlOpenClient qlOpenClient;

    @Resource
    private IInterfaceLogService interfaceLogService;
    @Autowired
    private FileCenterClient fileCenterClient;

    @Override
    public void organizationRoleBpm(BpmSccPjOrganizationRoleDto bpmPor) {
        String url = organizationRoleUrl;
        List<Long> ids = bpmPor.getIds();
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.BPM_ROLE;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,bpmPor);
        String strIn = null;
        String reIn = null;
        try {
            List<SccPjOrganizationRole> list = listByIds(ids);
            for (SccPjOrganizationRole e : list) {
                Map<String, String> headers = new HashMap<>(50);
                headers.put("SRC-SYSTEM", srcSystem);
                headers.put("USERID", apUsername);
                headers.put("DATA-ID", String.valueOf(e.getRowId()));
                OpenClient openClient = new OpenClient(preappKey, presecret);
                String str = getOrgRoleBpmString(e, bpmPor.getOperation());
                log.info(str);
                strIn = str;
                interfaceLog.setServiceInfo(str);
                String re = openClient.sendHttpPost(url, str,"application/json", headers);
                reIn = re;
                interfaceLog.setReturnInfo(re);
                interfaceLogService.createInterfaceLog(interfaceLog);
                log.info("调试bpm===" + re);
            }
        } catch (Exception e) {
            interfaceLog.setServiceInfo(strIn);
            interfaceLog.setReturnInfo(reIn);
            interfaceLog.setErrorInfo(e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLogService.createInterfaceLog(interfaceLog);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 手动或自动同步组织角色EAS
     *
     * @param easPor 传入数据
     */
    @Override
    public void organizationRoleEas(BpmSccPjOrganizationRoleDto easPor) throws Exception{
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.BPM_ROLE;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,easPor);
        String strIn = null;
        String reIn = null;
        try {
            LoginResultDto loginResultDto = easService.getLoginResultDto();
            Call call = getPersonCall(loginResultDto);
            List<SccPjOrganizationRole> list = listByIds(easPor.getIds());
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (SccPjOrganizationRole e : list) {
                Map<String, Object> paramMap  = new HashMap<>(50);
    //            职位编码
                paramMap.put("roIeCode", e.getRoleCode());
    //            职位名称
                paramMap.put("roleName", e.getRoleName());
    //            公司编码
                paramMap.put("organizationld", "111.01.02");
    //            公司的组织层级
                paramMap.put("orgcj", getTreeLevel(e.getParentRoleCode()));
    //            上级职位编码
                paramMap.put("parentRoIeCode", e.getParentRoleCode());
    //            上级职位名称
                paramMap.put("parentRoIeName", e.getParentRoleName());
    //            员工工号数组（多个逗号隔开，可空）
                paramMap.put("usermames", "01");
                paramMap.put("status", "Y".equals(e.getUseFlag()) ? "启用" : "禁用");
    //            状态（启用/禁用）
                paramMap.put("deleteFlag", "否");
    //            是否删除（是/否）
                resultList.add(paramMap);
            }
/*            log.info("组织角色推送EAS信息==={}" , JSONObject.toJSONString(resultList));
            strIn = JSONObject.toJSONString(resultList);
            interfaceLog.setServiceInfo(strIn);
            Object result = call.invoke(new Object[]{JSONObject.toJSONString(resultList)});
            reIn = String.valueOf(result);
            interfaceLog.setReturnInfo(String.valueOf(result));
            interfaceLogService.createInterfaceLog(interfaceLog);
            log.info("返回的EAS信息==={}" , JSONObject.toJSONString(result));
            log.info("返回的EAS信息===" + JSONObject.toJSONString(result));*/
        } catch (Exception e) {
            interfaceLog.setServiceInfo(strIn);
            interfaceLog.setReturnInfo(reIn);
            interfaceLog.setErrorInfo(e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLogService.createInterfaceLog(interfaceLog);
            throw new BaseException(e.getMessage());
        }
    }



    public Call getPersonCall(LoginResultDto loginResultDto) {
        Call call = loginResultDto.getCall();
        call.setOperationName("inPostion");
        call.setTargetEndpointAddress(targetEndpointAddress);
        call.setReturnQName(new QName("", "inPostionReturn"));
        /*调用业务接口 */
        call.setTimeout(100 * 60000 * 60);
        call.setMaintainSession(true);
        /*设置登录返回的session在soap头 "http://login.webservice.bos.kingdee.com"是固定的 */
        SOAPHeaderElement header = new SOAPHeaderElement(namespace, "SessionId", loginResultDto.getWsContext().getSessionId());
        call.addHeader(header);
        return call;
    }


    @Override
    public SccPjOrganizationRoleUser getParentUserByUsername(String username) {
        Assert.notBlank(username,"用户账号不能为空");

        List<SccPjOrganizationRoleUser> roleUserList = organizationRoleUserService.lambdaQuery().eq(SccPjOrganizationRoleUser::getUserName,username).list();
        if(roleUserList!=null&&roleUserList.size()>0&&roleUserList.get(0).getOrganizationRoleId()!=null){
            SccPjOrganizationRole pjOrganizationRole = this.getById(roleUserList.get(0).getOrganizationRoleId());
            if(StringUtils.isNotBlank(pjOrganizationRole.getParentRoleCode())){
                SccPjOrganizationRole parentOrgRole = this.getOne(Wrappers.lambdaQuery(SccPjOrganizationRole.class).eq(SccPjOrganizationRole::getRoleCode,pjOrganizationRole.getParentRoleCode()));
                if(parentOrgRole!=null){
                    List<SccPjOrganizationRoleUser> parentRoleUserList = organizationRoleUserService.lambdaQuery()
                            .eq(SccPjOrganizationRoleUser::getOrganizationRoleId,parentOrgRole.getRowId()).list();
                    if(parentRoleUserList!=null&&parentRoleUserList.size()>0) {
                        return parentRoleUserList.get(0);
                    }
                }
            }
        }

        return null;
    }



    private String getOrgRoleBpmString(SccPjOrganizationRole por, String operation) {
        List<Map<String, String>> itemData = new ArrayList<>();
        LambdaQueryWrapper<SccPjOrganizationRoleUser> li = new LambdaQueryWrapper<>();
        li.eq(SccPjOrganizationRoleUser::getOrganizationRoleId, por.getRowId());
        List<SccPjOrganizationRoleUser> roleUserList = organizationRoleUserService.list(li);
        for (SccPjOrganizationRoleUser e : roleUserList) {
            Map<String, String> itemDataMap = new HashMap<>(50);
            itemDataMap.put("__TABLE", "BO_EU_SRM_AUDIT_USER");
            itemDataMap.put("USERNO", e.getUserName());
            itemData.add(itemDataMap);
        }
        Map<String, Object> roleMap = new HashMap<>(50);
        roleMap.put("ROLECODE", por.getRoleCode());
        roleMap.put("ROLENAME", por.getRoleName());
        roleMap.put("PARENTCODE", por.getParentRoleCode());
        roleMap.put("STATUS", "Y".equals(por.getUseFlag()) ? "1" : "0");
        roleMap.put("ROLEORGID", por.getHrOrgnizationId());
        List<String> itemTable = new ArrayList<>();
        itemTable.add("BO_EU_SRM_AUDIT_USER");
        Map<String, Object> resultMap = new HashMap<>(50);
        resultMap.put("ITEMDATA", itemData);
        resultMap.put("ITEMTABLE", itemTable);
        resultMap.put("MAINTABLE", "BO_EU_SRM_AUDIT_ROLE");
        resultMap.put("MAINTABLEDATA", roleMap);
        resultMap.put("OPERATION", "DELETE".equals(operation) ? "DELETE" : "ADD");
        resultMap.put("UNIQUEKEY", "ROLECODE");
        resultMap.put("APPID", "com.awspaas.user.apps.app20230971602");
        return JSONObject.toJSONString(resultMap);
    }

    public Integer getTreeLevel(String pCode){
        int i = 0;
        boolean b = true;
        while (b) {
            i++;
            LambdaQueryWrapper<SccPjOrganizationRole> qw = new LambdaQueryWrapper<>();
            qw.eq(SccPjOrganizationRole::getRoleCode, pCode);
            SccPjOrganizationRole role = this.getOne(qw);
            if (role == null) {
                break;
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(role.getParentRoleCode())) {
                break;
            }
            pCode = role.getParentRoleCode();
            if (i >= 10) {
                b = false;
            }
        }
        return i;
    }

    public String getOrgCode(Long id) {
        try {
            Organization organization = qlOpenClient.read(ContextPath.BASE,"Organization", id, Organization.class);
            return organization.getOrganizationCode();
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 定标审批_推送中标范围
     */
    @Override
    public void pushZbfwToBpm(String zbfw,String zbfwcode,String caNo) {
        String url = organizationRoleUrl;
        Map<String, String> headers = new HashMap<>(15);
        headers.put("SRC-SYSTEM", srcSystem);
        headers.put("USERID", apUsername);
        headers.put("DATA-ID", String.valueOf(caNo));
        OpenClient openClient = new OpenClient(preappKey, presecret);
        Map<String, Object> map = new HashMap<>(15);
        Map<String, Object> zbfwMap = new HashMap<>(15);
        String[] zbfws = zbfw.split(";");
        String[] zbfwcodes = zbfwcode.split(";");
        for(int i = 0 ; i < zbfws.length; i ++){
            String cd = zbfwcodes[i];
            String fw = zbfws[i];
            zbfwMap.put("ZBFW",fw);
            zbfwMap.put("ZBFWCODE",cd);
            zbfwMap.put("DBSPDH",caNo);
            map.put("MAINTABLE", "BO_EU_DBSPST");
            map.put("MAINTABLEDATA", zbfwMap);
            map.put("APPID", "com.awspaas.user.apps.app20230971602");
            map.put("UNIQUEKEY", "ZBFW");
            log.info("推送定标审批_推送中标范围数据接口"+organizationRoleUrl);
            log.info("推送定标审批_推送中标范围数据"+JSONObject.toJSONString(map));
            ApiInfoEnum apiInfoEnum = ApiInfoEnum.PUSH_ZBFW_TO_BPM;
            InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,map);
            String re = null;
            try{
                re = openClient.sendHttpPost(url, JSONObject.toJSONString(map),"application/json", headers);
            }catch (Exception e){
                log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
                interfaceLog.setStatus(ResultStatus.FAIL.toString());
                interfaceLog.setErrorInfo(e.getMessage());
            }finally {
                interfaceLog.setReturnInfo(re);
                interfaceLogService.createInterfaceLog(interfaceLog);
            }


            log.info("推送定标审批_推送中标范围返回信息"+re);

        }
    }

    @Override
    public void importExcelTemplate(HttpServletResponse response) throws IOException {
        ArrayList<OrganizationRoleDto> organizationRoleDtos = new ArrayList<>();
        ArrayList<OrganizationRoleUserDto> organizationRoleUserDtos = new ArrayList<>();
        String[] sheetNames = {"流程角色导入模板","流程角色", "流程角色用户"};
        List<List<? extends Object>> dataLists = new ArrayList<>();
        dataLists.add(organizationRoleDtos);
        dataLists.add(organizationRoleUserDtos);
        Class<? extends Object>[] clazz = new Class[]{OrganizationRoleDto.class, OrganizationRoleUserDto.class};
        ServletOutputStream outputStream = EasyExcelUtil.getServletOutputStream(response,sheetNames[0]);
        EasyExcelUtil.writeExcelWithModel(outputStream, sheetNames, dataLists, clazz);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ExcelImportCheck(sheets = {
            @ExcelSheetCheck(declareClass = OrganizationRoleDto.class, sheetIndex = 0, canBeEmpty = true,needFollowIndex = true),
            @ExcelSheetCheck(declareClass = OrganizationRoleUserDto.class, sheetIndex = 1, canBeEmpty = true,needFollowIndex = true)
    })
    public Map<String, Object> importExcel(MultipartFile file, Fileupload fileupload) throws IOException {
        // 校验传参
        String filename = file.getOriginalFilename();
        if (!EasyExcelUtil.isExcel(filename)) {
            throw new BaseException("请导入正确的Excel文件");
        }
        Map<String, Object> result = new HashMap<>(15);
        // 获取输入流
        InputStream inputStream = file.getInputStream();
        ExcelReader excelReader = EasyExcel.read(inputStream).build();

        AnalysisEventListenerImpl<Object> sheet1Listener = new AnalysisEventListenerImpl();
        AnalysisEventListenerImpl<Object> sheet2Listener = new AnalysisEventListenerImpl();

        ReadSheet sheet1 = EasyExcel.readSheet(0).head(OrganizationRoleDto.class).registerReadListener(sheet1Listener).build();
        ReadSheet sheet2 = EasyExcel.readSheet(1).head(OrganizationRoleUserDto.class).registerReadListener(sheet2Listener).build();
        excelReader.read(sheet1, sheet2);
        excelReader.finish();

        List<Object> organizationRoleObjs = sheet1Listener.getDatas();
        List<Object> organizationRoleUserObjs = sheet2Listener.getDatas();

        List<OrganizationRoleDto> organizationRoleDtos = new ArrayList<>();
        List<OrganizationRoleUserDto> organizationRoleUserDtos = new ArrayList<>();

        List<String> roleCodes = new ArrayList<>();
        List<String> orgCodes = new ArrayList<>();
        List<String> usernames = new ArrayList<>();

        AtomicBoolean errorFlag = new AtomicBoolean(false);
        Map<String,List<OrganizationRoleDto>> importRoleMap = new HashMap<>(15);
        if (CollectionUtils.isNotEmpty(organizationRoleObjs)) {
            for(Object obj:organizationRoleObjs){
                OrganizationRoleDto dto = (OrganizationRoleDto) obj;
                organizationRoleDtos.add(dto);
                if(StringUtils.isNotEmpty(dto.getRoleCode())){
                    roleCodes.add(dto.getRoleCode());
                }
                if(StringUtils.isNotEmpty(dto.getParentRoleCode())){
                    roleCodes.add(dto.getParentRoleCode());
                }
                if(StringUtils.isNotEmpty(dto.getOrganizationCode())){
                    orgCodes.add(dto.getOrganizationCode());
                }
            }
            importRoleMap = organizationRoleDtos.stream().collect(Collectors.groupingBy(OrganizationRoleDto::getRoleCode));
        }

        if (CollectionUtils.isNotEmpty(organizationRoleUserObjs)) {
            for(Object obj:organizationRoleUserObjs){
                OrganizationRoleUserDto dto = (OrganizationRoleUserDto) obj;
                organizationRoleUserDtos.add(dto);
                StringBuilder errmsg = new StringBuilder();
                if(StringUtils.isNotEmpty(dto.getRoleCode())){
                    roleCodes.add(dto.getRoleCode());
                }else{
                    errmsg.append("流程角色编码*不能为空;");
                }
                if(StringUtils.isNotEmpty(dto.getUserName())){
                    usernames.add(dto.getUserName());
                }else{
                    errmsg.append("人员账号*不能为空;");
                }

            }
        }
        Assert.isTrue(CollectionUtils.isNotEmpty(roleCodes),"流程角色编码不能为空");

        Map<String, SccPjOrganization> pjOrgMap = new HashedMap();
        Map<String, Organization> orgMap = new HashedMap();
        if(CollectionUtils.isNotEmpty(orgCodes)){
            orgMap = baseClient.getOrganizationsByCodes(orgCodes);

            LambdaQueryWrapper<SccPjOrganization> pjOrgQw = new LambdaQueryWrapper<>();
            pjOrgQw.in(SccPjOrganization::getOrganizationCode,orgCodes);
            List<SccPjOrganization> pjOrgList = sccPjOrganizationService.list(pjOrgQw);
            pjOrgMap = pjOrgList.stream().collect(Collectors.toMap(e->e.getOrganizationCode(), Function.identity(),(x,y)->x));
        }

        Map<String, User> userMap = new HashedMap();
        Map<String, SccPjUser> pjUserMap = new HashedMap();
        if(CollectionUtils.isNotEmpty(usernames)){
            userMap = rbacClient.getUserMapByNames(usernames);

            LambdaQueryWrapper<SccPjUser> pjUserQw = new LambdaQueryWrapper<>();
            pjUserQw.in(SccPjUser::getPersonnelNo,usernames);
            List<SccPjUser> pjUserList = iSccPjUserService.list(pjUserQw);
            pjUserMap = pjUserList.stream().collect(Collectors.toMap(e->e.getPersonnelNo(), Function.identity(),(x,y)->x));
        }

        LambdaQueryWrapper<SccPjOrganizationRole> qw = new LambdaQueryWrapper<>();
        qw.in(SccPjOrganizationRole::getRoleCode,roleCodes);
        List<SccPjOrganizationRole> roleList = this.list(qw);
        Map<String,SccPjOrganizationRole> dbRoleMap = roleList.stream().collect(Collectors.toMap(e->e.getRoleCode(), Function.identity(),(x,y)->y));
        List<SccPjOrganizationRole> addRoleList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(organizationRoleDtos)){
            for(OrganizationRoleDto dto:organizationRoleDtos){
                String roleCode = dto.getRoleCode();
                String orgCode = dto.getOrganizationCode();
                String parentRoleCode = dto.getParentRoleCode();
                StringBuilder errmsg = new StringBuilder();
                SccPjOrganizationRole pjRole = new SccPjOrganizationRole();
                BeanCopyUtil.copyProperties(pjRole,dto);
                pjRole.setRowId(IdGenrator.generate());
                pjRole.setUseFlag(YesOrNo.YES.getValue());
                addRoleList.add(pjRole);
                if(StringUtils.isNotEmpty(roleCode)){
                    if(importRoleMap.get(roleCode).size()>1){
                        errmsg.append("流程角色编码导入表存在重复;");
                    }
                    if(dbRoleMap.get(roleCode)!=null){
                        errmsg.append("流程角色编码数据库已存在;");
                    }
                }else{
                    errmsg.append("流程角色编码（唯一）*不能为空;");
                }

                if(StringUtils.isNotEmpty(orgCode)){
                    Organization org = orgMap.get(orgCode);
                    if(org==null){
                        errmsg.append("组织编码没有匹配到对应组织;");
                    }else{
                        pjRole.setSrmOrgnizationId(org.getOrganizationId());
                        pjRole.setGroupName(org.getOrganizationName());
                    }
                    SccPjOrganization pjOrg = pjOrgMap.get(orgCode);
                    if(pjOrg!=null){
                        pjRole.setHrOrgnizationId(pjOrg.getId());
                    }
                }else{
                    errmsg.append("组织编码不能为空;");
                }

                if(StringUtils.isNotEmpty(parentRoleCode)){
                    SccPjOrganizationRole dbRole = dbRoleMap.get(parentRoleCode);
                    List<OrganizationRoleDto> importRoles = importRoleMap.get(parentRoleCode);
                    if(dbRole!=null){
                        pjRole.setParentRoleCode(dbRole.getRoleCode());
                        pjRole.setParentRoleName(dbRole.getRoleName());
                    }else if(CollectionUtils.isNotEmpty(importRoles)){
                        pjRole.setParentRoleCode(importRoles.get(0).getRoleCode());
                        pjRole.setParentRoleName(importRoles.get(0).getRoleName());
                    }else {
                        errmsg.append("请先维护上级流程角色;");
                    }
                }
                if(StringUtils.isNotEmpty(errmsg.toString())){
                    errorFlag.set(true);
                    dto.setErrorMessage(errmsg.toString());
                }
            }
        }
        Map<String,SccPjOrganizationRole> addRoleMap = new HashMap<>(15);
        if(CollectionUtils.isNotEmpty(addRoleList)){
            addRoleMap = addRoleList.stream().collect(Collectors.toMap(e->e.getRoleCode(),Function.identity(),(x,y)->x));
        }
        List<SccPjOrganizationRoleUser> addRoleUserList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(organizationRoleUserDtos)){
            for(OrganizationRoleUserDto dto:organizationRoleUserDtos){
                StringBuilder errmsg = new StringBuilder();
                String roleCode = dto.getRoleCode();
                String username = dto.getUserName();
                SccPjOrganizationRoleUser roleUser = new SccPjOrganizationRoleUser();
                roleUser.setRowId(IdGenrator.generate());
                addRoleUserList.add(roleUser);
                if(StringUtils.isNotEmpty(roleCode)){
                    SccPjOrganizationRole role = addRoleMap.get(roleCode);
                    if(role==null){
                        role = dbRoleMap.get(roleCode);
                    }
                    if(role!=null){
                        roleUser.setOrganizationRoleId(role.getRowId());
                    }else{
                        errmsg.append("流程角色编码没有匹配到数据;");
                    }
                }else{
                    errmsg.append("流程角色编码不能为空;");
                }
                if(StringUtils.isNotEmpty(username)){
                  User user = userMap.get(username);
                  SccPjUser pjUser = pjUserMap.get(username);
                  if(user!=null){
                      roleUser.setUserName(username);
                      roleUser.setUserNickName(user.getNickname());
                      roleUser.setSrmUserId(user.getUserId());
                  }else{
                      errmsg.append("人员账号没有匹配到人员信息;");
                  }
                  if(pjUser!=null){
                      roleUser.setHrUserId(pjUser.getId());
                  }
                }else{
                    errmsg.append("人员账号不能为空;");
                }

                if(StringUtils.isNotEmpty(errmsg.toString())){
                    errorFlag.set(true);
                    dto.setErrorMessage(errmsg.toString());
                }
            }
        }
        if (errorFlag.get()) {
            String[] sheetNames = {filename.split("\\.")[0],"流程角色", "流程角色用户"};
            List<List<? extends Object>> dataLists = new ArrayList<>();
            dataLists.add(organizationRoleDtos);
            dataLists.add(organizationRoleUserDtos);
            Class<? extends Object>[] clazz = new Class[]{OrganizationRoleDto.class, OrganizationRoleUserDto.class};
            Fileupload wrongFile = EasyExcelUtil.uploadErrorFile(fileCenterClient, fileupload, dataLists, clazz, sheetNames, file.getOriginalFilename(), file.getContentType());
            result.put("status", YesOrNo.NO.getValue());
            result.put("message", "error");
            result.put("fileuploadId", wrongFile.getFileuploadId());
            result.put("fileName", wrongFile.getFileSourceName());
            result.put("dataLists", dataLists);
        }else{
            List<Long> bpmIds = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(addRoleList)){
                this.saveBatch(addRoleList);
                List<Long> bpmRoleIds = addRoleList.stream().map(SccPjOrganizationRole::getRowId).collect(Collectors.toList());
                bpmIds.addAll(bpmRoleIds);
            }
            if(CollectionUtils.isNotEmpty(addRoleUserList)){
                organizationRoleUserService.saveBatch(addRoleUserList);
                List<Long> bpmRoleIds = addRoleUserList.stream().map(SccPjOrganizationRoleUser::getOrganizationRoleId).collect(Collectors.toList());
                bpmIds.addAll(bpmRoleIds);
            }
            if(CollectionUtils.isNotEmpty(bpmIds)){
                BpmSccPjOrganizationRoleDto bpmDto = new BpmSccPjOrganizationRoleDto();
                bpmDto.setIds(bpmIds);
                bpmDto.setOperation("ADD");
                this.organizationRoleBpm(bpmDto);
            }


            result.put("status", YesOrNo.YES.getValue());
            result.put("message", "success");
        }
        return result;
    }

}
