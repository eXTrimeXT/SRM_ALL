package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessConfigStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 寻源openAPI - 流程配置查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/20
 */
@Data
@ApiModel(description = "流程配置查询条件")
@EqualsAndHashCode(callSuper = true)
public class ApiSouProcessConfigQueryDTO extends BasePage {

    /** @see SouProcessConfig#getProcessConfigName */
    @ApiModelProperty("流程配置名称(模糊查询)")
    private String processConfigName;

    /** @see SouProcessConfig#getSouType */
    @ApiModelProperty("寻源类型")
    private String souType;

    /** @see SouProcessConfig#getProcessStatus */
    @ApiModelProperty("状态(等值查询)[字典:SOU_PROCESS_CONFIG_STATUS]")
    private SouProcessConfigStatusEnum status;

    /** @see SouProcessConfig#getPublishScope */
    @ApiModelProperty("发布范围(等值查询)[字典:SOU_PUBLISH_SCOPE]")
    private SouPublishScopeEnum publishScope;

    /** @see SouProcessConfig#getScoreRuleType */
    @ApiModelProperty("评选方式(等值查询)[字典:SOU_SCORE_RULE_TYPE]")
    private SouScoreRuleTypeEnum scoreRuleType;

    /**
     * 入参格式化
     */
    public void formatParams() {
        processConfigName = StringUtils.trimToNull(processConfigName);
        souType = StringUtils.trimToNull(souType);
    }

}
