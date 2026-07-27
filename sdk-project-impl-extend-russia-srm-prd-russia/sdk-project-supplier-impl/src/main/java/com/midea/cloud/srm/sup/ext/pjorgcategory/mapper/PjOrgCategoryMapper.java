package com.midea.cloud.srm.sup.ext.pjorgcategory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sup.orgcategory.entity.PjOrgCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author luxc18
 */
public interface PjOrgCategoryMapper extends BaseMapper<PjOrgCategory> {

    /**
     * listPageHeader
     * @param pjOrgCategory
     * @return
     */
    List<PjOrgCategory> listPageHeader(PjOrgCategory pjOrgCategory);

    /**
     * listByHeaderKey
     * @param list
     * @param pjOrgCategory
     * @return
     */
    List<PjOrgCategory> listByHeaderKey(@Param("list") List<PjOrgCategory> list,@Param("pjOrgCategory") PjOrgCategory pjOrgCategory);

}
