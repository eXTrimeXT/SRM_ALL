package com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto;

import com.midea.cloud.srm.supcooperate.meiql.onlineinvoice.dto.OnlineInvoiceDetailDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
@Accessors(chain = true)
public class ExtOnlineInvoiceDetail extends OnlineInvoiceDetailDTO {

    /**
     * 单据来源
     */
    private String extSource;
    /**
     * 验收单明细id
     */
    private Long extCheckDetailId;
    /**
     * 尾差调整金额
     */
    private BigDecimal extAdjustAmount;
    /**
     * 转出税额
     */
    private BigDecimal extInputTaxAmount;
    /**
     * sap成本中心编码
     */
    private String extSapCostCode;
    /**
     * sap成本中心内容
     */
    private String extSapCostContent;
    /**
     * 发票用途编码
     */
    private String extInvoiceUsage;
}
