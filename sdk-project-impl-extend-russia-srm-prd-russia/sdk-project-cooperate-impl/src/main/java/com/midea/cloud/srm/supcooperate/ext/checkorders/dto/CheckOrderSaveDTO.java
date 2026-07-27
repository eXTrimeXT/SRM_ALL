package com.midea.cloud.srm.supcooperate.ext.checkorders.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
public class CheckOrderSaveDTO extends CheckOrder {
    private List<CheckOrderDetail> detailList;
}
