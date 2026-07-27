package com.midea.cloud.srm.supcooperate.ext.order.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zenghx2
 */
@Data
public class ExtOrder extends Order {

    private Long ceeaEmpUserId;

    /**
     * 执行中：orderStatus=APPROVED, extStatus=ONGOING
     * 已完成：orderStatus=APPROVED, extStatus=FINISHED
     */
    private String extStatus;
    private String extVendorContacts;
    private String extVendorPhone;
    private String extPurchaserOrgCode;
    private String extPurchaserOrgName;
    private String extPurchaserPhone;
    private String extPurchaserEmail;
    private String extOrderProperty;

    /**
     * 申请人名称
     */
    @TableField("EXT_APPLICANT_NAME")
    private String extApplicantName;

    /**
     * 申请人工号
     */
    @TableField("EXT_APPLICANT_CODE")
    private String extApplicantCode;

    /**
     * 申请日期
     */
    @TableField("EXT_APPLY_DATE")
    private Date extApplyDate;

    private Long extApproveUserId;
    private String extApproveUserCode;
    private String extApproveUserName;
    private String extAreaCode;

    private List<ExtOrderDetail> detailList;

    /**
     * 京东主单号
     */
    @TableField("EXT_JD_ORDER_ID")
    private Long extJdOrderId;

    /**
     * 供定时任务用，定时查询京东订单的拆单情况
     * 1已提交订单，待确认拆单信息；2已确认拆单消息/未提交订单；
     */
    @TableField("EXT_JD_STATE")
    private Integer extJdState;
}
