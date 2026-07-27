package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 招标计划 - 计划取消附件
 * @author huangbf3
 */
@Data
@TableName("scc_npm_pr_require_cancel_file")
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementCancelAttach extends BaseEntity<ExtPrSouRequirementCancelAttach> {

    @TableId("REQUIREMENT_CANCEL_ATTACH_ID")
    @ApiModelProperty("ID")
    private Long requirementCancelAttachId;

    /** @see ExtPrSouRequirementCancel#getRequirementCancelId */
    @TableField("REQUIREMENT_CANCEL_ID")
    @ApiModelProperty("计划取消ID")
    private Long requirementCancelId;

    @TableField("FILE_ID")
    @ApiModelProperty("文件ID")
    private Long fileId;

    @TableField("FILE_NAME")
    @ApiModelProperty("文件名称")
    private String fileName;

    @TableField("UPLOAD_TIME")
    @ApiModelProperty("上传时间")
    private Date uploadTime;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
