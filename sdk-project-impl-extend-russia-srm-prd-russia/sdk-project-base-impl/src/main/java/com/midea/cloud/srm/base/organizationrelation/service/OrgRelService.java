package com.midea.cloud.srm.base.organizationrelation.service;

import com.midea.cloud.srm.model.pj.base.organization.dto.TreeNew;

import java.util.List;

/**
 * @author ex_liuxy46
 */
public interface OrgRelService {

    /**
     * treeNew
     * @return list
     */
    List<TreeNew> allTree();
}
