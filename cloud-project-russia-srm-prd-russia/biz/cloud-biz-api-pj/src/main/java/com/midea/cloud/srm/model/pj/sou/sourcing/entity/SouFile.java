package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 寻源-核心-附件表
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@ApiModel(description = "<pre>  寻源-附件表 </pre> <pre>  修改记录  修改后版本:  修改人: zhangwk12@meicloud.com  修改日期: 2021-10-16  修改内容: </pre>")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_file")
public class SouFile extends BaseEntity<SouFile> {

    @ApiModelProperty("寻源核心-附件表ID")
    @TableId("SOU_FILE_ID")
    private Long souFileId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("文件ID")
    @TableField("SOU_DOC_ID")
    private Long souDocId;

    @ApiModelProperty("文件名")
    @TableField("SOU_FILE_NAME")
    private String souFileName;

    @ApiModelProperty("附件类型[字典:SOU_FILE_TYPE]")
    @TableField("FILE_TYPE")
    private SouFileTypeEnum fileType;

    @ApiModelProperty("说明")
    @TableField("SOU_REMARK")
    private String souRemark;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

    /** @see SouVendor#getSouVendorId */
    @ApiModelProperty("供应商表ID")
    @TableField("SOU_VENDOR_ID")
    private Long souVendorId;

}
