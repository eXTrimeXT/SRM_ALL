package com.midea.cloud.srm.biz.pj.purveyor.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.srm.biz.pj.purveyor.PurveyorService;
import com.midea.cloud.srm.model.pj.sup.company.entity.PurveyorRootDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class PurveyorServiceImpl implements PurveyorService {

    @Value("${gwm.appkey}")
    private String appKey;

    @Value("${gwm.secret}")
    private String secret;

    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;
    /** 根据社会信用代码查询供应商信息接口url */
    @Value("${gwm.url.searchListByTaxCodes-url}")
    private String searchListByTaxCodesUrl;

    @Override
    public PurveyorRootDTO searchListByTaxCodes(List<String> taxCode, String region) {
        Assert.isTrue(taxCode!=null && taxCode.size()>0,"社会信用代码不能为空");
        JSONObject inputObject = new JSONObject();
        inputObject.put("taxCode",taxCode);
        inputObject.put("region",region);

        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
//        沙盒环境
        OpenClient openClient = new OpenClient(appKey,secret);
        String result = openClient.sendHttpPost(searchListByTaxCodesUrl,inputObject.toString(),"application/json",headers);

        PurveyorRootDTO purveyorRootDTO = JSONObject.parseObject(result,PurveyorRootDTO.class);

        return  purveyorRootDTO ;
    }
}
