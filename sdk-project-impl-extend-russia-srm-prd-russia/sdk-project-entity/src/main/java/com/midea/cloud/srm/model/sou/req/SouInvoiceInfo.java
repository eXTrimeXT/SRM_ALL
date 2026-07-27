package com.midea.cloud.srm.model.sou.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/1 11:36
 *  修改内容:
 * </pre>
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SouInvoiceInfo {
    /**
     * 主键
     */
    private Long invoiceInfoId;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 供应商id
     */
    private Long vendorId;

    /**
     * 供应商编码/企业标识
     */
    private String vendorCode;

    private String vendorName;

    /**
     * 纳税人识别号
     */
    private String taxPayer;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 开户账号
     */
    private String bankAccount;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 发票接收邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;
}
