package com.midea.cloud.srm.model.pj.sou.score.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("评分模板")
public class SouScoreTemplateQueryDTO extends BasePage {

    @ApiModelProperty("模板编码")
    @TableField("score_template_no")
    private String scoreTemplateNo;

    @ApiModelProperty("模板名称")
    @TableField("score_template_name")
    private String scoreTemplateName;

    @ApiModelProperty("寻源类型")
    @TableField("sou_type")
    private String souType;


    /**
     * 当前用户ID
     */
    private Long currentUserId;

    /**
     * 当前公司ID
     */
    private Long currentCompanyId;





}
