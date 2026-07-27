package com.midea.cloud.srm.model.contract.dto;

import lombok.Data;

import java.util.List;

/**
 * 合同创建类
 *
 * @author 100014323
 */
@Data
public class Action {
    private Integer serialNo;
    private List<Operator> actionOperators;

}