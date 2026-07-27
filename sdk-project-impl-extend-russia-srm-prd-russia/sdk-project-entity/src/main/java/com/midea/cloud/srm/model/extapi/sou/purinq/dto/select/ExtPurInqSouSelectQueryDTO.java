package com.midea.cloud.srm.model.extapi.sou.purinq.dto.select;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 100014337
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouSelectQueryDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID(必填)")
    private Long projectId;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
    }

}