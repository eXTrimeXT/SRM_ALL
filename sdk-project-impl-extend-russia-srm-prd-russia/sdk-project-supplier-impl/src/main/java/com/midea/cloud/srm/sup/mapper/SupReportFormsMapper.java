package com.midea.cloud.srm.sup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.sup.dto.SupReportFormsDto;
import com.midea.cloud.srm.sup.dto.SupReportFormsInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Mapper
public interface SupReportFormsMapper extends BaseMapper<CompanyInfo>{

    /**
     * getSupRfList
     * @param pa
     * @return
     */
    List<SupReportFormsDto> getSupRfList(@Param("pa") SupReportFormsDto pa);

    /**
     * supReportFormsInfoList
     * @param pa
     * @return
     */
    List<SupReportFormsInfoDto> supReportFormsInfoList(@Param("pa") SupReportFormsInfoDto pa);

    /**
     * projectScoreHeaderList
     * @param supId
     * @return
     */
    List<SupReportFormsInfoDto> projectScoreHeaderList(@Param("supId") Long supId);

    /**
     * getContractHeadList
     * @param supId
     * @param projectNo
     * @return
     */
    List<String> getContractHeadList(@Param("supId") Long supId, @Param("projectNo") String projectNo);

    /**
     * getSupList
     * @param pa
     * @return
     */
    Set<Long> getSupList(@Param("pa") SupReportFormsDto pa);
}
