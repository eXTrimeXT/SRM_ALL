package com.midea.cloud.srm.model.sou.fixprice.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtFixPriceReqLinesQueryDTO extends BasePage {

    /** @see RequirementLine#getRequirementHeadNum */
    @ApiModelProperty("需求单号")
    private String requirementHeadNum;

    /** @see RequirementLine#getMaterialCode */
    @ApiModelProperty("物料编码")
    private String materialCode;

    /** @see RequirementHead#getApplyBy */
    @ApiModelProperty("采购人")
    private String applyBy;

    /** @see RequirementLine#getOrgId */
    @ApiModelProperty("申请部门ID")
    private Long orgId;

    public void formatParams() {
        requirementHeadNum = StringUtils.trimToNull(requirementHeadNum);
        materialCode = StringUtils.trimToNull(materialCode);
        applyBy = StringUtils.trimToNull(applyBy);
    }

}
