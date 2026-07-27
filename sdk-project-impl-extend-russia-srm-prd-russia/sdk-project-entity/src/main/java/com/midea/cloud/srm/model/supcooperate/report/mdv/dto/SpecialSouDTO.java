package com.midea.cloud.srm.model.supcooperate.report.mdv.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

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
public class SpecialSouDTO extends BaseDTO {

    @ApiModelProperty("板块")
    private String extOrgBuName;

    @ApiModelProperty("公司")
    private String extOrgOuName;

    @ApiModelProperty("申请审批结束年份")
    private Date passTime;

    @ApiModelProperty("发生年份")
    private Integer year;

    @ApiModelProperty("数量")
    private Integer quantity;
}
