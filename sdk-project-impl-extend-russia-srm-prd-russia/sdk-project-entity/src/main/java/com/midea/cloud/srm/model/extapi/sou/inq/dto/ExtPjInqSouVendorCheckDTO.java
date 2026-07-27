package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
public class ExtPjInqSouVendorCheckDTO {

    private Long projectId;

    private List<Long> vendorIds;
}
