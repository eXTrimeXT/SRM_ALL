package com.midea.cloud.srm.base.material.service.impl;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.srm.base.material.service.IMaterialItemService;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.material.dto.MaterialItemQueryDto;
import com.midea.cloud.srm.model.base.quicksearch.common.InputSearchParam;
import com.midea.cloud.srm.model.objectx.dto.ConditionDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Service
public class MaterialForQuickSearchComponent {

    @Autowired
    private IMaterialItemService iMaterialItemService;

    /**
     * 反射快查
     * @param materialItemQueryDto
     * @return
     */
    public PageInfo<MaterialItem> listPageByCondition(MaterialItemQueryDto materialItemQueryDto) {
        materialItemQueryDto.setItemStatus(YesOrNo.YES.getValue());
        if(StringUtils.isNotEmpty(materialItemQueryDto.getMaterialType())){
            // 增加查询条件
            List<ConditionDTO> conditionDTOS = new ArrayList<>();
            ConditionDTO conditionDTO  = new ConditionDTO();
            conditionDTO.setField("materialType");
            conditionDTO.setOperator("like");
            conditionDTO.setValue(materialItemQueryDto.getMaterialType());
            conditionDTOS.add(conditionDTO);
            materialItemQueryDto.setExtendConditions(conditionDTOS);
        }
        return iMaterialItemService.listPageByCondition(materialItemQueryDto);
    }

    /**
     * 反射快查
     * @param inputSearchParam
     * @return
     */
    public PageInfo<MaterialItem> listPageByCondition(InputSearchParam inputSearchParam) {
        MaterialItemQueryDto materialItemQueryDto = new MaterialItemQueryDto();
        materialItemQueryDto.setPageNum(inputSearchParam.getPageNum());
        materialItemQueryDto.setPageSize(inputSearchParam.getPageSize());
        materialItemQueryDto.setMaterialCodeOrName(inputSearchParam.getInputValue());
        materialItemQueryDto.setItemStatus(YesOrNo.YES.getValue());
        return iMaterialItemService.listPageByCondition(materialItemQueryDto);
    }


}
