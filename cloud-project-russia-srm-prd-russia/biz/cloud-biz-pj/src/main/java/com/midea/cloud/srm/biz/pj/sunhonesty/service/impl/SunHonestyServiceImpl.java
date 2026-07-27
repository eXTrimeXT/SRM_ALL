package com.midea.cloud.srm.biz.pj.sunhonesty.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.api.ResultStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.pj.utils.AesEncryptUtil;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.biz.pj.sunhonesty.service.SunHonestyService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.pj.rbac.RbacExtClient;
import com.midea.cloud.srm.feign.pj.sou.SouSignClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.common.CommonResponseDto;
import com.midea.cloud.srm.model.pj.siss.dto.SunHonestyReturnDto;
import com.midea.cloud.srm.model.pj.siss.dto.SunHonestySupDto;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sunhonesty.dto.SunHonestyExam;
import com.midea.cloud.srm.model.pj.sunhonesty.entity.SccPjExamRecord;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.rbac.user.entity.UserThird;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


/** 对接阳光诚信自助平台相关
 * @author GW00311146
 */
@Slf4j
@Service
public class SunHonestyServiceImpl  implements SunHonestyService {

    public static final String CODE = "code";
    public static final String COMPANY_NAME = "companyName";
    /**
     * 阳光诚信字典编码
     */
    public static final String DICT_CODE = "SUNHONESTY_EXAM_INTERVAL";
    /**
     * 阳光诚信考试间隔
     */
    public static final String DICT_ITEM_CODE = "INTERVAL_DAYS";
    /**
     * 阳光诚信渠道标识
     */
    private static final String YGCX_SISS = "SISS";
    /**
     * 考试通过次数
     */
    private static final Long EXAM_NUM = 2L;
    @Value("${gwm.appkey}")
    private String appKey;
    @Value("${gwm.secret}")
    private String secret;
    @Value("${gwm.url.sunHonestySysPushUser-url}")
    private String sunHonestySysPushUserUrl;
    @Value("${gwm.url.sunHonestySys-getPreAuthCode-url}")
    private String getPreAuthCodeUrl;
    @Value("${gwm.url.sunHonestySys-platformCode}")
    private String platformCode;
    @Value("${gwm.url.sunHonestySys-encryptKey}")
    private String encryptKey;
    @Value("${gwm.url.sunHonestySys-externalSso-url}")
    private String externalSsoUrl;

    @Resource
    private IInterfaceLogService interfaceLogService;
    @Resource
    private BaseClient baseClient;
    @Resource
    private SouSignClient souSignClient;
    @Resource
    private QlService qlService;
    @Resource
    private RbacExtClient rbacExtClient;

