package com.midea.cloud.srm.biz.pj.orguser.controller;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.srm.biz.pj.hrorganizationtemp.service.ISccPjOrganizationTempService;
import com.midea.cloud.srm.biz.pj.hrusertemp.service.ISccPjUserTempService;
import com.midea.cloud.srm.model.pj.hrusertemps.entity.SccPjUserTemp;
import com.midea.cloud.srm.model.pj.hrorganizationtemp.SccPjOrganizationTemp;
import com.midea.cloud.srm.model.pj.orguser.dto.*;
import io.lettuce.core.ScriptOutputType;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ex_liuxy46
 */
@Slf4j
@RestController
@RequestMapping("/external/org/user")
public class OrgUserController {

    @Resource
    private ISccPjUserTempService sccPjUserTempService;

    private static final String USER_URL = "https://gwapi.gwm.cn/sandbox/hr/staff/roster/open/employee/company/pageCustomEmployee";

    private static final int NUM_TWO_HUNDRED = 200;

    private static final int NUM_THOUSAND = 1000;



    @ApiOperation(value = "分页查询子公司用户列表", notes = "分页查询子公司用户列表")
    @PostMapping("/pageCustomEmployee")
    public String pageCustomEmployee() {
        OpenClient openClient = new OpenClient("X63T8A0BM648", "00cc14bc145a43dc816fa5750a0a7324");
        String url = USER_URL + "?page=1&size=1000&organizationId=50000169";
        String result = openClient.sendHttpGet(url);
        UserReturnDto userReturnDto = JSONObject.parseObject(result, UserReturnDto.class);
        addUserTemp(userReturnDto);
        if (userReturnDto.getCode() == NUM_TWO_HUNDRED) {
            UserResultDto resultDto = userReturnDto.getResult();
            Integer total = resultDto.getTotal();
            Integer pageCount = resultDto.getPageCount();
            if (total > NUM_THOUSAND) {
                int startPageNum = 2;
                for (int i = 0; i < pageCount; i++) {
                    UserReturnDto obj = getUserList(startPageNum);
                    addUserTemp(obj);
                    startPageNum++;
                }
            }
        } else {
            return result;
        }
        return result;
    }



    public UserReturnDto getUserList(int pageNum) {
        OpenClient openClient = new OpenClient("X63T8A0BM648","00cc14bc145a43dc816fa5750a0a7324");
        String url = USER_URL + String.format("?page=%d&size=1000&organizationId=50000169", pageNum);
        String result = openClient.sendHttpGet(url);
        UserReturnDto re = null;
        try {
            re = JSONObject.parseObject(result, UserReturnDto.class);
        } catch (Exception e) {
            log.info("获取到的蜂巢组织数据==={}", result);
        }
        return re;
    }

    public void addUserTemp(UserReturnDto obj) {
        List<UserRowsDto> resultDto = obj.getResult().getRows();
        List<SccPjUserTemp> userList = new ArrayList<>();
        for (UserRowsDto userRowsDto : resultDto) {
            SccPjUserTemp userTemp = new SccPjUserTemp();
            userTemp.setId(userRowsDto.getId());
            userTemp.setChineseName(userRowsDto.getChineseName());
            userTemp.setPersonnelNo(userRowsDto.getPersonnelNo());
            //没有公司
            userTemp.setIsFormal(userRowsDto.getPersonType());
            userTemp.setGroupId(userRowsDto.getOrganizationIds());
            userTemp.setProcessMessage(userRowsDto.getRemark());
            userTemp.setProcessDate(userRowsDto.getCreateTime());
            userTemp.setUpdateTime(userRowsDto.getUpdateTime());
            userList.add(userTemp);
        }
        sccPjUserTempService.saveBatch(userList);
    }

    @Resource
    private ISccPjOrganizationTempService sccPjOrganizationTempService;

    @ApiOperation(value = "分页查询子公司用户列表", notes = "分页查询子公司用户列表")
    @PostMapping("/getOrgTreeByParentId")
    public String getOrgTreeByParentId() {
        OpenClient openClient = new OpenClient("X63T8A0BM648","00cc14bc145a43dc816fa5750a0a7324");
        String url = "https://gwapi.gwm.cn/sandbox/hr/staff/roster/open/organization/company/getOrgTreeByParentId?parentId=50000088";
        String result = openClient.sendHttpGet(url);
        log.info(result);
        OrgReturnDto orgReturnDto = JSONObject.parseObject(result, OrgReturnDto.class);
        log.info(JSONObject.toJSONString(orgReturnDto));
        if (orgReturnDto.getCode() == NUM_TWO_HUNDRED) {
            List<OrgResultDto> orgResultDto = orgReturnDto.getResult();
            if (CollectionUtils.isNotEmpty(orgResultDto)) {
                List<OrgResultDto> resultList = dealOrgList(orgResultDto.get(0).getChildrenList(), new ArrayList<>());
                log.info(JSONObject.toJSONString(resultList));
                List<SccPjOrganizationTemp> orgTempList = new ArrayList<>();
                for (OrgResultDto resultDto : resultList) {
                    SccPjOrganizationTemp sccPjUserTemp = new SccPjOrganizationTemp();
                    sccPjUserTemp.setId(resultDto.getId());
                    sccPjUserTemp.setGroupName(resultDto.getGroupName());
                    sccPjUserTemp.setGroupNameEn(resultDto.getGroupNameEn());
                    sccPjUserTemp.setParentId(resultDto.getParentId());
                    sccPjUserTemp.setShowOrder(resultDto.getShowOrder());
                    orgTempList.add(sccPjUserTemp);
                }
                if (CollectionUtils.isNotEmpty(orgTempList)) {
                    sccPjOrganizationTempService.saveBatch(orgTempList, NUM_THOUSAND);
                }
            }
        }
        return null;
    }

    public List<OrgResultDto> dealOrgList(List<OrgResultDto> paList, List<OrgResultDto> reList) {
        if (CollectionUtils.isNotEmpty(paList)) {
            for (OrgResultDto orgResultDto : paList) {
                if (CollectionUtils.isNotEmpty(orgResultDto.getChildrenList())) {
                    dealOrgList(orgResultDto.getChildrenList(), reList);
                }
                orgResultDto.setChildrenList(new ArrayList<>());
                reList.add(orgResultDto);
            }
        }
        return reList;
    }
}
