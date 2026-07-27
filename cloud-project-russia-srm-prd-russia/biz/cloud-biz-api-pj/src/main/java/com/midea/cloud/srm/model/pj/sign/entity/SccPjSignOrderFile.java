package com.midea.cloud.srm.model.pj.sign.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 签署业务附件关系表
 * @author huangbf3
 * @date 2023-09-21
 */
@Data
@TableName("scc_pj_sign_order_file")
public class SccPjSignOrderFile extends BaseEntity {

 @ApiModelProperty("主键ID")
 @TableId("SIGN_ORDER_FILE_ID")
 private Long signOrderFileId;

 @ApiModelProperty("签署业务单据关系表ID")
 @TableField("SIGN_ORDER_ID")
 private Long signOrderId;

 @ApiModelProperty("业务单据附件ID")
 @TableField("ORDER_FILE_ID")
 private Long orderFileId;

 @ApiModelProperty("来源类型：SRM、长城")
 @TableField("FROM_TYPE")
 private String fromType;

 @ApiModelProperty("合同文档ID")
 @TableField("SIGN_FILE_ID")
 private Long signFileId;

 @ApiModelProperty("文件名称")
 @TableField("File_Name")
 private String filename;

}

