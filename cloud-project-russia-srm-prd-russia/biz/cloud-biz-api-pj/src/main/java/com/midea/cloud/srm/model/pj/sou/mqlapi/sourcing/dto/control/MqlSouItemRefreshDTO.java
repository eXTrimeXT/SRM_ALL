package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.control;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.control.MqlSouItemRefreshAuthDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * MQL - 物料刷新信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@ApiModel(description = "物料刷新信息")
@EqualsAndHashCode(callSuper = true)
public class MqlSouItemRefreshDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("供应商对新物料的报价权限(有报价权限不代表可以报价，还需要看上一轮的入围情况等)")
    private List<MqlSouItemRefreshAuthDTO> authList;
    @ApiModelProperty("寻源类型")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
    }

}
