package com.midea.cloud.srm.model.sou.fixprice.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtFixPriceInqOrderItemsQueryVO extends SouOrderItem {

    /** @see SouProject#getSouNo */
    @ApiModelProperty("询价单号")
    private String souNo;

    /** @see ExtInqSouItem#getExtMaterialModel */
    @ApiModelProperty("物料规格型号")
    private String extMaterialModel;

    /** @see ExtInqSouItem#getExtBrand */
    @ApiModelProperty("品牌")
    private String extBrand;

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /** @see SouItem#getOrgOuId */
    @ApiModelProperty("业务实体ID(申请单位)")
    private Long orgOuId;

    /** @see SouItem#getOrgOuCode */
    @ApiModelProperty("业务实体编码(申请单位)")
    private String orgOuCode;

    /** @see SouItem#getOrgOuName */
    @ApiModelProperty("业务实体名称(申请单位)")
    private String orgOuName;

    @ApiModelProperty("未税总价")
    private BigDecimal standardNotaxTotalPrice;

    /** @see ExtInqSouItem#getExtAreaId */
    @ApiModelProperty("区域ID")
    private Long extAreaId;

    /** @see ExtInqSouItem#getExtAreaCode */
    @ApiModelProperty("区域编码")
    private String extAreaCode;

    /** @see ExtInqSouItem#getExtAreaName */
    @ApiModelProperty("区域名称")
    private String extAreaName;

    @ApiModelProperty("近期最低价格(未税)")
    private BigDecimal latestMinNotaxPrice;

    @ApiModelProperty("浮动比例")
    private BigDecimal priceFloatScale;

    @ApiModelProperty("近期最低供应商ID")
    private Long latestMinVendorId;

    @ApiModelProperty("近期最低供应商编码")
    private String latestMinVendorCode;

    @ApiModelProperty("近期最低供应商名称")
    private String latestMinVendorName;

    @ApiModelProperty("近期最低价品牌")
    private String latestMinBrand;

    /** @see ExtInqSouOrderItem#getExtWinReason */
    @ApiModelProperty("修改中标原因")
    private String extWinReason;

    /** @see ExtInqSouOrderItem#getAdvancePaymentRemark */
    @ApiModelProperty("预付款说明")
    private Enable advancePaymentRemark;

    /** @see ExtInqSouOrderItem#getSpecialPaymentRemark */
    @ApiModelProperty("特殊付款说明")
    private String specialPaymentRemark;

    /** @see ExtInqSouOrderItem#getExtLeadTime */
    @ApiModelProperty("供货周期")
    private Integer extLeadTime;

    @ApiModelProperty("采购员账号")
    private String buyerUsername;

    @ApiModelProperty("采购员昵称")
    private String buyerNickname;

    @ApiModelProperty("申请类型")
    private String applyType;

    /** @see ExtInqSouOrderItem#getExtWarrantyPeriod */
    @ApiModelProperty("保修期(保质期)")
    private Integer extWarrantyPeriod;

    @ApiModelProperty("预估单价")
    private BigDecimal extPredictPrice;

    @ApiModelProperty("预估总价")
    private BigDecimal extPredictAmount;

    @ApiModelProperty("来源类型")
    private ExtFixPriceSourceFromTypeEnum sourceFromType;

    @ApiModelProperty("购买类型")
    private String extBuyType;

    /** @see ExtInqSouItem#getExtSourceFromLineIds */
    @ApiModelProperty("来源单据明细ID集合(因为原表用Long类型，无法支持)")
    private String extSourceFromLineIds;

    @ApiModelProperty("指定物料指定轮次已报价次数")
    private Integer extOrderCount;

    @ApiModelProperty("使用部门")
    private String useDeptName;

    @ApiModelProperty("当前轮次")
    private Integer currentRound;

}
