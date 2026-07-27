package com.midea.cloud.srm.model.pj.cooperate;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author huangbf3
 */
@Data
@TableName("scc_npm_pr_require_head")
@ApiModel(description = "招标计划头表")
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@QlMatchType("SccNpmPrRequireHead")
public class SccNpmPrRequireHead extends BaseEntity{

    /** REQUIREMENT_HEAD_ID */
    @TableId
    @ApiModelProperty("主键id")
    private Long requirementHeadId;
    /**   HAS_CREATE_sou */
    @ApiModelProperty("是否已创建标书")
    private String hasCreateSou;

    /**   projectName */
    @ApiModelProperty("项目名称")
    private String projectName;

    /**   ORG_BU_NAME */
    @ApiModelProperty("所属板块名称")
    private String orgBuName;

    /**   ORG_BU_CODE */
    @ApiModelProperty("所属板块编码")
    private String orgBuCode;









}
