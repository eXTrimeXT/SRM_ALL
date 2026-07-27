package com.midea.cloud.srm.supcooperate.order.mapper;

import com.midea.cloud.srm.model.sou.designplans.dto.PullQueryDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Mapper
public interface CooDesignPlanOrderMapper {
    /**
     * 备注
     * @param pullQuery 参数
     * @return 返回
     */
    List<SccSouChDemandYearData> getOrderByParam(@Param("pullQuery") PullQueryDto pullQuery);
}
