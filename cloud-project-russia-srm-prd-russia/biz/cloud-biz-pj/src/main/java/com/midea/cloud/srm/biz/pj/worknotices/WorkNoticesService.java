package com.midea.cloud.srm.biz.pj.worknotices;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;


import java.util.List;

/**
 * @author huangbf3
 */
public interface WorkNoticesService {
    /**
     * 备注
     * @param content
     * @param userList
     * @return
     */
    @ApiOperation("钉钉消息-发送工作通知")
     JSONObject workNotices(String content,List<String> userList) ;

    }
