package com.midea.cloud.srm.biz.pj.changchengapi.black.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.srm.biz.pj.changchengapi.black.service.IBlackCompanyService;
import com.midea.cloud.srm.model.pj.changchengapi.yangguan.BlackCompanyInfo;
import com.midea.cloud.srm.model.pj.changchengapi.yangguan.ResultDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class BlackCompanyServiceImpl implements IBlackCompanyService {

    @ApiModelProperty("通过信用代码获取公司信息")
    @Value("${gwm.url.blackcompany-info}")
    private String blackcompanyInfoUrl;

    @Value("${gwm.appkey}")
    private String appKey;
    @Value("${gwm.secret}")
    private String secret;


    @Override
    public BlackCompanyInfo blackcompanyInfo(String taxCode) {
        String url = blackcompanyInfoUrl;

        url += "?taxCode="+taxCode;

        OpenClient openClient = new OpenClient(appKey,secret);
        String result = openClient.sendHttpGet(url);

        log.info(result);

        ResultDTO resultDTO = JSONObject.parseObject(result,ResultDTO.class);
        BlackCompanyInfo blackCompanyInfo = JSONObject.parseObject(JSONObject.toJSONString(resultDTO.getResult()), BlackCompanyInfo.class);
        return blackCompanyInfo;
    }
}
