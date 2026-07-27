package com.midea.cloud.srm.model.pj.sou.inq.vo.webapi.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 简易询价 - 供应商报价基本信息，用于评选管理界面展示
 *
 * @author linxc6@meicloud.com
 * @version 1.00.00
 * <p>
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人: zhangwk12@meicloud.com
 *  修改日期: 2021-10-28
 *  修改内容:
 * </pre>
 */
@ApiModel(description = "简易询价 - 供应商报价基本信息，用于评选管理界面展示 <p> <pre>  修改记录  修改后版本:  修改人: zhangwk12@meicloud.com  修改日期: 2021-10-28  修改内容: </pre>")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InqSouOrderTrackingWebVO extends BaseObjectX {

    /** @see SouOrder#getOrderId */
    @ApiModelProperty("报价单ID")
    private Long orderId;

    /** @see SouOrder#getOrderNo */
    @ApiModelProperty("报价单号")
    private String orderNo;

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("报价单状态")
    private SouOrderStatusEnum orderStatus;

    /** @see SouOrder#getWithdrawReason */
    @ApiModelProperty("报价撤回原因")
    private String withdrawReason;

    /** @see SouOrder#getWithdrawTime */
    @ApiModelProperty("报价撤回时间")
    private Date withdrawTime;

    /** @see SouOrder#getSubmitBy */
    @ApiModelProperty("提交人")
    private String submitBy;

    /** @see SouOrder#getSubmitFullName */
    @ApiModelProperty("提交人姓名")
    private String submitFullName;

    /** @see SouOrder#getSubmitTime */
    @ApiModelProperty("提交时间")
    private Date submitTime;

    /** @see SouOrder#getSubmitByIp */
    @ApiModelProperty("供应商报价IP")
    private String submitByIp;

    /** @see SouOrder#getRejectReason */
    @ApiModelProperty("作废说明")
    private String rejectReason;

    /** @see SouOrder#getRejectTime */
    @ApiModelProperty("作废时间")
    private Date rejectTime;

    /** @see SouVendor#getLinkmanName */
    @ApiModelProperty("联系人")
    private String linkmanName;

    /** @see SouVendor#getPhone */
    @ApiModelProperty("联系方式")
    private String phone;

    /** @see SouVendor#getEmail */
    @ApiModelProperty("电子邮件")
    private String email;

    /** @see SouOrder#getStandardNotaxTotalPrice */
    @ApiModelProperty("本币总未税报价")
    private BigDecimal standardNotaxTotalPrice;

    /** @see SouOrder#getStandardTaxTotalPrice */
    @ApiModelProperty("本币总含税报价")
    private BigDecimal standardTaxTotalPrice;

}
