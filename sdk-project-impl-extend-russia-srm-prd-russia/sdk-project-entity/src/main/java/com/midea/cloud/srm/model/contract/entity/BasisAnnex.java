package com.midea.cloud.srm.model.contract.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@TableName("scc_contract_basis_annex")
@ApiModel(
        description = "合同附件"
)
@Data
public class BasisAnnex extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("类型-要素Id")
    @TableId("ANNEX_ID")
    private Long annexId;
    @ApiModelProperty("合同头信息ID")
    @TableField("CONTRACT_HEAD_ID")
    private Long contractHeadId;
    @ApiModelProperty("原始文件名")
    @TableField("FILE_SOURCE_NAME")
    private String fileSourceName;
    @ApiModelProperty("文件ID")
    @TableField("FILEUPLOAD_ID")
    private Long fileuploadId;
    @ApiModelProperty("变更字段(N/Y)")
    @TableField("CHANGE_FIELD")
    private String changeField;
    @ApiModelProperty("附件类型")
    @TableField("FILE_TYPE")
    private String fileType;
    @ApiModelProperty("原附件id")
    @TableField("SOURCE_ID")
    private Long sourceId;
    @ApiModelProperty("上传时间")
    @TableField("CEEA_UPLOAD_TIME")
    private Date ceeaUploadTime;
    @ApiModelProperty("是否允许删除，给前端做判断")
    @TableField("DEL")
    private String del;
    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;

}
