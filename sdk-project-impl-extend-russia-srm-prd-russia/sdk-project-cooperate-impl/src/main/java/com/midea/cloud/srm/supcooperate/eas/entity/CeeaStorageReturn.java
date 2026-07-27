package com.midea.cloud.srm.supcooperate.eas.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 备注
 * @author huangbf3
 */
@Data
@TableName("ceea_storage_return")
@EqualsAndHashCode(callSuper = true)
public class CeeaStorageReturn extends BaseEntity<CeeaStorageReturn> {

    @ApiModelProperty("接收单号")
    @TableField("RECEIVE_ORDER_NO")
    private String receiveOrderNo;

    @ApiModelProperty("接收单号")
    @TableField("RECEIVE_ORDER_LINE_NO")
    private String receiveOrderLineNo;

    @ApiModelProperty("库存组织ID")
    @TableField("ORGANIZATION_ID")
    private String organizationId;

    @ApiModelProperty("库存组织编码")
    @TableField("ORGANIZATION_CODE")
    private String organizationCode;

    @ApiModelProperty("库存组织名称")
    @TableField("ORGANIZATION_NAME")
    private String organizationName;

    @ApiModelProperty("入库日期")
    @TableField("WAREHOUSING_DATE")
    private Date warehousingDate;
}
