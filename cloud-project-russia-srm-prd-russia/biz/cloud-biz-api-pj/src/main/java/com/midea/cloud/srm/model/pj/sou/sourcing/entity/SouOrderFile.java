package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileConfigTypeEnum;
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
@TableName("scc_sou_order_file")
@ApiModel("供应商报价附件")
public class SouOrderFile extends BaseEntity<SouOrderFile> {

    @TableId("ORDER_FILE_ID")
    @ApiModelProperty("ID")
    private Long orderFileId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder#getOrderId */
    @TableField("ORDER_ID")
    @ApiModelProperty("报价头ID")
    private Long orderId;

    /** @see SouFileConfig#getSouFileConfigId */
    @TableField("SOU_FILE_CONFIG_ID")
    @ApiModelProperty("配置文件Id")
    private Long souFileConfigId;

    /** @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder#getVendorId */
    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouOrder#getRound */
    @TableField("ROUND")
    @ApiModelProperty("轮次")
    private Integer round;

    /** @see SouFileConfig#getFileType */
    @TableField("FILE_TYPE")
    @ApiModelProperty("附件类型")
    private SouFileConfigTypeEnum fileType;

    @TableField("ORDER_DOC_ID")
    @ApiModelProperty("文件ID")
    private Long orderDocId;

    @TableField("ORDER_FILE_NAME")
    @ApiModelProperty("文件名")
    private String orderFileName;

    @TableField("ORDER_REMARK")
    @ApiModelProperty("备注")
    private String orderRemark;

}
