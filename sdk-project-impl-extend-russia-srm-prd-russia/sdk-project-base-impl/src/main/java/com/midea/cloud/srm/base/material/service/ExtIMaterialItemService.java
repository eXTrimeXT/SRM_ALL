package com.midea.cloud.srm.base.material.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.base.entity.ExtMaterialItem;
import com.midea.cloud.srm.model.base.entity.ExtMaterialItemDto;
import com.midea.cloud.srm.model.base.material.dto.MaterialItemQueryDto;

import java.util.List;

public interface ExtIMaterialItemService extends BaseService<ExtMaterialItem> {
    void ceeaSaveOrUpdate(ExtMaterialItemDto materialItem);

    /**
     * 根据查询参数获取中台物料编码
     * @param materialItemQueryDto
     * @return
     */
    List<String> queryItemCodes(MaterialItemQueryDto materialItemQueryDto);
}
