package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/05/08/ $
 * @Description: 采购需求提报新增明细导出-请求参数
 */
@Data
@ApiModel("采购需求提报新增明细导出-请求参数")
public class ExtPrSouRequirementLineExportRequestDto extends BaseDTO {

    @ApiModelProperty("申请ID")
    private List<Long> requirementHeadIdList;
}
