package com.midea.cloud.srm.model.pj.sou.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 评分模板
 * @author: hesl41
 * @Date: 2022/10/17 10:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_score_template")
@ApiModel("评分模板")
public class SouScoreTemplate  extends BaseEntity<SouScoreTemplate> {

    @ApiModelProperty("ID")
    @TableId("SCORE_TEMPLATE_ID")
    private Long scoreTemplateId;
    /** 前端展示单号 */
    @ApiModelProperty("模板编码")
    @TableField("SCORE_TEMPLATE_NO")
    private String scoreTemplateNo;

    @ApiModelProperty("模板名称")
    @TableField("SCORE_TEMPLATE_NAME")
    private String scoreTemplateName;

    @ApiModelProperty("寻源类型")
    @TableField("SOU_TYPE")
    private String souType;
    /**
     * 字典 sou_score_template_status
     */
    @ApiModelProperty("状态：拟定DRAFT,生效VALID,失效INVALID")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;


}
