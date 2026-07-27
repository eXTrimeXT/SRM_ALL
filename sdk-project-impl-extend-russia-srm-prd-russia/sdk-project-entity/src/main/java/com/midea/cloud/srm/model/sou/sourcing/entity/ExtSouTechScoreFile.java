package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "技术标评审附件")
@TableName(value = "scc_sou_tech_score_file")
public class ExtSouTechScoreFile extends BaseEntity<ExtSouTechScoreFile> {

    @ApiModelProperty("技术标评审附件表id")
    @TableId("TECH_SCORE_FILE_ID")
    private Long techScoreFileId;

    @ApiModelProperty("技术标评审ID")
    @TableField("TECH_SCORE_HEAD_ID")
    private Long techScoreHeadId;

    @ApiModelProperty("附件id")
    @TableField("FILE_ID")
    private Long fileId;

    @ApiModelProperty("附件名称")
    @TableField("FILE_NAME")
    private String fileName;

    @ApiModelProperty("附件路径")
    @TableField("FILE_URL")
    private String fileUrl;

    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;

}

