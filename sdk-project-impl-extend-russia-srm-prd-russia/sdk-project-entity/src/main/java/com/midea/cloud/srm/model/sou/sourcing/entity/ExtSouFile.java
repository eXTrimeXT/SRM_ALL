package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouFileTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(
        description = "<pre>  寻源-附件表 </pre> <pre>  修改记录  修改后版本:  修改人: zhangwk12@meicloud.com  修改日期: 2021-10-16  修改内容: </pre>"
)
@TableName("scc_sou_file")
@Data
public class ExtSouFile extends BaseEntity {

    @ApiModelProperty("寻源核心-附件表ID")
    @TableId("sou_file_id")
    private Long souFileId;
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("project_id")
    private Long projectId;
    @ApiModelProperty("文件ID")
    @TableField("sou_doc_id")
    private Long souDocId;
    @ApiModelProperty("文件名")
    @TableField("sou_file_name")
    private String souFileName;
    @ApiModelProperty("附件类型[字典:SOU_FILE_TYPE]")
    @TableField("file_type")
    private String fileType;
    @ApiModelProperty("说明")
    @TableField("sou_remark")
    private String souRemark;
    @ApiModelProperty("排序")
    @TableField("sort_index")
    private Integer sortIndex;

    /**
     * 推荐供应商单号
     */
    private String extRecommendNo;

    /**
     * 包名
     */
    @ApiModelProperty("包名")
    private String extPackageName;

}
