package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.sou.expert.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 专家库 - 专家申请信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertApplyDTO extends ExtSouExpertApply {

    @ApiModelProperty("适用组织")
    private List<ExtSouExpertOrgRelation> orgList;

    @ApiModelProperty("学历")
    private List<ExtSouExpertEducation> educationList;

    @ApiModelProperty("适用品类")
    private List<ExtSouExpertCategoryRelation> categoryList;

    @ApiModelProperty("工作经历")
    private List<ExtSouExpertWork> workList;

    @ApiModelProperty("亲属工作经历")
    private List<ExtSouExpertWorkRelation> workRelationList;

    @ApiModelProperty("申请附件")
    private List<SceneFile> attachList;

    @ApiModelProperty("单据号生成规则")
    private String generateCode;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    private Boolean tempSave = true;

}
