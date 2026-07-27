package com.midea.cloud.srm.biz.pj.sou.metadata.config;

import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataColVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * <pre>
 * 扩展字段配置属性
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/12 16:15
 *  修改内容:
 * </pre>
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "cloud.scc.metadata")
public class MetadataProperties {
    /**
     * 最大列数限制
     */
    private Long maximumCol;

    /**
     * 可配置的数据类型
     */
    private List<MetadataColVO> dataTypes;

    /**
     * 开启可扩展
     */
    private Boolean enableExtend = true;

}
