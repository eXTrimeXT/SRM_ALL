package com.midea.cloud.srm.cm.contract.mapper;

import com.midea.cloud.srm.model.contract.dto.ContractPerPlanStartTimeDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 100014336
 */
public interface ExtPerPlanMapper  {

    /**
     * 埋点
     * @param statuses
     * @param remindDate
     * @return
     */
    List<ContractPerPlanStartTimeDto> queryContractPerPlanByPlanStartTime(@Param("statuses") List<String> statuses, @Param("remindDate") String remindDate);
}
