package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "项目信息修改")
@EqualsAndHashCode(callSuper = true)
public class ApiExtSouProjectModifyDto extends BaseObjectX {

    @ApiModelProperty("主键ID")
    private Long projectId;

    /**
     * @see com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum
     */
    @ApiModelProperty("项目状态")
    private String projectStatus;

    /**
     * @see com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum
     */
    @ApiModelProperty("立项审核状态")
    private String createApprovalStatus;

}
