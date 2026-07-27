package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

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
@ApiModel("招标文件查询条件")
public class ApiExtSouOrderFileQueryDto extends BaseObjectX {

    @ApiModelProperty("寻源核心-询价单ID")
    private Long projectId;

    @ApiModelProperty("附件类型")
    private String fileType;

    @ApiModelProperty("附件类型")
    private List<String> fileTypeList;

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("供应商ID")
    private List<Long> vendorIdList;

    @ApiModelProperty("报价头ID")
    private Long orderId;


}
