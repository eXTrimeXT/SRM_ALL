package com.midea.cloud.srm.model.sou.fixprice.vo;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceLineStatusEnum;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPricePaymentMethodEnum;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceSourceFromTypeEnum;
import com.mideacloud.common.util.BeanUtil;
import com.mideacloud.common.util.DateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author 100014336 ganyh19
 */
@Data
public class ExtFixPriceExportVO {


    @ExcelProperty("定价单号")
    @ApiModelProperty("定价单号")
    private String fixPriceNo;

    @ExcelProperty("定价单状态")
    @ApiModelProperty("定价单状态")
    private String fixPriceStatus;

    @ExcelProperty("申请日期")
    @ApiModelProperty("申请日期")
    private String fixPriceDate;


    @ExcelProperty("采购部门")
    @ApiModelProperty("采购部门")
    private String orgDepName;

    @ExcelProperty("未税总金额")
    @ApiModelProperty("未税总金额")
    private BigDecimal totalNotaxPrice;

    @ExcelProperty("价税合计")
    @ApiModelProperty("价税合计")
    private BigDecimal totalTaxPrice;

    @ExcelProperty("最高含税单价")
    @ApiModelProperty("最高含税单价")
    private BigDecimal highestTaxPrice;

    @ExcelProperty("物料编码")
    @ApiModelProperty("物料编码")
    private String itemCode;

    @ExcelProperty("物料名称")
    @ApiModelProperty("物料名称")
    private String itemDesc;

    @ExcelProperty("规格型号")
    @ApiModelProperty("物料规格型号")
    private String extMaterialModel;

    @ExcelProperty("计量单位")
    @ApiModelProperty("单位")
    private String unit;

    @ExcelProperty("数量")
    @ApiModelProperty("数量")
    private BigDecimal quantity;

    @ExcelProperty("供应商名称")
    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ExcelProperty("未税单价")
    @ApiModelProperty("未税单价")
    private BigDecimal notaxPrice;

    @ExcelProperty("税率(%)")
    @ApiModelProperty("税率值")
    private BigDecimal taxRate;

    @ExcelProperty("未税总价")
    @ApiModelProperty("未税总价")
    private BigDecimal notaxTotalPrice;

    @ExcelProperty("发票类型")
    @ApiModelProperty("发票类型(EXT_SOU_INQ_ORDER_INVOICE_TYPE)")
    private String invoiceType;

    /**
     * 采购需求那边的最低价供应商
     */
    @ExcelProperty("近期最低价格(未税)")
    @ApiModelProperty("近期最低价格(未税)")
    private BigDecimal latestMinNotaxPrice;


    /**
     * 最低价跟当前价的比较
     */
    @ExcelProperty("浮动比例(%)")
    @ApiModelProperty("浮动比例")
    private BigDecimal priceFloatScale;

    @ExcelProperty("近期最低价供应商")
    @ApiModelProperty("近期最低价供应商名称")
    private String latestMinVendorName;

    /**
     * 采购需求那边的最低价的品牌
     */
    @ExcelProperty("近期最低价品牌")
    @ApiModelProperty("近期最低价品牌")
    private String latestMinBrand;

    /** @see ExtInqSouOrderItem#getExtWinReason */
    @ExcelProperty("中标原因")
    @ApiModelProperty("中标原因")
    private String extWinReason;

    /**  询价才有 @see SouOrderItem#getOrderRemark */
    @ExcelProperty("供应商备注")
    @ApiModelProperty("供应商备注")
    private String orderRemark;

    @ExcelProperty("预付款说明")
    @ApiModelProperty("预付款说明")
    private String advancePaymentRemark;

    /** 供货周期 -- 询价 */
    @ExcelProperty("到货周期(自然日)")
    @ApiModelProperty("供货周期")
    private Integer extLeadTime;


    /** 采购员昵称 -- 询价 */
    @ExcelProperty("采购员")
    @ApiModelProperty("采购员昵称")
    private String buyerNickname;

    /** RequirementHead#ceeaPrType (application_form_type) */
    @ExcelProperty("申请类型")
    @ApiModelProperty("申请类型")
    private String applyType;

    /** 质保期 -- 询价 */
    @ExcelProperty("质保期(自然日)")
    @ApiModelProperty("质保期")
    private Integer extWarrantyPeriod;

    @ExcelProperty("申请单位")
    @ApiModelProperty("业务实体名称(申请单位)")
    private String orgOuName;

    /** 预估单价 -- 采购需求 */
    @ExcelProperty("预估单价")
    @ApiModelProperty("预估单价")
    private BigDecimal extPredictPrice;

    /** 预估总金额 -- 采购需求 */
    @ExcelProperty("预估总价")
    @ApiModelProperty("预估总价")
    private BigDecimal extPredictAmount;

    @ExcelProperty("来源类型")
    @ApiModelProperty("来源类型")
    private String sourceFromType;

    /** 购买类型 -- 采购需求 */
    @ExcelProperty("购买类型")
    @ApiModelProperty("购买类型(PR_BUY_TYPE)")
    private String extBuyType;

    @ExcelProperty("询价单号")
    @ApiModelProperty("来源单据编号")
    private String sourceFromNo;

