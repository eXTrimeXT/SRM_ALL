package com.midea.cloud.srm.model.sou.bpmtodo.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/5/31
 */
@Data
@ApiModel("招标流程手机待办响应")
public class SouBpmtodoResponse extends BaseDTO {

    /**
     * 项目信息
     */
    @ApiModelProperty("项目信息")
    private ExtSouProject extSouProject;

    /**
     * 报价信息
     */
    @ApiModelProperty("报价信息")
    private List<ExtSouOrderDto> extSouOrderDtoList;

    @ApiModelProperty("是否显示开标按钮")
    private String showOpenButton;
}
