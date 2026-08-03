package com.midea.cloud.srm.biz.pj.changchengapi.ai;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.pj.aihelper.BidReviewResDto;
import com.midea.cloud.srm.model.pj.aihelper.ScanFileResDto;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author GW00302625
 */
@Api(value = "AiController", tags = {"知识大脑"})
@RestController
@Slf4j
@RequestMapping("/external/ai")
public class AiController {
    private static final String MSG_KEY = "msg";
    private static final String MSG_ERROR = "解析中";
    @Autowired
    private RedisUtil redisUtil;

    private static final String SRM_REQ_AI_TOKEN = "SRM_REQ_AI_TOKEN";
    private static final String GRANT_TYPE = "appKey";
    private static final String USER_TYPE = "tenant";


    @Value("${gwm.gwkb.appkey}")
    private String appKey;
    @Value("${gwm.gwkb.secret}")
    private String secret;

    @Value("${gwm.gwkb.login-url}")
    private String loginUrl;

    @Value("${gwm.gwkb.fileCompare-url}")
    private String fileCompareUrl;

    @Value("${gwm.gwkb.bidReviewScanFile-url}")
    private String bidReviewScanFileUrl;
    @Value("${gwm.gwkb.bidReviewItemsQuotation-url}")
    private String bidReviewItemsQuotationUrl;


    @ApiOperation(value = "知识大脑接口", notes = "知识大脑接口", httpMethod = "POST")
    @PostMapping("/getToken")
    public String getToken() {
        //获取当前登陆用户
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        //判断系统用户 or 游客
        String partRedisKey = Objects.nonNull(loginAppUser) ? loginAppUser.getUsername():"tourist";
        String redisKey  = String.join(":",SRM_REQ_AI_TOKEN,partRedisKey);
        //缓存获取token
//        if(redisUtil.exists(redisKey)){
//            return redisUtil.get(redisKey);
//        }
        TreeMap<String, String> treeMap = Maps.newTreeMap();
        treeMap.put("grantType", GRANT_TYPE);
        treeMap.put("userType", USER_TYPE);
        treeMap.put("appKey", appKey);
        treeMap.put("jobNum", Objects.nonNull(loginAppUser) && Objects.equals(loginAppUser.getUserType(),UserType.BUYER.name()) ? loginAppUser.getUsername():"");
        treeMap.put("userId", Objects.nonNull(loginAppUser) ? loginAppUser.getUserId().toString():"");
        treeMap.put("dateTime", String.valueOf(System.currentTimeMillis()));

        String verifyStr  = treeMap.entrySet()
                .stream()
                .filter(e-> StringUtils.isNotBlank(e.getValue()))
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        treeMap.put("sign", Hex.encodeHexString(DigestUtils.md5(String.join("", verifyStr, secret)),false));

        OpenClient openClient = new OpenClient(appKey,secret);
        //请求知识大脑
        log.info("智能助手获取token params:{}", treeMap);
        String resultStr = openClient.sendHttpPost(loginUrl, JSONObject.toJSONString(treeMap),"application/json");
        JSONObject result = JSONObject.parseObject(resultStr);

        if (!Objects.equals(OpenClientConstant.CODE_SUCCESS,result.getString(OpenClientConstant.CODE_KEY))) {
            throw new BaseException("授权失败！" + result.get("msg"));
        }
        String accessToken = result.getJSONObject("data").getString("access_token");
//        Long expireIn = result.getJSONObject("data").getLong("expire_in");
//        redisUtil.set(redisKey,accessToken,expireIn);
        return accessToken;
    }

    @ApiOperation(value = "围串标识别结果接口", notes = "围串标识别结果接口", httpMethod = "POST")
    @PostMapping("/project/fileCompare")
    public JSONArray getFileCompare(@RequestParam Long projectId,
                                    @RequestParam String compareWordSizes) {
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.join(" ","Bearer",getToken()));

        Map<String, String> bodyMap = Maps.newHashMap();
        bodyMap.put("embedClient", "SRMclient");
        bodyMap.put("compareWordSizes", compareWordSizes);
        bodyMap.put("projectId", String.valueOf(projectId));

        OpenClient openClient = new OpenClient(appKey,secret);
        String resultStr = openClient.sendHttpPost(fileCompareUrl, JSONObject.toJSONString(bodyMap),"application/json", headers);

