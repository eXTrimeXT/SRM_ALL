package com.midea.cloud.srm.sou.sourcing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouExpertRiskDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRisk;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtSouNpmExpertMapper extends BaseMapper {


    /**
     * 查询专家库
     * @param param
     * @return
     */
    List<ExtSouExpertRecord> queryExpertList(Map<String, Object> param);

    /**
     * 查询板块下的所有公司ID列表
     * @param orgBuId
     * @return
     */
    List<Long> queryOuIdListAsBuId(@Param("orgBuId") Long orgBuId);

    /**
     * 查询招标计划池招标技术负责人账号，用于专家抽取排除
     * @param requirementHeadNumList
     * @return
     */
    List<Long> queryTechUserIdAsRequirement(@Param("requirementHeadNumList") List<String> requirementHeadNumList);

    /**
     * 查询供应商风险数据
     * @param param
     * @return
     */
    List<ExtSouExpertRiskDto> queryExpertRisk(Map<String, Object> param);

}
