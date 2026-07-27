package com.midea.cloud.srm.sou.deposit.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.sou.deposit.entity.FinanceCompany;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author ex_liuxy46
 */
public class FinanceDto {

    private static final DateTimeFormatter LOCAL_DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 意向金
     * @param financeCompany 司库财务公司
     * @param souReqHead 寻源
     * @param companyInfo 供应商
     * @param payAccountName 付款方名称
     * @return 字符串
     */
    public static String dealData(FinanceCompany financeCompany, SouReqHead souReqHead, CompanyInfo companyInfo, String payAccountName, String systemSourceNo) {
        JSONObject jo = new JSONObject();
        jo.put("BusinessSource", 2);
        jo.put("SystemSource", "SRM");
        jo.put("ReceiptType", 1);
        jo.put("ReceiptSubType", 1);
        jo.put("IsInternal", 2);
        //预计收款日期
        jo.put("ReceiptEstimateTime", LocalDate.now().format(LOCAL_DATE_PATTERN));
        //收款单位编号，司库系统单位公司代码
        jo.put("ReceiptOrgCode", financeCompany.getCompanyCode());
        jo.put("ReceiptOrgName", financeCompany.getCompanyName());
        //收款账号，司库系统银行账户
        jo.put("ReceiptAccountNo", financeCompany.getAccount().replace(" ", ""));
        jo.put("CurrencyCode", "CNY");
        jo.put("CurrencyName", "人民币");
        //金额格式，不带千分符
        jo.put("ReceiptAmount", souReqHead.getDepositAmount() == null ? "0" : souReqHead.getDepositAmount().toString());
        //付款账号名称
        jo.put("PayAccountName", payAccountName);
        //付款方性质，1.企业、2个人；若不传值时默认1
        jo.put("PayNatureType", "PERSONAL".equals(companyInfo.getOverseasRelation()) ? "2" : "1");
        //来源系统单据号
        jo.put("SystemSourceNo", systemSourceNo);
        //款项类型编码
        jo.put("PaymentType", "ISST6WL010000196");
        jo.put("ContactsOrgType", "1");
        //利润中心编码
        jo.put("ProfitCenterCode", financeCompany.getProfitCenterCode());
        //利润中心名称
        jo.put("ProfitCenterName", financeCompany.getProfitCenterName());
        //收款账号名称
        jo.put("ReceiptAccountName", souReqHead.getBankAccountName().replace(" ", ""));
        //收款开户单位
        jo.put("ReceiptDepositCompany", souReqHead.getBankAccountName());
        //收款开户银行
        jo.put("ReceiptDepositBank", souReqHead.getBankName());
        //收款开户银行号
        jo.put("ReceiptDepositNo", souReqHead.getBankNumber());
        //付款单位编号
        jo.put("PayOrgCode", companyInfo.getCompanyCode());
        //付款单位名称
        jo.put("PayOrgName", companyInfo.getCompanyName());
        return jo.toString();
    }

    /**
     * 保证金
     * @param souProject 寻源
     * @param companyInfo 供应商
     * @param financeCompany 司库财务公司
     * @param extSouMargin 保证金
     * @param payAccountName 付款方名称
     * @return 字符串
     */
    public static String dealData(ExtSouProject souProject, CompanyInfo companyInfo, FinanceCompany financeCompany, ExtSouMargin extSouMargin, String payAccountName, String systemSourceNo) {
        JSONObject jo = new JSONObject();
        jo.put("BusinessSource", 2);
        jo.put("SystemSource", "SRM");
        jo.put("ReceiptType", 1);
        jo.put("ReceiptSubType", 1);
        jo.put("IsInternal", 2);
        //预计收款日期
        jo.put("ReceiptEstimateTime", LocalDate.now().format(LOCAL_DATE_PATTERN));
        //收款单位编号，司库系统单位公司代码
        jo.put("ReceiptOrgCode", financeCompany.getCompanyCode());
        jo.put("ReceiptOrgName", financeCompany.getCompanyName());
        //收款账号，司库系统银行账户
        jo.put("ReceiptAccountNo", financeCompany.getAccount().replace(" ", ""));
        jo.put("CurrencyCode", "CNY");
        jo.put("CurrencyName", "人民币");
        //金额格式，不带千分符
//        jo.put("ReceiptAmount", extSouMargin.getPayAmount() == null ? "0" : extSouMargin.getPayAmount().toString());
        jo.put("ReceiptAmount", souProject.getExtEarnestAmount().multiply(new BigDecimal(10000)).toString());
        //付款账号名称
        jo.put("PayAccountName", payAccountName);
        //付款方性质，1.企业、2个人；若不传值时默认1
        jo.put("PayNatureType", "PERSONAL".equals(companyInfo.getOverseasRelation()) ? "2" : "1");
        //来源系统单据号
        jo.put("SystemSourceNo", systemSourceNo);
        //款项类型编码
        jo.put("PaymentType", "ISST6WL010000110");
        jo.put("ContactsOrgType", "1");
        //利润中心编码
        jo.put("ProfitCenterCode", financeCompany.getProfitCenterCode());
        //利润中心名称
        jo.put("ProfitCenterName", financeCompany.getProfitCenterName());
        //收款账号名称
        jo.put("ReceiptAccountName", souProject.getExtBankAccountName());
        //收款开户单位
        jo.put("ReceiptDepositCompany", souProject.getExtBankAccountName());
        //收款开户银行
        jo.put("ReceiptDepositBank", souProject.getExtBankName());
        //收款开户银行号
        jo.put("ReceiptDepositNo", souProject.getExtBankNumber());
        //付款单位编号
        jo.put("PayOrgCode", companyInfo.getCompanyCode());
        //付款单位名称
        jo.put("PayOrgName", companyInfo.getCompanyName());
        return jo.toString();
    }

    /**
     * 应收单撤销查询接口
     * @param systemSourceNoList 外部单据号列表
     * @return 字符串
     */
    public static String dealCxData(List<String> systemSourceNoList) {
        JSONObject jo = new JSONObject();
        jo.put("BusinessSource", 2);
        jo.put("SystemSource", "SRM");
        jo.put("RevokeTimeStart", LocalDate.now().format(LOCAL_DATE_PATTERN));
        jo.put("RevokeTimeEnd", LocalDate.now().format(LOCAL_DATE_PATTERN));
        jo.put("SystemSourceNoList", systemSourceNoList);
        return jo.toString();
    }

    /**
     * 认领结果查询接口
     * @param systemSourceNoList 外部单据号列表
     * @return 字符串
     */
    public static String dealRlData(List<String> systemSourceNoList) {
        JSONArray ja = new JSONArray();
        systemSourceNoList.forEach(e -> {
            JSONObject jo = new JSONObject();
            jo.put("BusinessSource", 2);
            jo.put("SystemSourceNo", e);
            ja.add(jo);
        });
        return ja.toString();
    }

}
