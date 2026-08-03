package com.midea.cloud.srm.biz.pj.sou.metadata.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <pre>
 * 扩展实体列配置信息
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/12 17:42
 *  修改内容:
 * </pre>
 */
@Setter
@Getter
public class MetadataColVO implements Serializable {
    private String dataType;

    private String enableLength;

    private String enablePrecision;
}
