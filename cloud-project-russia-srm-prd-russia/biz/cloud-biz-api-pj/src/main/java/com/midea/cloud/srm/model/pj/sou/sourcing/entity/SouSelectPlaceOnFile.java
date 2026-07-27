package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author ex_yipeng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_place_on_file")
@ApiModel("归档附件")
public class SouSelectPlaceOnFile extends BaseEntity<SouSelectPlaceOnFile> {

    @TableId("FILE_ID")
    @ApiModelProperty("ID")
    private Long fileId;

    @TableField("PROJECT_ID")
    @ApiModelProperty("竞价单ID")
    private Long projectId;

    @TableField("FILE_TYPE")
    @ApiModelProperty("附件类型")
    private SouFileTypeEnum fileType;

    @TableField("DOC_ID")
    @ApiModelProperty("文件ID")
    private Long docId;

    @TableField("FILE_NAME")
    @ApiModelProperty("文件名")
    private String fileName;

    @TableField("FILE_REMARK")
    @ApiModelProperty("备注")
    private String fileRemark;
}
