package com.midea.cloud.srm.model.sou.agreement.dto;

import lombok.Data;

import java.io.Serializable;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class AreGroupDto implements Serializable {

    private String supplyArea;

    private String buyOrgCode;

    private Integer num;
}
