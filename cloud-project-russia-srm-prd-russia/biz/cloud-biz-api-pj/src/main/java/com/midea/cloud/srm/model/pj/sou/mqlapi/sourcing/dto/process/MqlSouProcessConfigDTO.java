package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.process;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程配置MQL
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouProcessConfigDTO extends SouProcessConfig {

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    private Boolean tempSave = false;

}
