package com.midea.cloud.srm.biz.pj.changchengapi;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.srm.biz.pj.base.organization.service.IOrganizationService;
import com.midea.cloud.srm.biz.pj.rbac.user.IUserService;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.rbac.iam.IamLoginInfo;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangbf3
 * 长城接口测试
 */
@Slf4j
@RestController
@RequestMapping("/openClientTest")
public class OpenClientTestController {

    @Value("${gwm.appkey}")
    private String appKey;
    @Value("${gwm.secret}")
    private String secret;
    @Value("${gwm.preappkey}")
    private String preappKey;
    @Value("${gwm.presecret}")
    private String presecret;
    @ApiModelProperty("组织列表")
    @Value("${gwm.url.org-list}")
    private String orgListUrl;

    @ApiModelProperty("人员列表")
    @Value("${gwm.url.employee-list}")
    private String employeeListUrl;

    @ApiModelProperty("创建流程")
    @Value("${gwm.url.flow-create}")
    private String flowCreateUrl;

    @ApiModelProperty("更新流程")
    @Value("${gwm.url.flow-update}")
    private String flowUpdateUrl;

    @ApiModelProperty("提交流程")
    @Value("${gwm.url.flow-resubmit}")
    private String flowResubmitUrl;

    @ApiModelProperty("获取审批历史")
    @Value("${gwm.url.process-comment-info}")
    private String processCommentInfoUrl;

    @ApiModelProperty("通过信用代码获取公司信息")
    @Value("${gwm.url.blackcompany-info}")
    private String blackcompanyInfoUrl;

    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;

    @Autowired
    private IOrganizationService iOrganizationService;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private IUserService iUserService;

    /**
     *
     * @param page
     * @param size
     * @param latestUpdateTime 最近更新日期(大于最近更新日期，格式：yyyy-MM-dd)
     * @return
     */
    @ApiOperation(value = "组织列表接口")
    @GetMapping("/hr/staff/domain/org/list")
    public String orgList(@RequestParam(value = "page")String page,@RequestParam(value = "size")String size,
                        @RequestParam(value = "latestUpdateTime",required = false)String latestUpdateTime) {
        String url = orgListUrl;

        url += "?page="+page+"&size="+size;
        if(StringUtils.isNotBlank(latestUpdateTime)){
            url+="&latest_update_time="+latestUpdateTime;
        }

        OpenClient openClient = new OpenClient(appKey,secret);
        String result = openClient.sendHttpGet(url);

        log.info(result);

        return result;
    }


    /**
     *
     * @param page
     * @param size
     * @param latestUpdateTime 仅限晚上使用）最近更新日期(大于最近更新日期，格式：yyyy-MM-dd)，****首次初始化后，必传，传递前一天更新时间即可****
     * @return
     */
    @ApiOperation(value = "人员列表接口")
    @GetMapping("/hr/staff/domain/employee/list")
    public String employeeList(@RequestParam(value = "page")String page,@RequestParam(value = "size")String size,
                        @RequestParam(value = "latestUpdateTime",required = false)String latestUpdateTime) {
        String url = employeeListUrl;

        url += "?page="+page+"&size="+size;
        if(StringUtils.isNotBlank(latestUpdateTime)){
            url+="&latest_update_time="+latestUpdateTime;
        }

        OpenClient openClient = new OpenClient(appKey,secret);

        String result = openClient.sendHttpGet(url);

        log.info(result);

        return result;
    }


    @ApiOperation(value = "创建流程")
    @PostMapping("/public/flow/native/createProcess")
    public String createProcess(@RequestBody JSONObject requestJsn,@RequestParam("dataId") String dataId,@RequestParam("userId") String userId) {
        String url = flowCreateUrl;

        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
        headers.put("USERID",userId);
        headers.put("DATA-ID",dataId);

        OpenClient openClient = new OpenClient(preappKey,presecret);
        String result = openClient.sendHttpPost(url,requestJsn.toString(),"application/json",headers);

        log.info("请求参数:"+requestJsn.toString());
        log.info("返回结果:"+result);

        return result;
    }

    @ApiOperation(value = "更新流程")
    @PostMapping("/public/flow/native/updateBoData")
    public String updateBoData(@RequestBody JSONObject requestJsn,@RequestParam("dataId") String dataId) {
        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
        headers.put("USERID","GW00244106");
        headers.put("DATA-ID",dataId);

        String url = flowUpdateUrl;
        OpenClient openClient = new OpenClient(preappKey,presecret);

        String result = openClient.sendHttpPost(url,requestJsn.toString(),"application/json",headers);

        log.info(result);

        return result;
    }

    @ApiOperation(value = "驳回后重新提交流程")
    @PostMapping("/public/flow/native/resubmitProcess")
    public String resubmitProcess(@RequestBody JSONObject requestJsn,@RequestParam("dataId") String dataId) {

        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
        headers.put("USERID","GW00244106");
        headers.put("DATA-ID",dataId);

        String url = flowResubmitUrl;

        OpenClient openClient = new OpenClient(preappKey,presecret);

        String result = openClient.sendHttpPost(url,requestJsn.toString(),"application/json",headers);

        log.info(result);

        return result;
    }


    @ApiOperation(value = "查询审批记录")
    @RequestMapping("/pre/public/flow/common/comment/getCommentAndTodoTaskList")
    public String getCommentAndTodoTaskList(@RequestBody JSONObject requestJsn) {
        String url = processCommentInfoUrl;

        OpenClient openClient = new OpenClient(preappKey,presecret);



        String result = openClient.sendHttpPost(url,requestJsn.toString(),"application/json");

        log.info("请求url："+url);
        log.info("返回结果："+result);

        return result;
    }


    @ApiOperation(value = "通过信用代码获取公司信息")
    @GetMapping("/public/mdm/sun/blackcompany/info")
    public String blackcompanyInfo(@RequestParam(value = "taxCode")String taxCode) {
        String url = blackcompanyInfoUrl;

        url += "?taxCode="+taxCode;

        OpenClient openClient = new OpenClient(appKey,secret);
        String result = openClient.sendHttpGet(url);

        log.info(result);

        return result;
    }

    @ApiOperation(value = "获取Token")
    @GetMapping("/getToken")
    public String getToken(HttpServletRequest request,@RequestParam("username") String username) throws Exception {
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()){
            String headerKey = headers.nextElement();
            String headerValue = request.getHeader(headerKey);
            log.info("headerKey:"+headerKey+" headerValue:"+headerValue);
        }

        IamLoginInfo accessToken = rbacClient.getAccessTokenByUsername(username);
        log.info("accessToken");

        log.info("headers:"+JSONObject.toJSONString(headers));
        return accessToken.getIdmToken();
    }
}
