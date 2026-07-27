package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto;

import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelLine;
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
public class ExtPrSouRequirementCancelDTO extends ExtPrSouRequirementCancel {

    @ApiModelProperty("取消明细")
    private List<ExtPrSouRequirementCancelLine> cancelLineList;

    @ApiModelProperty("取消附件")
    private List<ExtPrSouRequirementCancelAttach> cancelAttachList;

    @ApiModelProperty("单据号生成规则")
    private String generateCode;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-取消")
    private Boolean tempSave = true;

}
