package com.midea.cloud.srm.model.base.category.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/5/29
 */
@Data
@ApiModel("品类快查参数")
public class CategoryQuickSearchParam extends BaseDTO {

    /**
     * 品类编码
     */
    @ApiModelProperty("品类编码")
    private String categoryCode;

    /**
     * 品类名称
     */
    @ApiModelProperty("品类名称")
    private String categoryName;

    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用")
    private String enabled;

    /**
     * 级别
     */
    @ApiModelProperty("级别")
    private Integer level;

    /**
     * 是否末级
     */
    @ApiModelProperty("是否末级")
    private String lastLevelFlag;

    /**
     * 下拉选择过滤条件
     */
    @ApiModelProperty("下拉选择过滤条件")
    private String inputValue;
}
