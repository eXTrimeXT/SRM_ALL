package com.midea.cloud.srm.model.supcooperate.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqExcelPropertyValues;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.enums.DeliveryNoteSource;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/11
 */
@Data
public class DeliveryNoteExcel {
    @ExcelIgnore
    private Long deliveryNoteId;
    @ExcelProperty("送货单号")
    private String deliveryNumber;
    @ExcelProperty("送货日期")
    private String deliveryDate;
    @ExcelProperty("单据状态")
    @ExtInqExcelPropertyValues(dictCode = "DELIVERY_NOTE_DETAIL_STATUS")
    private String deliveryNoteStatus;
    @ExcelProperty("货运方式")
    @ExtInqExcelPropertyValues(dictCode = "DELIVERY_WAY")
    private String ceeaDeliverySystem;
    @ExcelProperty("业务实体")
    private String orgName;
    @ExcelProperty("库存组织")
    private String organizationName;
    @ExcelProperty("收货地址")
    private String ceeaDeliveryPlace;
    @ExcelProperty("供应商名称")
    private String vendorName;
    @ExcelProperty("采购员")
    private String extPurchaserName;
    @ExcelProperty("备注")
    private String comments;
    @ExcelProperty("创建人")
    private String createdFullName;
    @ExcelProperty("创建日期")
    private Date creationDate;
    @ExcelIgnore
    private String beginDate;
    @ExcelIgnore
    private String endDate;
    @ExcelIgnore
    private String orgId;
    @ExcelIgnore
    private String extPurchaserNo;
}
