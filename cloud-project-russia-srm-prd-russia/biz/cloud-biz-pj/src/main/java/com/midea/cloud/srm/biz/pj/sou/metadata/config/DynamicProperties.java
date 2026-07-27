package com.midea.cloud.srm.biz.pj.sou.metadata.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <pre>
 * 动态数据源配置
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/18 11:10
 *  修改内容:
 * </pre>
 */
@ConditionalOnProperty(value = "spring.application.name", havingValue = "cloud-biz-base")
@Component
@ConfigurationProperties("cloud.scc.dynamic")
public class DynamicProperties {
    private Map<String, JSONObject> datasouces = new LinkedHashMap();

    public Map<String, JSONObject> getDatasouces() {
        return this.datasouces;
    }

    public void setDatasouces(Map<String, JSONObject> datasouces) {
        this.datasouces = datasouces;
    }
}
