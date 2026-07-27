package com.midea.cloud.srm.mall.request.jd.order;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class JDOrderSubmitRequestDTO extends BaseRequestDTO {
    
    private String thirdOrder;
    private String sku;
    private String name;
    private Integer province;
    private Integer city;
    private Integer county;
    private Integer town;
    private String address;
    private String zip;
    private String phone;
    private String mobile;
    private String email;
    private String remark;
    private Integer invoiceState;
    private Integer invoiceType;
    private Integer  selectedInvoiceTitle;
    private String companyName;
    private Integer  invoiceContent;
    private Integer  paymentType;
    private String payDetails;
    private Integer isUseBalance;
    private Integer  submitState;
    private String   invoiceName;
    private String   invoicePhone;
    private Integer   invoiceProvice;
    private Integer  invoiceCity;
    private Integer invoiceCounty;
    private String   invoiceAddress;
    private String  regCompanyName;
    private String  regCode;
    private String  regAddr;
    private String   regPhone;
    private String  regBank;
    private String  regBankAccount;
    private Integer   reservingDate;

    private Integer installDate;
    private boolean needInstall;
    private String promiseDate;
    private String promiseTimeRange;
    private Integer promiseTimeRangeCode;
    private String reservedDateStr;
    private String reservedTimeRange;
    private String cycleCalendar;
    private String poNo;
    private boolean validHolidayVocation;
    private Map<String,Object> customOrderExt;


}
