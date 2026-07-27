package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zenghx2
 */
@Data
public class PurchaseRequirementLineDTO extends RequirementLine {

    @ApiModelProperty("需求池状态,Y有效 N已关闭")
    private String extPoolStatus;
    @ApiModelProperty("关闭原因")
    private String extClosedCause;
    @ApiModelProperty("关闭附件id")
    private Long extClosedFileId;
    @ApiModelProperty("关闭附件名称")
    private String extClosedFileName;

    @ApiModelProperty("附件id")
    private Long extAttachId;
    @ApiModelProperty("附件名称")
    private String extAttachName;

    @ApiModelProperty("是否商品，Y是，N否")
    private String extProductFlag;
    @ApiModelProperty("预估单价")
    private BigDecimal extPredictPrice;
    @ApiModelProperty("预估总价")
    private BigDecimal extPredictAmount;

    @ApiModelProperty("使用部门id")
    private String extUseDepartmentId;
    @ApiModelProperty("使用部门编码")
    private String extUseDepartmentCode;
    @ApiModelProperty("使用部门名称")
    private String extUseDepartmentName;
    @ApiModelProperty("使用人工号")
    private String extUserCode;
    @ApiModelProperty("使用人名称")
    private String extUserName;

    @ApiModelProperty("分单人名称")
    private String extPushUserName;
    @ApiModelProperty("分单人工号")
    private String extPushUserCode;
    @ApiModelProperty("分单时间")
    private LocalDateTime extPushTime;

    @ApiModelProperty("用途")
    private String extUseTo;

    @ApiModelProperty("型号")
    private String extMaterialModel;

    @ApiModelProperty("购买类型，字典PURCHASE_REQUIREMENT_BUY_TYPE")
    private String extBuyType;
    @ApiModelProperty("购买备注")
    private String extBuyTypeComment;

    @ApiModelProperty("是否引出历史供应商，Y/N")
    private String extHistoryVendorFlag;
    @ApiModelProperty("历史供应商编码1")
    private String extHistoryVendorCode1;
    @ApiModelProperty("历史供应商名称1")
    private String extHistoryVendorName1;
    @ApiModelProperty("历史供应商价格1")
    private BigDecimal extHistoryVendorPrice1;
    @ApiModelProperty("历史供应商品牌1")
    private String extHistoryVendorBrand1;
    @ApiModelProperty("历史供应商订单1")
    private Long extHistoryOrderDetail1;
    @ApiModelProperty("历史供应商编码2")
    private String extHistoryVendorCode2;
    @ApiModelProperty("历史供应商名称2")
    private String extHistoryVendorName2;
    @ApiModelProperty("历史供应商价格2")
    private BigDecimal extHistoryVendorPrice2;
    @ApiModelProperty("历史供应商品牌2")
    private String extHistoryVendorBrand2;
    @ApiModelProperty("历史供应商订单2")
    private Long extHistoryOrderDetail2;
    @ApiModelProperty("历史供应商编码3")
    private String extHistoryVendorCode3;
    @ApiModelProperty("历史供应商名称3")
    private String extHistoryVendorName3;
    @ApiModelProperty("历史供应商价格3")
    private BigDecimal extHistoryVendorPrice3;
    @ApiModelProperty("历史供应商品牌3")
    private String extHistoryVendorBrand3;
    @ApiModelProperty("历史供应商订单3")
    private Long extHistoryOrderDetail3;

    @ApiModelProperty("区域id")
    private String extAreaId;
    @ApiModelProperty("区域编码")
    private String extAreaCode;
    @ApiModelProperty("区域名称")
    private String extAreaName;
    @ApiModelProperty("共享库存")
    private BigDecimal extShareStock;
    @ApiModelProperty("实时库存")
    private BigDecimal extActualStock;
    @ApiModelProperty("收货人")
    private String extReceiver;


    /**
     * 近期采购类数据
     */
    @ApiModelProperty("预付款说明")
    private Enable extAdvancePaymentRemark;
    @ApiModelProperty("供货周期")
    private Integer extLeadTime;
    @ApiModelProperty("发票类型")
    private String extInvoiceType;
    @ApiModelProperty("质保期")
    private Integer extWarrantyPeriod;

    /**
     * 实际价格源数据
     */
    @ApiModelProperty("协议编码")
    private String agreementCode;
    @ApiModelProperty("协议行id")
    private Long agreementInfoId;
    @ApiModelProperty("协议性质")
    private String agreementType;
    @ApiModelProperty("定价单编码")
    private String fixPriceNo;
    @ApiModelProperty("未税单价")
    private BigDecimal unitPrice;
    @ApiModelProperty("含税单价")
    private BigDecimal taxPrice;
    @ApiModelProperty("起订量")
    @TableField("START_NUM")
    private Integer startNum;
    @ApiModelProperty("供货周期")
    private Integer leadTime;
    @ApiModelProperty("发票类型")
    private String invoiceType;
    @ApiModelProperty("质保期")
    private Integer warrantyPeriod;
    @ApiModelProperty("付款方式")
    private String paymentMethod;
    @ApiModelProperty("付款条款")
    private String paymentTerm;
    @ApiModelProperty("开票主体")
    private String invoiceOrgName;
    /**  询价才有 @see SouOrderItem#getOrderRemark */
    @TableField("ORDER_REMARK")
    @ApiModelProperty("供应商备注")
    private String orderRemark;

    public String getOrderKey(){
        return getOrgId() + "-" + getCeeaPerformUserId() + "-" + getReceiveAddress();
    }

    /**
     * 不同供应商、业务实体、采购员、收货地址、币种、付款条款、付款方式不能一起下单
     * @return key
     */
    public String getOrderGroupKey() {
        return getVendorId() + "-" + getOrgId() + "-" + getCeeaPerformUserId() + "-" + getReceiveAddress()
                +  getCurrencyName() + "-" + getPaymentTerm() + "-" + getPaymentMethod();
    }
}
