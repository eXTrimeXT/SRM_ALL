package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertWork;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertWorkRelation;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 专家库 - 初始化导入信息 - 亲属工作履历
 *
 * @author zhangwk12@meiclolud.com
 * @since 2023-11-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertInitWorkRelationImportExcelDTO extends BaseObjectX {

    /** @see ExtSouExpertApply#getApplyByNickname */
    @ApiModelProperty("姓名")
    private String applyByNickname;

    /** @see ExtSouExpertApply#getApplyByCode */
    @ApiModelProperty("工号")
    private String applyByCode;

    /** @see ExtSouExpertWorkRelation#getRelativeType */
    @ApiModelProperty("与本人亲属关系")
    private String relativeType;

    /** @see ExtSouExpertWorkRelation#getWorkUnit */
    @ApiModelProperty("工作单位名称")
    private String workUnit;

}
