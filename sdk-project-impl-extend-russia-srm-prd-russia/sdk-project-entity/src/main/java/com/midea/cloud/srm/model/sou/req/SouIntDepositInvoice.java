package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

/**
 * 寻源单意向金开票表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
@ApiModel(description = "寻源单意向金开票表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_sou_intention_deposit_invoice")
public class SouIntDepositInvoice extends BaseEntity {
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId
    private Long invoiceId;

    /**
     * 意向金开票单号
     */
    @ApiModelProperty("意向金开票单号")
    private String invoiceNo;

    /**
     * 接口返回的意向金开票编号
     */
    @ApiModelProperty("接口返回的意向金开票编号")
    private String reqInvoiceNo;

    /**
     * 意向金开票代码
     */
    @ApiModelProperty("意向金开票代码")
    private String invoiceCode;

    /**
     * 寻源单ID
     */
    @ApiModelProperty("寻源单ID")
    private Long reqHeadId;

    /**
     * 寻源需求单报名ID
     */
    @ApiModelProperty("寻源需求单报名ID")
    private Long applyId;
    /**
     * 红字发票对应的发票ID主键
     */
    @ApiModelProperty("红字发票对应的发票ID主键")
    private Long fromDepositInvoiceId;
    @ApiModelProperty("红字发票对应的发票单号")
    private String fromDepositInvoiceNo;

    /**
     * 单据状态
     */
    @ApiModelProperty("单据状态")
    private String status;
    @ApiModelProperty("电话")
    private String phone;

    /**
     * 项目名称
     */
    @ApiModelProperty("项目名称")
    private String projectName;

    /**
     * 发票类型(发票,红字发票)
     */
    @ApiModelProperty("发票类型(发票,红字发票)")
    private String invoiceType;

    /**
     * 开票公司
     */
    @ApiModelProperty("开票公司")
    private String invoiceCompany;

    /**
     * 纳税人识别号
     */
    @ApiModelProperty("纳税人识别号")
    private String taxPayer;

    /**
     * 开户银行
     */
    @ApiModelProperty("开户银行")
    private String bankName;

    /**
     * 开户账号
     */
    @ApiModelProperty("开户账号")
    private String bankAccount;

    /**
     * 委托代付凭证附件ID
     */
    @ApiModelProperty("委托代付凭证附件ID")
    private Long entrustPayVoucherFileId;

    /**
     * 委托代付凭证附件名称
     */
    @ApiModelProperty("委托代付凭证附件名称")
    private String entrustPayVoucherFileName;

    /**
     * 地址
     */
    @ApiModelProperty("地址")
    private String address;

    /**
     * 发票接收邮箱
     */
    @ApiModelProperty("发票接收邮箱")
    private String invoiceReceiverEmail;

    /**
     * 开票数量
     */
    @ApiModelProperty("开票数量")
    private Integer invoiceQuantity;

    /**
     * 单价(元)
     */
    @ApiModelProperty("单价(元)")
    private BigDecimal price;

    /**
     * 供应商id
     */
    @ApiModelProperty("供应商id")
    private Long vendorId;

    /**
     * 供应商编码/企业标识
     */
    @ApiModelProperty("供应商编码/企业标识")
    private String vendorCode;

    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /**
     * 开具红字发票原因
     */
    @ApiModelProperty("开具红字发票原因")
    private String redInvoiceReason;

    /**
     * 开具发票失败原因
     */
    @ApiModelProperty("开具发票失败原因")
    private String applyInvoiceFailReason;

    /**
     * 结算单据编码
     */
    @ApiModelProperty("结算单据编码")
    private String settleDocumentCode;

    /**
     * 开具状态
     */
    @ApiModelProperty("开具状态")
    private String createInvoiceStatus;

    @ApiModelProperty("附件id")
    private Long fileId;
    @ApiModelProperty("附件名称")
    private String fileName;
    @ApiModelProperty("附件url")
    private String fileUrl;


}
