package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo;

import com.midea.cloud.srm.model.pm.mql.pr.requirement.vo.init.MqlPrRequirementHeadVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 招标计划
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementHeadVO extends MqlPrRequirementHeadVO {

    @ApiModelProperty("招标计划拓展")
    private ExtPrSouRequirementHead souReqHead;

    @ApiModelProperty("招标负责人列表")
    private List<ExtPrSouRequirementGroup> souGroupList;

    @ApiModelProperty("推荐供应商列表")
    private List<ExtPrSouRequirementVendor> souVendorList;

    @ApiModelProperty("招标计划附件列表")
    private List<ExtPrSouRequirementAttach> souAttachList;

}
