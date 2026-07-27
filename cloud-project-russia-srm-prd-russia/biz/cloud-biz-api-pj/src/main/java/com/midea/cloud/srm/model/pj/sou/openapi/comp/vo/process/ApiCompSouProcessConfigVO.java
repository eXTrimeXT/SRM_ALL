package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价openAPI - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouProcessConfigVO extends SouProcessConfig {

    /** @see CompSouProcessConfig#getCompHall */
    @ApiModelProperty("竞价大厅")
    private Enable compHall;

}
