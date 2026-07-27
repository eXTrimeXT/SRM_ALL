package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
public class ExtInqCloseItemParams {

    private Long souItemId;

    private String reason;
}
