package com.midea.cloud.srm.sup.ext.pjorgcategory.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.sup.orgcategory.entity.PjOrgCategory;
import com.midea.cloud.srm.sup.ext.pjorgcategory.service.PjOrgCategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 品类库二开
 * @author 100014323
 */
@Api(value = "PjOrgCategoryController", tags = {"品类库二开"})
@RestController
@RequestMapping("/pj/orgCategory")
public class PjOrgCategoryController {

    @Autowired
    private PjOrgCategoryService pjOrgCategoryService;


    @PostMapping("/listPageHeader")
    public PageInfo<PjOrgCategory> listPageHeader(@RequestBody PjOrgCategory pjOrgCategory) {
        PageUtil.startPage(pjOrgCategory.getPageNum(), pjOrgCategory.getPageSize());
        return new PageInfo<PjOrgCategory>(pjOrgCategoryService.listPageHeader(pjOrgCategory));
    }

    @PostMapping("/listPageDetailByHeader")
    public PageInfo<PjOrgCategory> listPageDetailByHeader(@RequestBody PjOrgCategory pjOrgCategory) {
        PageUtil.startPage(pjOrgCategory.getPageNum(), pjOrgCategory.getPageSize());
        return new PageInfo<PjOrgCategory>(pjOrgCategoryService.listPageDetailByHeader(pjOrgCategory));
    }


}
