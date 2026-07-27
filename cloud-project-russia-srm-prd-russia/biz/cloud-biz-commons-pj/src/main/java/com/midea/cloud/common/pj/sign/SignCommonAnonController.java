package com.midea.cloud.common.pj.sign;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.pj.config.CommonSccConfig;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.sign.service.ISignCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author huangbf3
 */
@Lazy
@Slf4j
@RestController
@RequestMapping("/external/sign")
public class SignCommonAnonController extends BaseController {

    @Autowired
    private CommonSccConfig commonSccConfig;

    @PostMapping("/callbackSign")
    public String callbackSign(@RequestParam("serviceBean") String serviceBean, @RequestParam("callbackMethod") String callbackMethod,
                               @RequestParam("businessId") Long businessId, @RequestParam("param") String param, @RequestBody List<Fileupload> fileuploads) throws Exception {
        log.info("流程回调参数serviceBean:{},callbackMethod:{},param:{},businessId:{}",
                serviceBean, callbackMethod, JSONObject.toJSONString(param), businessId);
        if (CollectionUtils.isNotEmpty(commonSccConfig.getSignServiceBeans()) &&
                !commonSccConfig.getSignServiceBeans().contains(serviceBean)) {
            throw new BaseException("禁止访问");
        }

        ISignCallbackService iSignCallbackService = null;
        Class clazz = Class.forName(serviceBean);
        Object bean = SpringContextHolder.getApplicationContext().getBean(clazz);
        iSignCallbackService = (ISignCallbackService) bean;
        String completeText = "complete";
        if (completeText.equals(callbackMethod)) {
            iSignCallbackService.complete(businessId, param, fileuploads);
        } else {
            throw new BaseException("回调方法找不到");
        }

        return "业务回调成功";
    }
}
