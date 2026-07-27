package com.midea.cloud.srm.model.sou.fixprice.vo;

import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceSourceFromTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtFixPriceReqLinesQueryVO extends RequirementLine {

    @ApiModelProperty("来源类型")
    private ExtFixPriceSourceFromTypeEnum sourceFromType;

    /** @see RequirementHead#getCeeaPrType */
    @ApiModelProperty("申请类型")
    private String ceeaPrType;

    /** @see RequirementHead#getApplyBy */
    @ApiModelProperty("申请人账号")
    private String applyBy;

    @ApiModelProperty("申请人昵称")
    private String applyByName;

    @ApiModelProperty("报价次数")
    private Integer extOrderCount;

}
