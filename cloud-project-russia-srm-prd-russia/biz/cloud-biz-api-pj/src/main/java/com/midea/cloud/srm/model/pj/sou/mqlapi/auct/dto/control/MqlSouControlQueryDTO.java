package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.control;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouControlQueryDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("true-所有供应商/false-仅已提交报价的供应商")
    private Boolean showAllVendors;

    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
    }

}
