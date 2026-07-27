package com.midea.cloud.srm.model.sou.ipaddresss.dto;

import lombok.Data;
import java.io.Serializable;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class IpAddressDto implements Serializable {

    /**
     * 招标id
     */
    private String bidsId;

    /**
     * 供应商id
     */
    private String supplierId;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 来源
     */
    private String source;
}
