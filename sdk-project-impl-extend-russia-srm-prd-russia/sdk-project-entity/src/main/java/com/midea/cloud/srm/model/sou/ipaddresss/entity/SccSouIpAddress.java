package com.midea.cloud.srm.model.sou.ipaddresss.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("scc_sou_ip_address")
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_ip_address")
public class SccSouIpAddress extends BaseEntity {

    @ApiModelProperty("ip地址ID")
    @TableId("ROW_ID")
    private Long rowId;

    @TableField("BIDS_ID")
    @ApiModelProperty("招标id")
    private Long bidsId;

    @ApiModelProperty("供应商id")
    @TableField("SUPPLIER_ID")
    private Long supplierId;

    @ApiModelProperty("供应商编码")
    @TableField("SUPPLIER_CODE")
    private String supplierCode;

    @ApiModelProperty("供应商名称")
    @TableField("SUPPLIER_NAME")
    private String supplierName;

    @ApiModelProperty("ip地址")
    @TableField("IP")
    private String ip;

    @ApiModelProperty("监控时间")
    @TableField("MONITOR_TIME")
    private String monitorTime;

    @ApiModelProperty("监控时间从")
    @TableField(exist = false)
    private String monitorTimeFrom;

    @TableField(exist = false)
    @ApiModelProperty("监控时间到")
    private String monitorTimeTo;

    @ApiModelProperty("来源")
    @TableField("SOURCE")
    private String source;

    @ApiModelProperty("不同供应商，相同ip的标识")
    @TableField(exist = false)
    private String sameIpFlag;

    @ApiModelProperty("招标id")
    @TableField(exist = false)
    private Long projectId;
}
