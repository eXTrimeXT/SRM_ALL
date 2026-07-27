package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
 * 项目式询价.供应商报价附件
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_select_file")
@ApiModel("供应商报价附件")
public class SouSelectFile extends BaseEntity<SouSelectFile> {

    @TableId("SELECT_FILE_ID")
    @ApiModelProperty("ID")
    private Long selectFileId;

    @TableField("PROJECT_ID")
    @ApiModelProperty("竞价单ID")
    private Long projectId;

    @TableField("FILE_TYPE")
    @ApiModelProperty("附件类型")
    private SouFileTypeEnum fileType;

    @TableField(value = "FILE_DOC_ID", updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("文件ID")
    private Long selectDocId;

    @TableField(value = "SELECT_FILE_NAME", updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty("文件名")
    private String selectFileName;

    @TableField("SELECT_REMARK")
    @ApiModelProperty("备注")
    private String selectRemark;

    @TableField("APPROVAL_PROCESS")
    @ApiModelProperty("审批流")
    private String approvalProcess;

}
