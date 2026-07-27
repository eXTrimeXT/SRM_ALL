package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPriceTemplate;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("投标历史")
public class ApiExtSouOrderDetailDto extends BaseObjectX {

    @ApiModelProperty("寻源核心-单据ID")
    private Long projectId;

    @ApiModelProperty("寻源核心-报价单ID")
    private Long orderId;

    @ApiModelProperty("技术标文件")
    private List<ExtSouOrderFile> techOrderFileList;

    @ApiModelProperty("商务报价单")
    private List<ExtSouOrderFile> busOrderFileList;

    @ApiModelProperty("报价模板")
    private List<ExtSouPriceTemplate> priceTemplateList;

    @ApiModelProperty("报价信息")
    private List<ApiExtSouOrderItemDto> orderItemList;

    @ApiModelProperty("项目信息")
    private ExtSouProjectDto project;

    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

}
