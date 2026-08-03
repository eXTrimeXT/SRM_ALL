package com.midea.cloud.srm.biz.pj.changchengapi.companybigdata.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.biz.pj.changchengapi.companybigdata.CompanyBigDataService;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CompanyAQCApiDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class ICompanyBigDataServiceImpl implements CompanyBigDataService {

    @Value("${gwm.url.company-aqc}")
    private String companyAqcUrl;

    private static final String APPLICATION_JSON_MEDIA_TPYE = "application/json; charset=UTF-8";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 通过供应商名称去爱企查查询数据
     * @param companyNames 供应商名称
     * @return
     */
    @Override
    public List<CompanyAQCApiDTO> findAqcByNames(List<String> companyNames) {
        Assert.isTrue(companyNames!=null&&companyNames.size()>0,"供应商名称不能为空");

        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType(APPLICATION_JSON_MEDIA_TPYE);
        headers.setContentType(mediaType);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("Accept-Language", "zh-CN");

        JSONObject data = new JSONObject();
        data.put("data",companyNames);
        HttpEntity<String> entity = new HttpEntity(data.toJSONString(), headers);

        log.info("entity:"+JSONObject.toJSONString(entity));
        String responseStr = restTemplate.postForEntity(companyAqcUrl,entity,String.class).getBody();
        log.info("responseStr:"+JSONObject.toJSONString(entity));

        JSONObject response = JSONObject.parseObject(responseStr);

        List<CompanyAQCApiDTO> companyAqcApiDtos = new ArrayList<>();
        String statusText = "status";
        int successValue = 200;
        if(response.getInteger(statusText)==successValue){

            JSONArray jsonArray = response.getJSONArray("result");
            List<CompanyAQCApiDTO> finalCompanyAqcApiDtos = companyAqcApiDtos;
            jsonArray.forEach(jsonObject -> {
                JSONObject companyObject = (JSONObject)jsonObject;
                CompanyAQCApiDTO companyAQCApiDTO = null;
                try {
                    companyAQCApiDTO = companyObject.toJavaObject(CompanyAQCApiDTO.class);

                } catch (Exception e) {
                    /**用于解决此类数据结构：{"company": "河北少恩建设工程有限公司", "data": "没有该公司，请改日再试"}*/
                    companyAQCApiDTO = new CompanyAQCApiDTO();
                    companyAQCApiDTO.setCompany(companyObject.getString("company"));
                    companyAQCApiDTO.setData(new CompanyAQCApiDTO.ItemData());
                }
                finalCompanyAqcApiDtos.add(companyAQCApiDTO);
            });

        }
        return companyAqcApiDtos;
    }
}
