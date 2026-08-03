package com.midea.cloud.srm.biz.pj.job;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.biz.pj.hrusertemp.service.ISccPjUserTempService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.pj.hrusertemps.entity.SccPjUserTemp;
import com.midea.cloud.srm.model.pj.orguser.dto.UserResultDto;
import com.midea.cloud.srm.model.pj.orguser.dto.UserReturnDto;
import com.midea.cloud.srm.model.pj.orguser.dto.UserRowsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ex_liuxy46
 */
@Job("UserTempJob")
@Slf4j
public class UserTempJob implements ExecuteableJob {

    @Resource
    private ISccPjUserTempService sccPjUserTempService;

    @Value("${gwm.url.user-mployeeCompanyPage}")
    private String userUrl;

    private static final String PARAM_URL = "?page=%d&size=1000&organizationId=%S";
    private static final String PARAM_TIME_URL = "?page=%d&size=1000&organizationId=%S&latestUpdateTime=%S";

    private static final String LOCAL_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());
    private static final String PENDING = "PENDING";

    private static final String HR_ORG_ID = "HR_ORG_ID";

    private static final int ONE = 1;
    private static final int CODE_NUM = 200;
    private static final int TOTAL_NUM = 1000;

    @Value("${gwm.appkey}")
    private String appKey;

    @Value("${gwm.secret}")
    private String secret;

    @Resource
    private BaseClient baseClient;

    /**
     * @param params 参数
     * @return 返回
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BaseResult executeJob(Map<String, String> params) {
        try {
            SystemConfigureDTO systemConfigureDTO = baseClient.getSystemConfigure(HR_ORG_ID);
            OpenClient openClient = new OpenClient(appKey,secret);
            String url = getUrl(ONE, systemConfigureDTO.getParamValue());
            String result = openClient.sendHttpGet(url);
            UserReturnDto userReturnDto = JSONObject.parseObject(result, UserReturnDto.class);
            log.info(JSONObject.toJSONString(userReturnDto));
            if (userReturnDto.getCode() == CODE_NUM) {
                addUserTemp(userReturnDto);
                UserResultDto resultDto = userReturnDto.getResult();
                Integer total = resultDto.getTotal();
                Integer pageCount = resultDto.getPageCount();
                if (total > TOTAL_NUM) {
                    int startPageNum = 2;
                    for (int i = 0; i < pageCount; i++) {
                        UserReturnDto obj = getUserList(startPageNum, systemConfigureDTO.getParamValue());
                        if (obj.getCode() == CODE_NUM) {
                            addUserTemp(obj);
                            startPageNum++;
                        } else {
                            return BaseResult.build(ResultCode.UNKNOWN_ERROR, result);
                        }
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

    public String getUrl(int pageNum, String pa) {
        String[] sp = pa.split(",");
        String url;
        if (LocalDate.now().isAfter(LocalDate.parse(sp[1]))) {
            url = userUrl + String.format(PARAM_TIME_URL, pageNum, sp[0], LOCAL_DATE);
        } else {
            url = userUrl + String.format(PARAM_URL, pageNum, sp[0]);
        }
        return url;
    }

    public UserReturnDto getUserList(int pageNum, String pa) {
        OpenClient openClient = new OpenClient(appKey,secret);
        String url = getUrl(pageNum, pa);
        String result = openClient.sendHttpGet(url);
        UserReturnDto re = null;
        try {
            re = JSONObject.parseObject(result, UserReturnDto.class);
        } catch (Exception e) {
            log.info("获取到的蜂巢组织数据==={}", result);
        }
        return re;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void addUserTemp(UserReturnDto obj) {
        List<UserRowsDto> resultDto = obj.getResult().getRows();
        List<SccPjUserTemp> userList = new ArrayList<>();
        for (UserRowsDto userRowsDto : resultDto) {
            SccPjUserTemp userTemp = new SccPjUserTemp();
            userTemp.setId(userRowsDto.getId());
            userTemp.setChineseName(userRowsDto.getChineseName());
            userTemp.setPersonnelNo(userRowsDto.getPersonnelNo());
            userTemp.setIsFormal(userRowsDto.getPersonType());
            userTemp.setGroupId(userRowsDto.getOrganizationIds());
            userTemp.setUpdateTime(userRowsDto.getUpdateTime());
            userTemp.setProcessStatus(PENDING);
            userTemp.setIsDelete("0");
            userTemp.setState(ONE);
            userList.add(userTemp);
        }
        sccPjUserTempService.saveBatch(userList);
    }
}
