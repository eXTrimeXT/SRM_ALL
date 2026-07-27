package com.midea.cloud.srm.model.pj.base.organization.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author huangbf3
 */
@Data
public class BpmSccPjOrganizationRoleDto implements Serializable {

    private List<Long> ids;

    private String operation;
}
