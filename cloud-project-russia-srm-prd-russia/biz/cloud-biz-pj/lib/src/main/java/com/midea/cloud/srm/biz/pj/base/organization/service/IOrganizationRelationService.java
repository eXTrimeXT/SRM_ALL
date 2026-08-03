package com.midea.cloud.srm.biz.pj.base.organization.service;

import com.midea.cloud.srm.model.pj.base.organization.dto.TreeNew;

import java.util.List;

/**
 * @author huangbf3
 */
public interface IOrganizationRelationService {
    /**
     * 备注
     * @param organizationRelation
     * @return
     */
    List<TreeNew> assembleTreeByParentNew(TreeNew organizationRelation);

    /**
     * 查询集团-板块-公司结构
     * @return
     */
    List<TreeNew> treeNewAllGroupBuOu();

}
