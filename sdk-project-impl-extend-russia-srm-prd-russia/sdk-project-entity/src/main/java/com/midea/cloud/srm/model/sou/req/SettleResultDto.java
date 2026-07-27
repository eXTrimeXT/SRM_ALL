package com.midea.cloud.srm.model.sou.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/05/10/ $
 * @Description: 结算结果返回实体
 */
@Data
@ApiModel("code")
public class SettleResultDto {

    @ApiModelProperty("返回结果编码")
    private Integer code;

    @ApiModelProperty("返回结果信息")
    private String msg;

    @ApiModelProperty("返回结果数据")
    private DataDto data;
    
    
    @Data
    @ApiModel("返回结果数据")
    public static class DataDto {
        
        @ApiModelProperty("系统编码")
        private String systemCode;
        @ApiModelProperty("单据号")
        private String businessNo;
        @ApiModelProperty("流水")
        private String reqSn;
        @ApiModelProperty("结算单号")
        private String settleDocumentCode;
        @ApiModelProperty("结算单据状态（01-拟定，02-审批中，03-驳回，04-已审批，05-已完成，06-已作废，07-已确认）")
        private String state;
        @ApiModelProperty("结算单据状态说明")
        private String stateExplain;
        @ApiModelProperty("返回结果明细")
        private List<ItemDto> items;
    }

    @Data
    @ApiModel("返回结果明细")
    public static class ItemDto {
        @ApiModelProperty("序号")
        private String itemNo;

        @ApiModelProperty("发票明细")
        private List<InvoiceDto> invoiceList;

        @ApiModelProperty("结算明细")
        private AccountingInfoDto accountingInfo;
    }

    @Data
    @ApiModel("发票明细")
    public static class InvoiceDto {

        @ApiModelProperty("服务编码")
        private String serviceCode;
        @ApiModelProperty("状态编码")
        private String stateCode;
        @ApiModelProperty("状态描述")
        private String stateDesc;
        @ApiModelProperty("状态解析")
        private String stateExplain;
        @ApiModelProperty("发票列表")
        private List<InvoiceListDto> list;
    }

    @Data
    @ApiModel("发票列表")
    public static class InvoiceListDto {
        @ApiModelProperty("发票号")
        private String invoiceNo;
        @ApiModelProperty("发票代码")
        private String invoiceCode;
        @ApiModelProperty("发票附件URL")
        private String invoiceUrl;
        @ApiModelProperty("发票不含税金额")
        private String invoiceExcludeTaxAmount;
        @ApiModelProperty("发票含税金额")
        private String invoiceTaxAmount;
        @ApiModelProperty("发票金额")
        private String invoiceAmount;
        @ApiModelProperty("发票日期")
        private String invoiceDate;
    }


    @Data
    @ApiModel("结算信息")
    public static class AccountingInfoDto{
        @ApiModelProperty("状态码")
        private String stateCode;
        @ApiModelProperty("状态描述")
        private String stateDesc;
        @ApiModelProperty("状态解析")
        private String stateExplain;

        @ApiModelProperty("凭证列表")
        private List<String> voucherNos;

    }

}
