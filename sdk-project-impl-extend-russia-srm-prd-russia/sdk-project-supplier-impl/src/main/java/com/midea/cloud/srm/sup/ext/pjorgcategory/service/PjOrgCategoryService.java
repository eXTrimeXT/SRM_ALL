package com.midea.cloud.srm.sup.ext.pjorgcategory.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.sup.orgcategory.entity.PjOrgCategory;

import java.util.List;
/**
 * @author luxc18
 */
public interface PjOrgCategoryService extends BaseService<PjOrgCategory> {

    /**
     * listPageHeader
     * @param pjOrgCategory
     * @return
     */
    List<PjOrgCategory> listPageHeader(PjOrgCategory pjOrgCategory);

    /**
     * listPageDetailByHeader
     * @param pjOrgCategory
     * @return
     */
    List<PjOrgCategory> listPageDetailByHeader(PjOrgCategory pjOrgCategory);
}
