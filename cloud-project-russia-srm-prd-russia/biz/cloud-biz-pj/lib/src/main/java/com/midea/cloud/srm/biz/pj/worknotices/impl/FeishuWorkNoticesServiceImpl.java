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
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huangbf3
 */

@Slf4j
@Service
public class FeishuWorkNoticesServiceImpl implements WorkNoticesService {
    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;

    @Value("${gwm.appkey}")
    private String appKey;

    @Value("${gwm.secret}")
    private String secret;

    @Value("${gwm.url.feishu-workNotices-url}")
    private String workNoticesUrl;

    @Autowired
    private IDingdingLogService iDingdingLogService;

    private static final String CONTENT_TEXT = "{\"text\":\"%s  \\n消息由【srm-慧采云】发送\"}";

    /**
     * 钉钉消息-发送工作通知,入参为内容和员工工号列表
     * **/
    @Override
    public JSONObject workNotices(String content, List<String> userList) {

        long requestNo = IdGenrator.generate();

        JSONObject  root = new JSONObject();
        root.put("content",String.format(CONTENT_TEXT,content));
        root.put("msgType","text");
        root.put("receiveIdList",userList);
        root.put("receiveIdType","user_id");
        root.put("requestNo",requestNo);

        OpenClient openClient = new OpenClient(appKey,secret);
        JSONObject jsonObject = null;
        DingdingLog dingdingLog = new DingdingLog();
        try{
            dingdingLog.setDingdingLogId(requestNo);
            iDingdingLogService.save(dingdingLog);
            jsonObject = JSONObject.parseObject(openClient.sendHttpPost(workNoticesUrl,root.toString(),"application/json"));
            dingdingLog.setReturnStr(jsonObject.toJSONString());
        }catch (Exception e){
            log.error("飞书推送失败",e);
            dingdingLog.setReturnStr(e.getMessage().substring(0,1000));
        }finally {
            iDingdingLogService.updateById(dingdingLog);
        }

        return jsonObject;
     }


}
