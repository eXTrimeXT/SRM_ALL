package com.midea.cloud.srm.model.sou.designplans.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "集采台账-需求信息")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_demand_info")
public class SccSouChDemandInfo extends BaseEntity<SccSouChDemandInfo> {

    @ApiModelProperty("需求id")
    @TableId("DEMAND_ID")
    private Long demandId;

    @ApiModelProperty("品牌")
    @TableField("BRAND")
    private String brand;

    @ApiModelProperty("订单状态，多个用逗号分隔")
    @TableField("ORDER_STATUS")
    private String orderStatus;

    @ApiModelProperty("单项物资订单数")
    @TableField("ORDER_NUM")
    private String orderNum;
    @ApiModelProperty("单项物资订单数类型,大于、小于、等于、大于等于、小于等于")
    @TableField("ORDER_NUM_TYPE")
    private String orderNumType;

    @ApiModelProperty("单项物资采购金额（未税）,大于、小于、等于、大于等于、小于等于")
    @TableField("BUY_MONEY_TYPE")
    private String buyMoneyType;
    @ApiModelProperty("单项物资采购金额（未税）")
    @TableField("BUY_MONEY")
    private String buyMoney;

    @ApiModelProperty("上年订单日期从")
    @TableField("LAST_YEAR_ORDER_DATE_START")
    private Date lastYearOrderDateStart;

    @ApiModelProperty("上年订单日期到")
    @TableField("LAST_YEAR_ORDER_DATE_END")
    private Date lastYearOrderDateEnd;

    @ApiModelProperty("上上年订单日期从")
    @TableField("LAST_LAST_YEAR_ORDER_DATE_START")
    private Date lastLastYearOrderDateStart;

    @ApiModelProperty("上上年订单日期到")
    @TableField("LAST_LAST_YEAR_ORDER_DATE_END")
    private Date lastLastYearOrderDateEnd;
}
