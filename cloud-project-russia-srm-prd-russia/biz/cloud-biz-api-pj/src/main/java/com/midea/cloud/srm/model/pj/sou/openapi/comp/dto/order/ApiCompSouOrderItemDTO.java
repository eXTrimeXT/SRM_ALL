package com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.order;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 竞价openAPI - 报价明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouOrderItemDTO extends ApiSouOrderItemDTO {

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("供应商填写的公式报价json")
    private String formulaResult;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人ID")
    private Long submitById;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人账号")
    private String submitBy;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人IP")
    private String submitByIp;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人昵称")
    private String submitFullName;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交时间")
    private Date submitTime;

}
