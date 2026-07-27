package com.midea.cloud.srm.supcooperate.ext.requirementcancles.factory;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.RequirementCancleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Component
@Slf4j
public class RequirementCancleFactory {

    private static RequirementCancleFactory requirementCancleFactory;

    @PostConstruct
    private void inite() {
        requirementCancleFactory = this;
    }

    /**
     * 获取实例
     * @return
     */
    public static RequirementCancleFactory instance() {
        return requirementCancleFactory;
    }

    @Autowired
    private Map<String, RequirementCancleService> serviceMap;

    /**
     * 单例模式
     */
    private RequirementCancleFactory() {
    }

    public RequirementCancleService createCancleBusiness(String type) {
        log.info(MessageFormat.format("获取采购需求取消实现类{0}", type));
        if(!requirementCancleFactory.serviceMap.containsKey(type)) {
            throw new BaseException(MessageFormat.format("采购需求取消实现类{0}为空", type));
        }
        return requirementCancleFactory.serviceMap.get(type);
    }
}
