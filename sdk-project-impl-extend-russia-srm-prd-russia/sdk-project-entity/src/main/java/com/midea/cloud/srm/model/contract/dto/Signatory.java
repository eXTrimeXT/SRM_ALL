package com.midea.cloud.srm.model.contract.dto;

import lombok.Data;

import java.util.List;

/**
 * 合同创建实体类
 * @author 100014323
 */
@Data
public class Signatory {
    private String tenantName;
    private String receiverNumber;
    private Integer serialNo;
    private String contact;
    private List<Action> actions;
}