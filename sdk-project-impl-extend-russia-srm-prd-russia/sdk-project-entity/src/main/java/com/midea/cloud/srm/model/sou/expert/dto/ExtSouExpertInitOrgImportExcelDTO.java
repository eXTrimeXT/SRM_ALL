package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertOrgRelation;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专家库 - 初始化导入信息 - 适用评分组织
 *
 * @author zhangwk12@meiclolud.com
 * @since 2023-11-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertInitOrgImportExcelDTO extends BaseObjectX {

    /** @see ExtSouExpertApply#getApplyByNickname */
    @ApiModelProperty("姓名")
    private String applyByNickname;

    /** @see ExtSouExpertApply#getApplyByCode */
    @ApiModelProperty("工号")
    private String applyByCode;

    /** @see ExtSouExpertOrgRelation#getOrgName */
    @ApiModelProperty("适用组织")
    private String orgName;

}
