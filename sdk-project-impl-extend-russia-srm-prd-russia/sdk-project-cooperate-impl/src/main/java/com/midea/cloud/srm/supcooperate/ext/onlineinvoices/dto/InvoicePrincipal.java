package com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
public class InvoicePrincipal implements Serializable {

    private Long principalId;
    private Long orgId;
    private String orgCode;
    private String orgName;
    private String principalCode;
    private String principalName;
    private String profitCenterCode;
    private String profitCenterName;
}
