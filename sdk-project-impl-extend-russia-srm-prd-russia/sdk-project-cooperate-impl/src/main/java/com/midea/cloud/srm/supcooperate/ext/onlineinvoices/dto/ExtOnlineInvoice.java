package com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto;

import com.midea.cloud.srm.supcooperate.meiql.onlineinvoice.dto.OnlineInvoiceDTO;
import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
public class ExtOnlineInvoice extends OnlineInvoiceDTO {

    private String extStatus;

    /**
     * 关闭原因
     */
    private String comment;

    /**
     * 单据来源
     */
    private String extSource;
    /**
     * 是否代开发票 Y/N
     */
    private String extBehalfInvoice;
    /**
     * 是否进项税转出 Y/N
     */
    private String extInputTax;
    /**
     * 是否免赠 Y/N
     */
    private String extFreeOfCharge;
    /**
     * 开票主体编码
     */
    private String extPrincipalCode;
    /**
     * 开票主体名称
     */
    private String extPrincipalName;
    /**
     * 利润中心编码
     */
    private String extProfitCenterCode;
    /**
     * 利润中心名称
     */
    private String extProfitCenterName;
    /**
     * 是否同步eas
     */
    private String extSyncEas;
}
