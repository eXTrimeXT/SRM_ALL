package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessConfigEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/21
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouProcessConfigDTO extends ApiSouProcessConfigEditDTO {

    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

}
