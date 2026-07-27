package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service;

import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.context.RequirementCancleContext;

import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
public interface ReuirementCancleCommonService {

    /**
     * 取消处理
     * @param requirementHeadIdList
     * @param requirementHeadNumMap
     * @return
     */
    public RequirementCancleContext cancleReuirement(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap);

    /**
     * 查询申请单未终止和未废弃的合同
     * @param requirementHeadNum
     * @return
     */
    public List<RecordDTO> queryReuiremnetAsWithContract(String requirementHeadNum);
}
