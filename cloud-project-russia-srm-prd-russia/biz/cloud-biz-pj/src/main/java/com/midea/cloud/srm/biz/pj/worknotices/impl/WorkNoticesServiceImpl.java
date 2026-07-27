package com.midea.cloud.srm.biz.pj.worknotices.impl;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IDingdingLogService;
import com.midea.cloud.srm.biz.pj.worknotices.WorkNoticesService;
import com.midea.cloud.srm.model.pj.api.interfacelog.entity.DingdingLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangbf3
 */
@Slf4j
@Service
@Primary
public class WorkNoticesServiceImpl implements WorkNoticesService {

    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;

    @Value("${gwm.appkey}")
    private String appKey;

    @Value("${gwm.secret}")
    private String secret;

    @Value("${gwm.url.dingding-workNotices-url}")
    private String workNoticesUrl;

    @Autowired
    private IDingdingLogService iDingdingLogService;

    /**
     * 钉钉消息-发送工作通知,入参为内容和员工工号列表
     * **/
    @Override
    public JSONObject workNotices(String content, List<String> userList) {
        JSONObject  root = new JSONObject();
        /*类型（0:钉钉）该接口需传0 */
        int messagetype  = 0;
        /*消息类型（0:文本text,1:图片image,2:链接link,3:文件file,4:语音voice,6:markdown消息,7:actionCard卡片消息） */
        int msgtype = 0;
        root.put("messagetype",messagetype);
        root.put("msgtype",msgtype);
        root.put("content",content);
        root.put("userList",userList);

        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);

        OpenClient openClient = new OpenClient(appKey,secret);
        JSONObject jsonObject = null;
        DingdingLog dingdingLog = new DingdingLog();
        try{
            dingdingLog.setParam(content);
            dingdingLog.setDingdingLogId(IdGenrator.generate());
            iDingdingLogService.save(dingdingLog);
            jsonObject = JSONObject.parseObject(openClient.sendHttpPost(workNoticesUrl,root.toString(),"application/json",headers));
            dingdingLog.setReturnStr(jsonObject.toJSONString());
        }catch (Exception e){
            log.info("钉钉推送失败:{}",e.getMessage());
            dingdingLog.setReturnStr(e.getMessage().substring(0,1000));
        }finally {
            iDingdingLogService.updateById(dingdingLog);
        }

        return jsonObject;
     }


}
