package com.midea.cloud.srm.biz.pj.changchengapi;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author huangbf3
 *  以 JAVA代码为例
 */
@Slf4j
public class OpenClientTest {

    @Autowired
    RestTemplate restTemplate;

    /**
     * 沙盒环境
     */
    public static String APP_KEY = "X63T8A0BM648";
    public static String SECRET = "00cc14bc145a43dc816fa5750a0a7324";


    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        orglist();
    }

    /**
     * 组织列表
     */
    public static void orglist(){
        OpenClient openClient = new OpenClient("X63T8A0BM648","00cc14bc145a43dc816fa5750a0a7324");

        JSONObject bodyJsn = new JSONObject();
        bodyJsn.put("size",10);
        bodyJsn.put("page",1);

        String url = "https://gwapi.gwm.cn/sandbox/hr/staff/domain/org/list?page=1&size=10";
        String result = openClient.sendHttpGet(url);

        log.info(result);
    }
}