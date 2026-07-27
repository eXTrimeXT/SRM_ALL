package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo;

import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招标计划 - 招标取消详情
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementCancelLineVO extends ExtPrSouRequirementCancelLine {

    @ApiModelProperty("招标计划信息")
    private ExtPrSouRequirementHeadVO reqHead;

    @ApiModelProperty("招标负责人")
    private String souGroupFullName;

    @ApiModelProperty("招标负责人ID")
    private Long souGroupUserId;

    @ApiModelProperty("招标负责人账号")
    private String souGroupUsername;

    @ApiModelProperty("技术负责人")
    private String techGroupFullName;

    @ApiModelProperty("技术负责人ID")
    private Long techGroupUserId;

    @ApiModelProperty("技术负责人账号")
    private String techGroupUsername;

    @ApiModelProperty("供应商负责人")
    private String vendorGroupFullName;

    @ApiModelProperty("供应商负责人ID")
    private Long vendorGroupUserId;

    @ApiModelProperty("供应商负责人账号")
    private String vendorGroupUsername;

}
