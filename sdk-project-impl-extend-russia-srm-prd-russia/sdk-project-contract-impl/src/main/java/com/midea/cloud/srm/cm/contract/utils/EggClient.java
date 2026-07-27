package com.midea.cloud.srm.cm.contract.utils;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.model.contract.dto.ContractEggQueryDto;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @author 100014336 gnayh19
 */
@Component
public class EggClient {

    @Value("${srm.baseUrl}")
    private String baseUrl;


    public InputStream upload(String htmString){
        String url = baseUrl+"egg/upload";
        ContractEggQueryDto queryDto = ContractEggQueryDto.createA4PdfRequest(htmString);
        String json = JsonUtil.entityToJsonStr(queryDto);
        String auth = HttpServletHolder.getRequest().getHeader("Authorization");
        String contentType =HttpServletHolder.getRequest().getContentType();
        return HttpRequest.post(url).auth(auth).contentType(contentType).body(json).execute().bodyStream();
    }
}
