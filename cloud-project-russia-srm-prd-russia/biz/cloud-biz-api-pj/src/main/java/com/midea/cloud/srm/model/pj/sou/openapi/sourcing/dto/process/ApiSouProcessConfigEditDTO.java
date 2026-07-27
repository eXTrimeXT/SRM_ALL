package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/28
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "流程配置信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouProcessConfigEditDTO extends SouProcessConfig {

    @ApiModelProperty("true-暂存/false-提交")
    private boolean isTempSave;

}
