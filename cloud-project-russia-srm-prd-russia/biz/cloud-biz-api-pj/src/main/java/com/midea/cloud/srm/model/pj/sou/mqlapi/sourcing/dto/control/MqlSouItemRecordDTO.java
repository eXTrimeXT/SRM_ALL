package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.control;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init.MqlSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

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
public class MqlSouItemRecordDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("变更后的物料集合")
    private List<MqlSouItemDTO> itemList;

    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(itemList)) {
            throw new IllegalArgumentException("缺少变更物料信息");
        }
    }

}