        JSONObject result = JSONObject.parseObject(resultStr);
        if (!Objects.equals(OpenClientConstant.CODE_SUCCESS,result.getString(OpenClientConstant.CODE_KEY))) {
            throw new BaseException("获取围串标对比结果失败" + result.get("msg"));
        }
        return result.getJSONArray("data");
    }



    @ApiOperation(value = "智能评标数据扫描件接口", notes = "智能评标数据扫描件接口", httpMethod = "POST")
    @PostMapping("/bidReview/scanFileList")
    public List<Long> scanFileList(@RequestParam Long projectId) {
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.join(" ","Bearer",getToken()));
        Map<String, String> bodyMap = Maps.newHashMap();
        bodyMap.put("embedClient", "SRMclient");
        bodyMap.put("projectId", String.valueOf(projectId));
        OpenClient openClient = new OpenClient(appKey,secret);
        String resultStr = openClient.sendHttpPost(bidReviewScanFileUrl, JSONObject.toJSONString(bodyMap),"application/json",headers);
        JSONObject result = JSONObject.parseObject(resultStr);
        if (!Objects.equals(OpenClientConstant.CODE_SUCCESS,result.getString(OpenClientConstant.CODE_KEY))) {
            throw new BaseException("智能评标数据扫描件接口" + result.get("msg"));
        }
        ScanFileResDto scanFileResDto = JSONUtil.toBean(result.getString("data"), ScanFileResDto.class);

        return Optional.ofNullable(scanFileResDto)
                .map(ScanFileResDto::getCompanyList)
                .map(companies -> companies.stream()
                        .flatMap(company -> Optional.ofNullable(company.getFileList())
                                .map(fileList -> fileList.stream())
                                .orElseGet(Stream::empty))
                        .map(ScanFileResDto.File::getFileId)
                        .collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    @ApiOperation(value = "智能评标获取评审项结果接口", notes = "智能评标获取评审项结果接口", httpMethod = "POST")
    @PostMapping("/bidReview/itemsQuotation")
    public BidReviewResDto itemsQuotation(@RequestParam Long projectId) {
        return getBidReviewResDto(projectId);
    }
    @ApiOperation(value = "智能评标评审进度接口", notes = "智能评标评审进度接口", httpMethod = "GET")
    @GetMapping("/bidReview/doneFlag")
    public Boolean doneFlag(@RequestParam(value = "projectId") Long projectId) {
        return Objects.nonNull(getBidReviewResDto(projectId));
    }

    private BidReviewResDto getBidReviewResDto(Long projectId) {
        Map<String, String> bodyMap = Maps.newHashMap();
        bodyMap.put("embedClient", "SRMclient");
        bodyMap.put("projectId", String.valueOf(projectId));
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization",String.join(" ","Bearer",getToken()));
        OpenClient openClient = new OpenClient(appKey,secret);
        String resultStr = openClient.sendHttpPost(bidReviewItemsQuotationUrl, JSONObject.toJSONString(bodyMap),"application/json",headers);
        JSONObject result = JSONObject.parseObject(resultStr);

        if (!Objects.equals(OpenClientConstant.CODE_SUCCESS,result.getString(OpenClientConstant.CODE_KEY))) {
            if (Objects.equals(MSG_ERROR,result.getString(MSG_KEY))) {
                return null;
            }
            log.info("智能评标获取评审项结果接口:" + result.toJSONString());
            throw new BaseException("智能评标获取评审项结果接口:" + result.getString(MSG_KEY));
        }
        BidReviewResDto data = JSONUtil.toBean(result.getString("data"), BidReviewResDto.class);
        if (Objects.isNull(data) || CollectionUtils.isEmpty(data.getReviewItemList())) {
            return data;
        }
        data.getReviewItemList().forEach(reviewItem -> {
            reviewItem.getCompanyList().forEach(company -> {
                List<BidReviewResDto.AnswerAndQuotation> answerAndQuotationList = company.getAnswerAndQuotationList().stream()
                        .filter(answerAndQuotation -> StringUtils.isNotBlank(answerAndQuotation.getAnswer()))
                        .collect(Collectors.toList());

                answerAndQuotationList.forEach(answer ->{
                    List<BidReviewResDto.Quotation> quotationList = answer.getQuotationList().stream()
                            .filter(q -> !CollectionUtils.isEmpty(q.getLocation()))
                            .collect(Collectors.toList());
                    answer.setQuotationList(quotationList);
                });

                company.setAnswerAndQuotationList(answerAndQuotationList);
            });
        });

        return data;
    }
}
