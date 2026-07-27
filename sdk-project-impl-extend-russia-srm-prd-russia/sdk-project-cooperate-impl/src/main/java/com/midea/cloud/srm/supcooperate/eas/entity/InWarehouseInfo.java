package com.midea.cloud.srm.supcooperate.eas.entity;

import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class InWarehouseInfo {

    private String workType;
    private String materielCode;
    private String materielDescribe;
    private String materielType;
    private String collectNum;
    private String basicUnit;
    private String taxPrice;
    private String taxRate;
    private String taxAmount;
    private String rateTotal;
    private String currency;
    private String exchangeRate;
    private String collectTime;
    private String warehousingNum;
    private String warehousingLineNum;
    private String orderNo;
    private String orderLineNum;
    private String deliveryNoteNum;
    private String deliveryNoteLineNum;
    private String businessEntity;
    private String inventoryOrg;
    private String supplierCode;
    private String supplierName;
    private String purchaser;
    private String isCreateNo;

}
