package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author: panmq
 * @Date: 2024/05/08/ $
 * @Description: 采购需求提报新增明细导出-实体类
 */
@Data
@ApiModel("采购需求提报新增明细导出-实体类")
public class ExtPrSouRequirementLineExportDto {

    @ApiModelProperty("申请ID")
    @ExcelIgnore
    private Long requirementHeadId;

    @ApiModelProperty("申请编号")
    @ExcelProperty("申请编号")
    private String requirementHeadNum;
    @ApiModelProperty("物资编码")
    @ExcelProperty("物资编码")
    private String materialCode;
    @ApiModelProperty("物资名称")
    @ExcelProperty("物资名称")
    private String materialName;
    @ApiModelProperty("采购品类")
    @ExcelProperty("采购品类")
    private String categoryName;
    @ApiModelProperty("规格型号")
    @ExcelProperty("规格型号")
    private String extMaterialModel;
    @ApiModelProperty("品牌")
    @ExcelProperty("品牌")
    private String brand;
    @ApiModelProperty("使用部门")
    @ExcelProperty("使用部门")
    private String extUseDepartmentName;
    @ApiModelProperty("使用人联系方式")
    @ExcelProperty("使用人联系方式")
    private String extUserPhone;
    @ApiModelProperty("基本计量单位")
    @ExcelProperty("基本计量单位")
    private String unit;
    @ApiModelProperty("需求数量")
    @ExcelProperty("需求数量")
    private BigDecimal requirementQuantity;
    @ApiModelProperty("预估单价")
    @ExcelProperty("预估单价")
    private BigDecimal extPredictPrice;
    @ApiModelProperty("预估总价")
    @ExcelProperty("预估总价")
    private BigDecimal extPredictAmount;
    @ApiModelProperty("本次需求日期")
    @ExcelProperty(value = "本次需求日期", converter = ExtPrLocalDateConverter.class)
    private LocalDate requirementDate;
    @ApiModelProperty("收货地址")
    @ExcelProperty("收货地址")
    private String receiveAddress;
    @ApiModelProperty("收货人")
    @ExcelProperty("收货人")
    private String extReceiver;
    @ApiModelProperty("区域编码")
    @ExcelIgnore
    private String extAreaCode;
    @ApiModelProperty("收货人联系方式")
    @ExcelProperty("收货人联系方式")
    private String receiveTelephone;
    @ApiModelProperty("区域")
    @ExcelProperty("区域")
    private String extAreaName;
    @ApiModelProperty("用途")
    @ExcelProperty("用途")
    private String extUseTo;
    @ApiModelProperty("费用科目")
    @ExcelProperty("费用科目")
    private String extFeeSubject;
    @ApiModelProperty("备注")
    @ExcelProperty("备注")
    private String comments;
    @ApiModelProperty("是否商品")
    @ExcelProperty("是否商品")
    private String extProductFlag;
    @ApiModelProperty("实时库存")
    @ExcelProperty("实时库存")
    private BigDecimal extActualStock;
    @ApiModelProperty("共享库存")
    @ExcelProperty("共享库存")
    private BigDecimal extShareStock;
}
