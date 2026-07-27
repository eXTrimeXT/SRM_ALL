package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author ex_yipeng
 */
@Data
@ApiModel(description = "中标通知修改项目状态")
@EqualsAndHashCode(callSuper = true)
public class ApiSouChangProjectDTO extends BaseObjectX {

    @ApiModelProperty("竞价单ID")
    protected Long projectId;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
    }
}
