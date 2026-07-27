package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
public class StockQueryDTO implements Serializable {

    private String orgCode;
    private List<String> materialCodes;
}
