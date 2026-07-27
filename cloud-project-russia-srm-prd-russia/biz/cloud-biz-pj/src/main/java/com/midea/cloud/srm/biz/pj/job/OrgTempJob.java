package com.midea.cloud.srm.biz.pj.job;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.biz.pj.hrorganizationtemp.service.ISccPjOrganizationTempService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.pj.hrorganizationtemp.SccPjOrganizationTemp;
import com.midea.cloud.srm.model.pj.orguser.dto.OrgResultDto;
import com.midea.cloud.srm.model.pj.orguser.dto.OrgReturnDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ex_liuxy46
 */
@Job("OrgTempJob")
@Slf4j
public class OrgTempJob implements ExecuteableJob {

    @Resource
    private ISccPjOrganizationTempService sccPjOrganizationTempService;

    private static final int CODE_NUM = 200;

    private static final int BATCH_SIZE = 1000;

    @Value("${gwm.appkey}")
    private String appKey;

    @Value("${gwm.secret}")
    private String secret;

    @Resource
    private BaseClient baseClient;

    @Value("${gwm.url.org-companyPageByParentId}")
    private String orgUrl;

    private static final String PENDING = "PENDING";
    private static final String PARENT_ID = "ORG_PARENT_ID";

    /**
     * @param params 参数
     * @return 返回
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BaseResult executeJob(Map<String, String> params) {
        try {
            SystemConfigureDTO systemConfigureDTO = baseClient.getSystemConfigure(PARENT_ID);
            OpenClient openClient = new OpenClient(appKey,secret);
            String url = orgUrl + String.format("?parentId=%S", systemConfigureDTO.getParamValue());
            String result = openClient.sendHttpGet(url);
            log.info("返回的数据==={}", JSONObject.toJSONString(result));
            OrgReturnDto orgReturnDto = JSONObject.parseObject(result, OrgReturnDto.class);
            log.info("转换的数据==={}", JSONObject.toJSONString(orgReturnDto));
            long processGroupId = IdGenrator.generate();
            if (orgReturnDto.getCode() == CODE_NUM) {
                List<OrgResultDto> orgResultDto = orgReturnDto.getResult();
                if (CollectionUtils.isNotEmpty(orgResultDto)) {
                    List<OrgResultDto> resultList = dealOrgList(orgResultDto.get(0).getChildrenList(), new ArrayList<>());
                    log.info("最终的数据==={}", JSONObject.toJSONString(resultList));
                    orgResultDto.addAll(resultList);
                    List<SccPjOrganizationTemp> orgTempList = new ArrayList<>();
                    for (OrgResultDto resultDto : orgResultDto) {
                        SccPjOrganizationTemp sccPjUserTemp = new SccPjOrganizationTemp();
                        sccPjUserTemp.setId(resultDto.getId());
                        sccPjUserTemp.setGroupName(resultDto.getGroupName());
                        sccPjUserTemp.setGroupNameEn(resultDto.getGroupNameEn());
                        sccPjUserTemp.setParentId(resultDto.getParentId());
                        sccPjUserTemp.setShowOrder(resultDto.getShowOrder());
                        sccPjUserTemp.setGrade(resultDto.getBusinessType());
                        sccPjUserTemp.setProcessStatus(PENDING);
                        sccPjUserTemp.setProcessGroupId(processGroupId);
                        sccPjUserTemp.setDeleteFlag("false");
                        /** 预设SRM组织ID */
                        sccPjUserTemp.setPreOrganizationId(IdGenrator.generate());
                        orgTempList.add(sccPjUserTemp);
                    }
                    if (CollectionUtils.isNotEmpty(orgTempList)) {
                        sccPjOrganizationTempService.saveBatch(orgTempList, BATCH_SIZE);
                    }
                }
            } else {
                return BaseResult.build(ResultCode.UNKNOWN_ERROR, result);
            }
        } catch (Exception e) {
            return BaseResult.build(ResultCode.UNKNOWN_ERROR, e.getMessage());
        }
        return BaseResult.build(ResultCode.SUCCESS);
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
