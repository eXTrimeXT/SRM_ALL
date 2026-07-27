package com.midea.cloud.srm.model.base.dto;

import lombok.Data;

/**
 * @author zenghx2
 */
@Data
public class OrgQueryDTO {

    private Long parentId;
    private String type;
    private Boolean existsOwner;
    private String organizationCode;
    private String organizationName;
}
