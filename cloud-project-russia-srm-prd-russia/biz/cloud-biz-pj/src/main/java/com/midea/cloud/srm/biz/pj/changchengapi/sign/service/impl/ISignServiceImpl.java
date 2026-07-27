package com.midea.cloud.srm.biz.pj.changchengapi.sign.service.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISccPjSignFeignService;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISccPjSignOrderService;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISccPjSignTemplateService;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISignService;
import com.midea.cloud.srm.biz.pj.common.PjInterfaceLogUtils;
import com.midea.cloud.srm.biz.pj.contractlock.ContractLockService;
import com.midea.cloud.srm.feign.pj.sign.SignCallbackClient;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.sign.dto.SignCallback;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignFeign;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrder;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignTemplate;
import com.midea.cloud.srm.model.pj.sign.enums.SignCallbackStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description 契约锁
 * @author huangbf3
 * @date 2023-09-25
 */
@Slf4j
@Service
public class ISignServiceImpl implements ISignService {
    @Autowired
    private ISccPjSignOrderService signOrderService;
    @Autowired
    private ISccPjSignTemplateService signTemplateService;
    @Autowired
    private ISccPjSignFeignService signFeignService;

    @Autowired
    private ContractLockService contractLockService;

    @Override
    public void statusCallback(SignCallback signCallback) {

        String msg = ProcessStatusEnum.COMPLETED.getName();
        try {
            Assert.notBlank(signCallback.getContractId(),"文件编号不能为空");
            Assert.notBlank(signCallback.getStatus(),"状态不能为空");

            SccPjSignOrder signOrder = signOrderService.lambdaQuery().eq(SccPjSignOrder::getSignId,signCallback.getContractId()).one();
            Assert.notNull(signOrder,"业务ID不存在");
            Assert.notBlank(signOrder.getOrderType(),"未配置流程");

            SccPjSignTemplate signTemplate = signTemplateService.lambdaQuery().eq(SccPjSignTemplate::getOrderType,signOrder.getOrderType()).one();
            Assert.notNull(signTemplate,"未配置流程");

            SignCallbackClient callbackClient = getFeignClient(signTemplate);
            signCallback.setBussinessClass(signTemplate.getBussinessClass());
            signCallback.setSrmOrderId(signOrder.getSrmOrderId());

            signOrder.setSignStatus(signCallback.getStatus());
            signOrderService.updateById(signOrder);
            // 在这里调附件下载接口，并将附件送给业务
            List<Fileupload> fileuploads = contractLockService.downloadNew(signOrder.getSignId());

            try {
                if(StringUtils.equals(signCallback.getStatus(),SignCallbackStatus.COMPLETE.getStatus())){
                    callbackClient.callbackSign(signTemplate.getBussinessClass(),SignCallbackStatus.getMethodByStatus(signCallback.getStatus())
                            ,signOrder.getSrmOrderId(),JSONObject.toJSONString(signCallback),fileuploads);
                }

            } catch (Exception e) {
                log.info("契约锁状态回调业务单据失败:"+e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            log.error("statusCallback Exception", e);
            msg = "接口异常" + e.getMessage();
            throw new BaseException(e.getMessage());
        } finally {
            PjInterfaceLogUtils.sendInterfaceLog(ApiInfoEnum.SIGN_CALLBACK, JSON.toJSONString(signCallback), msg);
        }

    }

    private SignCallbackClient getFeignClient(SccPjSignTemplate signTemplate) {
        SccPjSignFeign signFeign = signFeignService.lambdaQuery().eq(SccPjSignFeign::getFeignUrlPath,signTemplate.getFeignUrlPath()).one();
        Assert.notNull(signFeign,"缺失Feign路径配置");
        Class clazz= null;
        try {
            clazz = Class.forName(signFeign.getFeignClassPath());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Object bean = SpringContextHolder.getApplicationContext().getBean(clazz);
        return (SignCallbackClient)bean;
    }


}

