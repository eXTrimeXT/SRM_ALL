package com.midea.cloud.srm.model.contract.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author GW00086630
 */
@Data
public class ContractHeadSourceDto  {
    /**
     * 项目编号
     */
    private String sourceNumber;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 招标负责人账号
     */
    private String extInviteHeadAccount;
    /**
     * 经办人账号
     */
    private String extContractHandlerAccount;
    /**
     * 合同有效日期至
     */
    private LocalDate effectiveDateTo;
    /**
     * 月份
     */
    private String month;
}
