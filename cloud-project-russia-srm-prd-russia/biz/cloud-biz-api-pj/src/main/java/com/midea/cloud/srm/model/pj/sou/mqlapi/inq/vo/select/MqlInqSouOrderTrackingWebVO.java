package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.vo.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * MQL - 供应商报价基本信息，用于评选管理界面展示
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqlInqSouOrderTrackingWebVO {

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
