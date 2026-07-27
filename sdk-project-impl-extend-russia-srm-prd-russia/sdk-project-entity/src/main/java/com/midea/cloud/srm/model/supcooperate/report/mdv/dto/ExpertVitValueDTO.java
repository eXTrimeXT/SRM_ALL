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
public class ExpertVitValueDTO extends BaseDTO {

    @ApiModelProperty("发生年份")
    private Integer year;

    @ApiModelProperty("板块名称")
    private String buName;

    @ApiModelProperty("公司名称")
    private String orgOuName;

    @ApiModelProperty("数量")
    private Integer quantity;
}
