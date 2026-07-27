package com.midea.cloud.srm.model.supcooperate.report.mdv.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/4/15 10:00
 *  修改内容:
 * </pre>
 */
@Data
public class VendorQuantityDTO extends BaseDTO {

    @ApiModelProperty("年月")
    private String yearMonth;

    @ApiModelProperty("用途分类")
    private String extUseType;

    @ApiModelProperty("年")
    private String year;

    @ApiModelProperty("月")
    private String month;

    @ApiModelProperty("当年数量")
    private Integer thisYearQuantity;

    @ApiModelProperty("当月数量")
    private Integer thisMonthQuantity;

    @ApiModelProperty("上月数量")
    private Integer lastMonthQuantity;
}