    @Override
    public List<SunHonestyReturnDto> pushCompanyUser(List<SunHonestySupDto> sunHonestySupDtos) {
        if (CollectionUtils.isEmpty(sunHonestySupDtos)){
            return Lists.newArrayList();
         }
        AssertUtils.isTrue(sunHonestySupDtos.size()<50, "数据过多");

        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(ApiInfoEnum.SUN_HONESTY_COMPANY_SYN);
        String result = null;
        try {
            OpenClient openClient = new OpenClient(appKey,secret);
            interfaceLog.setServiceInfo(JSONObject.toJSONString(sunHonestySupDtos));
            result  = openClient.sendHttpPost(sunHonestySysPushUserUrl, JSONObject.toJSONString(sunHonestySupDtos),"application/json");
            interfaceLog.setReturnInfo(result);
        } catch (Exception e) {
            log.info(ApiInfoEnum.SUN_HONESTY_COMPANY_SYN.getServiceName() + "报错:" + e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLog.setErrorInfo(e.getMessage());
        }finally {
            interfaceLog.setReturnInfo(result);
            interfaceLogService.createInterfaceLog(interfaceLog);
        }
        if (ResultStatus.FAIL.toString().equals(interfaceLog.getStatus())) {
            throw new BaseException("调用接口异常");
        }
        CommonResponseDto<List<SunHonestyReturnDto>> commonResponseDto = CommonResponseDto.buildResp(SunHonestyReturnDto.class,result);

        if (OpenClientConstant.CODE_SUCCESS_INT.compareTo(commonResponseDto.getCode()) != 0) {
            throw new BaseException("请求阳光诚信接口返回异常：" + commonResponseDto.getMsg());
        }
        return commonResponseDto.getData().stream()
                .peek(e-> e.setResultId(Objects.isNull(e.getResultId()) ? -1:e.getResultId()))
                .collect(Collectors.toList());



    }


    @Override
    public String getUrlForSunHonestySys(User u) throws Exception {
        //校验用户是否推送过，推送过才可以免登陆
        
        OpenClient openClient = new OpenClient(appKey,secret);
        StringJoiner stringJoiner = new StringJoiner("/");
        stringJoiner.add(u.getUsername());
        stringJoiner.add(u.getCompanyName());
        //请求参数：
        //data：加密后数据（‘手机号/公司名称’）
        //platformCode:平台码 9913cc1c46a546ff9cc54746d0d82a43  加密key：483bf82e315b4600
        String data = AesEncryptUtil.encrypt(stringJoiner.toString(),encryptKey);

        Map<String , String> bodyMap = new HashMap<>(8);
        bodyMap.put("data",data);
        bodyMap.put("platformCode",platformCode);
        //获取认证code
        String result = openClient.sendHttpPost(getPreAuthCodeUrl,bodyMap);
//        //返回示例：
//        //{"msg": "操作成功",
//        //    "code": 200,
//        //    "data": {
//        //        "code": "授权码"
//        //    }
//        //}
        JSONObject resultOb = JSONObject.parseObject(result);
        if (!Objects.equals(OpenClientConstant.CODE_SUCCESS,resultOb.getString(CODE))) {
            throw new BaseException("授权失败！" + resultOb.get("msg"));
        }
//        String redirectUri = String.format(externalSsoUrl, URLEncoder.encode(resultOb.getJSONObject("data").getString(code), "UTF-8"), platformCode);
        return String.format(externalSsoUrl, URLEncoder.encode(resultOb.getJSONObject("data").getString(CODE),"UTF-8"), platformCode);
    }
    @Override
    public SunHonestyExam checkExam() {
        SunHonestyExam sunHonestyExam=new SunHonestyExam();
        //获取当前用户
        User u= AppUserUtil.getLoginAppUser();
        //查询三方用户表
        UserThird userThird=new UserThird();
        userThird.setUserAccount(u.getUsername());
        userThird.setThirdSource(YGCX_SISS);
        List<UserThird> userThirdList = rbacExtClient.selectUserThird(userThird);
        if(userThirdList.size()>0 ){
            //如果已存在并且resultID<=0 默认已考试
            if(Long.parseLong(userThirdList.get(0).getThirdUnionId())<=0) {
                sunHonestyExam.setIsExam(YesOrNo.YES.getValue());
                return sunHonestyExam;
            }
            //如果resultID>0 代表已同步阳光诚信
            if(Long.parseLong(userThirdList.get(0).getThirdUnionId())>0){
                //获取考试间隔
                List<DictItem> dictList = baseClient.listDictItemByDictCode(DICT_CODE);
                for(DictItem sictItem:dictList){
                    if(sictItem.getDictItemCode().equals(DICT_ITEM_CODE)){
                        //查询考试记录表  根据 userid及 考试间隔 ， 查询 考试记录表 此期间内 最新考试记录是否通过
                        sunHonestyExam=isExam(u.getUsername(),Long.parseLong(sictItem.getDictItemMark()));
                    }
                }
            }
        }else{
            sunHonestyExam.setIsExam(YesOrNo.YES.getValue());
        }

        return sunHonestyExam;
    }
    private SunHonestyExam isExam(String userName,long days) {
        SunHonestyExam sunHonestyExam=new SunHonestyExam();
        //查询这个用户最新的考试记录
        List<SccPjExamRecord> sccPjExamRecordList = qlService.queryByWrapper(QlWrappers.query(SccPjExamRecord.class)
                .eq(SccPjExamRecord::getUsername, userName)
                .orderByDesc(SccPjExamRecord::getCompletedDate), SccPjExamRecord.class);
        //如果最新记录已通过，则验证
        if(sccPjExamRecordList.size()>0){
            //如果考试两次通过，则不用考试
            long num = sccPjExamRecordList.stream().filter(e -> "Y".equals(e.getCompleted())).count();
            if(num>=EXAM_NUM){
                sunHonestyExam.setIsExam(YesOrNo.YES.getValue());
                return sunHonestyExam;
            }
            if(sccPjExamRecordList.get(0).getCompleted().equals(YesOrNo.NO.getValue())){
                sunHonestyExam.setIsExam(YesOrNo.NO.getValue());
                return sunHonestyExam;
            }
            long diff = Math.abs(System.currentTimeMillis() - sccPjExamRecordList.get(0).getCompletedDate().getTime());
            long diffDays = diff / (24 * 60 * 60 * 1000);
            if(diffDays<=days){
                sunHonestyExam.setIsExam(sccPjExamRecordList.get(0).getCompleted());
            }else{
                //查询供应商ID是否在时间范围内投过标，如果投过也不用考试
                List<SouOrder> list=souSignClient.getLastDateBySup(Long.parseLong(sccPjExamRecordList.get(0).getCompanyId()));
                if(list.size()==0){
                    sunHonestyExam.setIsExam(YesOrNo.NO.getValue());
                }else{
                    long submitTimeDiff = Math.abs(System.currentTimeMillis() - list.get(0).getSubmitTime().getTime());
                    long submitTimeDiffDays = submitTimeDiff / (24 * 60 * 60 * 1000);
                    if(submitTimeDiffDays<=days){
                        sunHonestyExam.setIsExam(YesOrNo.YES.getValue());
                    }else{
                        sunHonestyExam.setIsExam(YesOrNo.NO.getValue());
                    }

                }

                return sunHonestyExam;
            }
        }else{
            sunHonestyExam.setIsExam(YesOrNo.NO.getValue());
        }
        return sunHonestyExam;
    }
}
