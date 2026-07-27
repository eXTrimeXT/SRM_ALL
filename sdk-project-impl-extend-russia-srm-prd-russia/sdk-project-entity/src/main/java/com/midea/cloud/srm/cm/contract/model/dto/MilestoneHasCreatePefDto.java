package com.midea.cloud.srm.cm.contract.model.dto;

import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 100014336 ganyh10
 */
@Data
public class MilestoneHasCreatePefDto {

    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("里程碑类型")
    private String milestoneType;

    @ApiModelProperty("是否已创建项目评分")
    private Enable enable;



}
