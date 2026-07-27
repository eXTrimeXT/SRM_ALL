package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo;

import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelAttach;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 招标计划 - 计划取消
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementCancelVO extends ExtPrSouRequirementCancel {

    @ApiModelProperty("取消明细")
    private List<ExtPrSouRequirementCancelLineVO> cancelLineList;

    @ApiModelProperty("取消附件")
    private List<ExtPrSouRequirementCancelAttach> cancelAttachList;

}