package com.midea.cloud.srm.model.contract.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;


/**
 * 【长城SRM】【合同履约埋点】
 * BUG2024011700024
 * @author 100014336 ganyh19
 * 定时器执行，查找合同履约计划里程碑的计划开始时间前两天
 * 查询返回dto
 */
@Data
public class ContractPerPlanStartTimeDto {

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 履约里程碑计划开始时间
     */
    private Date planStartDate;

    /**
     * 经办人账号
     */
    private String extContractHandlerAccount;

    /**
     * 里程碑类型
     */
    private String milestoneType;
}
