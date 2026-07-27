package com.midea.cloud.common.pj.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Set;

/**
 * @author huangbf3
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "cloud.scc")
public class CommonSccConfig {
    /** SignCommonAnonController 流程回调参数serviceBean枚举名校验 */
    private Set<String> signServiceBeans;
    /** BpmController bpm嵌套单点跳转地址前缀校验 */
    private Set<String> bpmRedirectWhiteUris;

}
