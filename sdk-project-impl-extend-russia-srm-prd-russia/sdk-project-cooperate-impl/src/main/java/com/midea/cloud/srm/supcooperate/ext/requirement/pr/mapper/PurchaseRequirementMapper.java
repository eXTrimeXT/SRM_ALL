package com.midea.cloud.srm.supcooperate.ext.requirement.pr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementLineDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.RequirementSelectionQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface PurchaseRequirementMapper extends BaseMapper<PurchaseRequirementHeadDTO> {
    /**
     * 备注
     * @param params
     * @return
     */
    List<PurchaseRequirementLineDTO> selectWithPriceAgree(@Param("params") RequirementSelectionQueryDTO params);

    /**
     * selectWithFixPrice
     * @param params
     * @return
     */
    List<PurchaseRequirementLineDTO> selectWithFixPrice(@Param("params") RequirementSelectionQueryDTO params);

}
