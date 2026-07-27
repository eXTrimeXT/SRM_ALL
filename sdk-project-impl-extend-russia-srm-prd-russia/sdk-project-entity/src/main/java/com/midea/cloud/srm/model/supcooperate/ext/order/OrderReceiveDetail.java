package com.midea.cloud.srm.model.supcooperate.ext.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.entry.DeliveryNote;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.entry.DeliveryNoteDetail;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 100014336 ganyh19
 */
@Data
public class OrderReceiveDetail  {


    private Long orderDetailId;

    /**
     * @see Order#getVendorName()
     */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /**
     * @see Order#getVendorCode()
     */
    @ApiModelProperty("供应商代码")
    private String vendorCode;

    /**
     * @see ExtOrder#getExtPurchaserOrgCode
     */
    @ApiModelProperty("采购单位代码")
    private String extPurchaserOrgCode;

    /**
     * @see ExtOrder#getExtPurchaserOrgName
     */
    @ApiModelProperty("采购单位名称")
    private String extPurchaserOrgName;

    /**
     * @see Order#getCeeaOrgCode()
     */
    @ApiModelProperty("业务实体编码(申请单位编码)")
    private String ceeaOrgCode;

    /**
     * @see Order#getCeeaOrgName()
     */
    @ApiModelProperty("业务实体名称(申请单位名称)")
    private String ceeaOrgName;

    /**
     * @see ExtOrderDetail#getExtUseDepartmentCod()
     */
    @ApiModelProperty("使用部门编码")
    private String extUseDepartmentCode;


    /**
     * @see ExtOrderDetail#getExtUseDepartmentName()
     */
    @ApiModelProperty("使用部门编码")
    private String extUseDepartmentName;



    /**
     * @see OrderDetail#getMaterialCode()
     */
    @ApiModelProperty("物料编码")
    private String materialCode;

    /**
     * @see OrderDetail#getMaterialName()
     */
    @ApiModelProperty("物料名称")
    private String materialName;

    /**
     * @see OrderDetail#getSpecification()
     */
    @ApiModelProperty("物料规格")
    private String specification;

    /**
     * 申请日期
     * @see ExtOrder#getExtApplyDate
     */
    @ApiModelProperty("申请日期")
    private Date extApplyDate;

    /**
     * 订单日期
     * @see Order#getCeeaPurchaseOrderDate()
     */
    @ApiModelProperty("订单日期")
    private Date ceeaPurchaseOrderDate;

    /**
     * @see Order#getOrderNumber()
     */
    @ApiModelProperty("订单编号")
    private String orderNumber;


    /**
     * @see DeliveryNote#getDeliveryNumber()
     */
    @ApiModelProperty("送货单编号")
    private String deliveryNumber;

    /**
     * @see ExtOrderDetail#getDeliveryDate
     */
    @ApiModelProperty("交货日期")
    private String deliveryDate;

    /**
     * @see ExtOrderDetail#getDeliveryCycle
     */
    @ApiModelProperty("到货周期")
    private String extDeliveryCycle;

    /**
     * @see OrderDetail#getUnit()
     */
    @ApiModelProperty("基本计量单位")
    private String unit;


    @ApiModelProperty("基本计量单位描述")
    private String unitDesc;

    /**
     * @see ExtOrderDetail#getExtBrand
     */
    @ApiModelProperty("品牌")
    private String extBrand;

    /**
     * @see ExtOrder#getCeeaEmpNo
     */
    @ApiModelProperty("账户工号(采购员员工工号)")
    private String ceeaEmpNo;

    /**
     * @see ExtOrder#getCeeaEmpUserId
     */
    @ApiModelProperty("采购员id")
    private Long ceeaEmpUserId;
    /**
     * @see ExtOrder#getCeeaEmpUserName
     */
    @ApiModelProperty("采购员名称")
    private String ceeaEmpUsername;

    /**
     * @see OrderDetail#getRequirementQuantity()
     */
    @ApiModelProperty("需求数量")
    private BigDecimal requirementQuantity;

    /**
     * @see OrderDetail#getOrderNum()
     */
    @ApiModelProperty("订单数量")
    private BigDecimal orderNum;

    /**
     * @see OrderDetail#getStorageNum()
      */
    @ApiModelProperty("入库数量")
    private BigDecimal storageNum;

    /**
     * @see OrderDetail#getReceiveNum()
     */
    @ApiModelProperty("收货数量")
    private BigDecimal receiveNum;


    /**
     * @see OrderDetail#getReceiveSum()
     */
    @ApiModelProperty("收货总数")
    private BigDecimal receiveSum;

    /**
     * @see OrderDetail#getOrderDetailStatus()
     */
    @ApiModelProperty("订单行状态")
    private String orderDetailStatus;


    @ApiModelProperty("订单行状态描述")
    private String orderDetailStatusDesc;

    /**
     * @see ExtDeliveryNoteDetail#getExtDetailStatus
     */
    @ApiModelProperty("送货单行状态")
    private String deliveryNoteDetailStatus;


    @ApiModelProperty("送货单行状态描述")
    private String deliveryNoteDetailStatusDesc;


    @ApiModelProperty("采购订单取消原因")
    private String orderCancelReason;


    @ApiModelProperty("供应商联系人")
    private String extVendorContacts;

    @ApiModelProperty("供应商电话")
    private String extVendorPhone;

    @ApiModelProperty("采购类型")
    private String extBuyType;

    @ApiModelProperty("使用人")
    private String extUserName;

    @ApiModelProperty("使用人工号")
    private String extUserCode;

    @ApiModelProperty("EAS收货时间(max(EXT_RECEIVE_TIME,EXT_STORAGE_TIME))")
    private Date easReceiveTime;

    @ApiModelProperty("EAS收货时间")
    private Date extReceiveTime;

    @ApiModelProperty("EAS入库时间")
    private Date extStorageTime;


    @ApiModelProperty("品类")
    private String categoryName;

    private String ceeaOrgId;
}
