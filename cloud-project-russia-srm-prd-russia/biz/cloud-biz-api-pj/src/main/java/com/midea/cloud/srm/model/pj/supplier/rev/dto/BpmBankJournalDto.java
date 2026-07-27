package com.midea.cloud.srm.model.pj.supplier.rev.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangbf3
 */
@Data
public class BpmBankJournalDto implements Serializable {

    /**
     * 银行代码
     */
    private String yhdm;

    /**
     * 银行名称
     */
    private String yhmc;

    /**
     * 开户行名称
     */
    private String khhmc;

    /**
     * 分行编码
     */
    private String fhbm;

    /**
     * 账户名称
     */
    private String zhmc;

    /**
     * 银行账号
     */
    private String yhzh;

    /**
     * 币种
     */
    private String bz;

    /**
     * 是否主账号
     */
    private String sfzzh;

    /**
     * 启用
     */
    private String qy;

}
