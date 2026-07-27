package com.midea.cloud.srm.model.pj.supplier.rev.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangbf3
 */
@Data
public class BpmCateJournalDto implements Serializable {

    /**
     * 引入品类
     */
    private String yrpl;

    /**
     * 品类本年度采购金额（万元）
     */
    private String plbnd;
}
