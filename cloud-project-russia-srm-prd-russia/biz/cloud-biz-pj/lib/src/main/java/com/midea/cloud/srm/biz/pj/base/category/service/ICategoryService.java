package com.midea.cloud.srm.biz.pj.base.category.service;

import com.midea.cloud.srm.model.pj.changchengapi.dto.CategoryApiParamDTO;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CategoryDTO;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @author huangbf3
 */
public interface ICategoryService {
    /**
     * b
     * @param categoryApiParamDTO
     * @param serialNum
     * @param processGroupId
     * @return
     */
    @ApiOperation(value = "分页查询物料品类")
    List<CategoryDTO> findCategoryFromApi(CategoryApiParamDTO categoryApiParamDTO, String serialNum, Long processGroupId);

    /**
     * 备注
     * @param categoryApiParamDTO
     * @param serialNum
     */
    void saveOrUpdateBatch(CategoryApiParamDTO categoryApiParamDTO, String serialNum);
}
