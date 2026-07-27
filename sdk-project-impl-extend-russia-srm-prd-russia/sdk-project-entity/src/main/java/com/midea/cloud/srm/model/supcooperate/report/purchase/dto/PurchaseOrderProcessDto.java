package com.midea.cloud.srm.model.supcooperate.report.purchase.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@ApiModel("采购进度报表实体类")
@Data
public class PurchaseOrderProcessDto extends BaseDTO {
    @ApiModelProperty("采购申请编号")
    private String requirementHeadNum;
    @ApiModelProperty("申请日期")
    private Date applyDate;
    @ApiModelProperty("审批结束日期")
    private Date extApproveTime;
    @ApiModelProperty("需求日期")
    private Date requirementDate;
    @ApiModelProperty("申请人")
    private String applyFullName;
    @ApiModelProperty("使用人")
    private String extUserName;
    @ApiModelProperty("使用部门")
    private String extUseDepartmentName;
    @ApiModelProperty("申请单位")
    private String orgName;
    @ApiModelProperty("物料编码")
    private String materialCode;
    @ApiModelProperty("物料名称")
    private String materialName;
    @ApiModelProperty("规格型号")
    private String extMaterialModel;
    @ApiModelProperty("计量单位")
    private String unitCode;
    @ApiModelProperty("需求数量")
    private BigDecimal requirementQuantity;
    @ApiModelProperty("物料购买类型")
    private String extBuyType;
    @ApiModelProperty("质保期（自然日）")
    private Integer extWarrantyPeriod;
    @ApiModelProperty("交货日期")
    private Date deliveryDate;
    @ApiModelProperty("到货周期")
    private Integer extDeliveryCycle;
    @ApiModelProperty("订单编号")
    private String orderNumber;
    @ApiModelProperty("供应商名称")
    private String vendorName;
    @ApiModelProperty("供应商编码")
    private String vendorCode;
    @ApiModelProperty("采购员")
    private String ceeaEmpUsername;
    @ApiModelProperty("采购单位")
    private String extPurchaserOrgName;
    @ApiModelProperty("采购员联系方式")
    private String extPurchaserPhone;
    @ApiModelProperty("实际订单数量")
    private String orderNum;
    @ApiModelProperty("订单日期")
    private Date ceeaPurchaseOrderDate;
    @ApiModelProperty("备注信息")
    private String comments;
    @ApiModelProperty("订单取消原因")
    private String closedCause;
    @ApiModelProperty("送货单单号")
    private String deliveryNumber;
    @ApiModelProperty("发货数量")
    private BigDecimal deliveryQuantity;
    @ApiModelProperty("发货日期")
    private Date deliveryTime;
    @ApiModelProperty("收货日期")
    private Date receivedTime;
    @ApiModelProperty("收货数量")
    private BigDecimal receivedNum;
    @ApiModelProperty("入库数量")
    private BigDecimal warehouseQuantity;
    @ApiModelProperty("入库日期")
    private Date extStorageTime;
    @ApiModelProperty("未入库数量")
    private BigDecimal withoutWarehouseQuantity;

    @ApiModelProperty("采购申请ID")
    private Long requirementHeadId;
    @ApiModelProperty("物料ID")
    private Long materialId;
    @ApiModelProperty("计量单位名称）")
    private String unit;
    @ApiModelProperty("申请单行ID")
    private Long ceeaRequirementLineId;
    @ApiModelProperty("申请单号")
    private String ceeaRequirementHeadNum;
    @ApiModelProperty("采购单位ID")
    private Long orderId;
    @ApiModelProperty("供应商ID")
    private Long vendorId;
    @ApiModelProperty("采购订单明细ID")
    private Long orderDetailId;
    @ApiModelProperty("送货单ID")
    private Long deliveryNoteId;
    @ApiModelProperty("已入库数量")
    private BigDecimal extStorageQty;

    @ApiModelProperty("需求状态")
    private String auditStatus;
    @ApiModelProperty("关闭原因")
    private String extClosedCause;
    @ApiModelProperty("收货地址")
    private String receiveAddress;
    @ApiModelProperty("收货人")
    private String extReceiver;
    @ApiModelProperty("收货人联系方式")
    private String receiveTelephone;
    @ApiModelProperty("区域")
    private String extAreaCode;
    @ApiModelProperty("区域")
    private String extAreaName;

    @ApiModelProperty("物流单号")
    private String extExpressNo;
}
