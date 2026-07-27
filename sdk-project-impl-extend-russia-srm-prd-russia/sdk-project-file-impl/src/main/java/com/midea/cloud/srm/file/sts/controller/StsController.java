package com.midea.cloud.srm.file.sts.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.meicloud.paas.osca.configure.OscaConfigProperties;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.file.sts.dto.StsAccessCredentialsDto;
import com.midea.cloud.srm.model.common.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;


/**
 * OSS sts访问
 *
 * @author FuBiao
 * @since 2024/06/26
 */
@RestController
@RequestMapping("/sts/api")
@Api(tags = "oss sts接口")
@Slf4j
public class StsController extends BaseController {
    @Resource
    private OscaConfigProperties oscaConfigProperties;

    @Value("${gwm.sts.endpoint}")
    private String endpoint;

    @Value("${gwm.sts.ai-helper.roleArn}")
    private String roleArn;


    /**
     * oss sts 访问
     */
    @GetMapping("/aiSts")
    public StsAccessCredentialsDto aiSts(@RequestParam("key") String key) throws Exception {
        return getStsAccessCredentialsDto(key);
    }

    private StsAccessCredentialsDto getStsAccessCredentialsDto(String key) throws ClientException {
        DefaultProfile.addEndpoint("", "", "Sts", endpoint);
        IClientProfile profile = DefaultProfile.getProfile("", oscaConfigProperties.getAccessKeyId(), oscaConfigProperties.getAccessKeySecret());
        HttpClientConfig clientConfig = HttpClientConfig.getDefault();
        clientConfig.setIgnoreSSLCerts(true);
        profile.setHttpClientConfig(clientConfig);
        DefaultAcsClient client = new DefaultAcsClient(profile);

        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setMethod(MethodType.POST);
        request.setRoleArn(roleArn);
        request.setRoleSessionName(key);
        // 若policy为空，则用户将获得该角色下所有权限
        String policy = config(key);
        request.setPolicy(policy);
        // 设置凭证有效时间(12H)
//        request.setDurationSeconds(60*60L);

        AssumeRoleResponse response = client.getAcsResponse(request);

        StsAccessCredentialsDto stsAccessCredentialsDto = new StsAccessCredentialsDto();
        stsAccessCredentialsDto.setAccessKeyId(response.getCredentials().getAccessKeyId());
        stsAccessCredentialsDto.setAccessKeySecret(response.getCredentials().getAccessKeySecret());
        stsAccessCredentialsDto.setSecurityToken(response.getCredentials().getSecurityToken());
        stsAccessCredentialsDto.setExpiration(response.getCredentials().getExpiration());
        return stsAccessCredentialsDto;
    }

    private String config(String key) {
        String result;
        try {
            // license.xml应放在资源路径下
            InputStream is = this.getClass().getResourceAsStream("/oss-sts/sts_config.json");
            JSONObject jsonObject = JSONObject.parseObject(IOUtils.toString(is, "utf-8"));
            result = jsonObject.getString(key);
        } catch (Exception e) {
            log.error("StsController config error",e);
            throw new BaseException("设置权限异常");
        }
        AssertUtils.notNull(result,"非法key");
        return result;
    }

    public static void main(String[] args) {
        String endpoint ="http://oss0c83-cn-baoding-gwmcloud-d01-a.ops.cloud.gwm.cn/";
        String accessKeyId ="STS.3xDDgoybuXJiQvP4S533NNvLis";
        String accessKeySecret ="FqvnutRT6yTdmXS1yqmDUNW2dXCExmTdrtVgViYnMpoj";
        String securityToken ="CAISsAN1q6Ft5B6yfSjI0rvxD93blL1U74iCU1Dh0FNgP7xiobPni23GAXtIfXRgCeoWtP0xlWtR7fsZjoMIUINeckXFWst96oxa6zSlZIHIv5RCMCH3Qsf3d1KIAjvXgeVPCoeQFaFaE5XAQlTAkTAJn9meXD6+XlujHISUgJp8FLo+VRW5ajw0crUzIRB5+uAXKVzbN/umLnyPhXHLXnJ1pi12i2509d56q6/60BfFi0DgweI4vpn4JoPeD/NhJ5BiSdy4rhQfFOzI2zUC7ANRpuUkzv5f4zKCpsuRRFRB/xicNePQtdRuGw90YrA3AZlAq/P1kbo62KranJ+lzA1Wb6MHEXbEXoXl3cLYX+65Ktsjc7//fH3M5dCCMoH4qTQgZnkSMEQIGZwoIWQiDgc3GHOIaP284lWPbgq9DKWVzKws15NxiFzpytqHI0CCWYKd1i0RPth+TTtxaEJNgjCxK/ZXKlwdLQw6PdvPE9UvNiI5gLjzoAjfWhdnynxqpPDkb5vUwPtDMNivDsgWi9NDNc8f6DZxFU6OUK61z0APbyk4GOsMi+z2I5qvK2YOUVJGhYUagAEmaZpQv9/d3DpHblkiWF8/TpDbBjxo2Pl514TQq67Y9Ma+lSB7o2oWvo0ZCNKu8wYFgfMLtK4DpyeHoMahLVkds+mRAE0UJmH2tHLqm2sSxTbj1OUfdiGMkB5J7tx54yRpUhkvGBXD1/ExXhuVukoXF8t8XcAB/ODxjkPMB2mKXQ==";
        String bucket ="srm-dev";
        String targetDirTrue ="external/ai_helper_local/";
        String targetDirFalse ="external/ai_helper_temp/";
        String targetDirFalse2 ="external/";
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, securityToken);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, targetDirTrue+"001.png", new File("D:\\1.png"));
        PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
        System.out.println("args = " + putObjectResult.getRequestId());

//        PutObjectRequest putObjectRequest1 = new PutObjectRequest(bucket, targetDirFalse+"001.png", new File("D:\\1.png"));
//        PutObjectResult putObjectResult1 = ossClient.putObject(putObjectRequest1);
//        System.out.println("args = " + putObjectResult1.getRequestId());

//        PutObjectRequest putObjectRequest2 = new PutObjectRequest(bucket, targetDirFalse2+"001.png", new File("D:\\1.png"));
//        PutObjectResult putObjectResult2=  ossClient.putObject(putObjectRequest2);
//        System.out.println("args = " + putObjectResult2.getRequestId());

//        PutObjectRequest putObjectRequest3 = new PutObjectRequest(bucket, "00111.png", new File("D:\\1.png"));
//        PutObjectResult putObjectResult3 = ossClient.putObject(putObjectRequest3);
//        System.out.println("args = " + putObjectResult3.getRequestId());


    }

}