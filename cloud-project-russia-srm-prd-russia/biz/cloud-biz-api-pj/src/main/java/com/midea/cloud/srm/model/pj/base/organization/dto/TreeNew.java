package com.midea.cloud.srm.model.pj.base.organization.dto;

import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TreeNew extends Organization {

    private Long relId;

    private Long parentOrganizationId;

    private List<TreeNew> childOrganRelation;

}