    /** 审批状态(通过/不通过) */
    @ExcelProperty("审批状态")
    @ApiModelProperty("审批状态")
    private String fixPriceLineStatus;

    @ExcelProperty("是否关闭")
    @ApiModelProperty("是否关闭")
    private String hasClosed;


    /** @see ExtFixPricePaymentMethodEnum */
    @ExcelProperty("付款方式")
    @ApiModelProperty("付款方式")
    private String paymentMethod;

    @ExcelProperty("付款条款")
    @ApiModelProperty("付款条款")
    private String paymentTerm;

    @ExcelProperty("是否签订合同")
    @ApiModelProperty("是否签订合同")
    private String hasSignedContract;


    public static ExtFixPriceExportVO createExtFixPriceExportVO(ExtFixPriceHead extFixPriceHead, ExtFixPriceLine extFixPriceLine,Map<String, DictItemDTO> invoiceTypeMap, Map<String, DictItemDTO> applyTypeMap, Map<String, DictItemDTO> extBuyTypeMap, Map<String, DictItemDTO> paymentMethodMap,Map<String, DictItemDTO> extPriceStatusMap){
        ExtFixPriceExportVO extFixPriceExportVO = BeanUtil.copyProperties(extFixPriceLine,ExtFixPriceExportVO.class);
        DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(ObjectUtil.isNotNull(extFixPriceHead)){
            BeanUtils.copyProperties(extFixPriceHead,extFixPriceExportVO);
            extFixPriceExportVO.setFixPriceDate(extFixPriceHead.getFixPriceDate().format(dfDate));
        }
        if(ObjectUtil.isAllNotEmpty(invoiceTypeMap,extFixPriceLine.getInvoiceType())){
            DictItemDTO dictItemDTO = invoiceTypeMap.get(extFixPriceLine.getInvoiceType());
            if(ObjectUtil.isNotNull(dictItemDTO)){
                extFixPriceExportVO.setInvoiceType(dictItemDTO.getDictItemName());
            }
        }
        if(ObjectUtil.isAllNotEmpty(applyTypeMap,extFixPriceLine.getApplyType())){
            DictItemDTO dictItemDTO = applyTypeMap.get(extFixPriceLine.getApplyType());
            if(ObjectUtil.isNotNull(dictItemDTO)){
                extFixPriceExportVO.setApplyType(dictItemDTO.getDictItemName());
            }
        }

        if(ObjectUtil.isAllNotEmpty(extBuyTypeMap,extFixPriceLine.getExtBuyType())){
            DictItemDTO dictItemDTO = extBuyTypeMap.get(extFixPriceLine.getExtBuyType());
            if(ObjectUtil.isNotNull(dictItemDTO)){
                extFixPriceExportVO.setExtBuyType(dictItemDTO.getDictItemName());
            }
        }

        if(ObjectUtil.isNotNull(extFixPriceLine.getPaymentMethod())){
            DictItemDTO dictItemDTO = paymentMethodMap.get(extFixPriceLine.getPaymentMethod());
            if(ObjectUtil.isNotNull(dictItemDTO)){
                extFixPriceExportVO.setPaymentMethod(dictItemDTO.getDictItemName());
            }
        }

        if(ObjectUtil.isNotNull(extFixPriceHead.getFixPriceStatus())){
            DictItemDTO dictItemDTO = extPriceStatusMap.get(extFixPriceHead.getFixPriceStatus());
            if(ObjectUtil.isNotNull(dictItemDTO)){
                extFixPriceExportVO.setFixPriceStatus(dictItemDTO.getDictItemName());
            }
        }

        if(ObjectUtil.isNotNull(extFixPriceLine.getFixPriceLineStatus())){
            if(ExtFixPriceLineStatusEnum.PASS.equals(extFixPriceLine.getFixPriceLineStatus())){
                extFixPriceExportVO.setFixPriceLineStatus("通过");
            } else if(ExtFixPriceLineStatusEnum.UN_PASS.equals(extFixPriceLine.getFixPriceLineStatus())){
                extFixPriceExportVO.setFixPriceLineStatus("不通过");
            }
        }

        if(ObjectUtil.isNotNull(extFixPriceLine.getSourceFromType())){
            if(ExtFixPriceSourceFromTypeEnum.PURCHASE_REQ.equals(extFixPriceLine.getSourceFromType())){
                extFixPriceExportVO.setSourceFromType("需求");
            }
            if(ExtFixPriceSourceFromTypeEnum.INQ.equals(extFixPriceLine.getSourceFromType())){
                extFixPriceExportVO.setSourceFromType("询比价");
            }
        }
        extFixPriceExportVO.setHasClosed(getEnableDesc(extFixPriceLine.getHasClosed().name()));
        extFixPriceExportVO.setHasSignedContract(getEnableDesc(extFixPriceLine.getHasSignedContract().name()));
        return extFixPriceExportVO;
    }


    private static String getEnableDesc(String enable){
        if(Enable.Y.name().equalsIgnoreCase(enable)){
            return "是";
        } else if(Enable.N.name().equalsIgnoreCase(enable)) {
            return "否";
        }
        return "";
    }




















}
