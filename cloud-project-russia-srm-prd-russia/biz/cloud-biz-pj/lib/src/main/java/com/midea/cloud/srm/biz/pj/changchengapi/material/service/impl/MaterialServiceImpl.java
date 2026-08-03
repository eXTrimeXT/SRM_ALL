package com.midea.cloud.srm.biz.pj.changchengapi.material.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.srm.biz.pj.changchengapi.material.service.IMaterialService;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialParam;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialResultDto;
import com.midea.cloud.srm.model.pj.changchengapi.yangguan.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
@Slf4j
@Service
public class MaterialServiceImpl implements IMaterialService {

    @Value("${gwm.url.material-url}")
    private String materialUrl;
    @Value("${gwm.preappkey}")
    private String preappKey;
    @Value("${gwm.presecret}")
    private String presecret;

    @Value("${gwm.appkey}")
    private String appKey;
    @Value("${gwm.secret}")
    private String secret;

    @Override
    public ResultDTO<MaterialResultDto> page(MaterialParam materialParam) {
        String url = materialUrl;

        OpenClient openClient = new OpenClient(appKey,secret);



        String result = openClient.sendHttpPost(url,JSONObject.toJSONString(materialParam),"application/json");

        log.info("请求url："+url);
        log.info("返回结果："+result);

        ResultDTO<MaterialResultDto> resultDto = JSONObject.parseObject(result,ResultDTO.class);

        return resultDto;
    }
}
