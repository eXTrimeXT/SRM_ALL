package com.midea.cloud.srm.model.sou.approve.dto;

import com.midea.cloud.srm.model.sou.approve.entity.SouApproveOperate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("审批操作类")
public class SouApproveOperateDto extends SouApproveOperate {

    @ApiModelProperty("业务单据关联ID")
    private Long businessId;
}
