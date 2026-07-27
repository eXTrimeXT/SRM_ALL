package com.midea.cloud.srm.sou.meiql.recommvendor.service;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CompanyAQCApiDTO;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorRiskDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

/**
 * 供应商推荐：查看风险
 * @author huangbf3
 */
public interface SouRecommvendorRiskService {

    /**
     * 查看供应商风险
     * @param param
     * @return
     */
    RecommvendorRiskDto vendorRisk(RecommvendorProjectDto param);

    /**
     * 查看供应商风险-新接口
     * @param param
     * @return
     */
    RecommvendorRiskDto vendorRiskNew(RecommvendorProjectDto param);

    /**
     * 查询供应商主数据
     * @param recommvendorDtoList 参数
     * @return 返回
     */
    @ApiOperation("查询供应商主数据")
    Map<Long, RecordDTO> queryCompanyInfo(List<RecommvendorDto> recommvendorDtoList);

    /**
     * 大数据爬虫接口
     * @param recommvendorDtoList 参数
     * @param ignoreGscp 参数
     * @param dictItems 参数
     * @return 返回
     */
    @ApiOperation("大数据爬虫接口")
    Map<String, CompanyAQCApiDTO> crawler(List<RecommvendorDto> recommvendorDtoList, Boolean ignoreGscp, List<DictItem> dictItems);

    /**
     * 大数据爬虫接口
     * @param recommvendorDtoList 参数
     * @param aqcApiFlag 参数
     * @param redisFlag 参数
     * @return 返回
     */
    @ApiOperation("大数据爬虫接口")
    Map<String, CompanyAQCApiDTO> queryCompanyAqcApi(List<RecommvendorDto> recommvendorDtoList, String aqcApiFlag, String redisFlag);

    /**
     * 查询GSCP接口
     * @param companyId 参数
     * @param gscpFlag 参数
     * @param redisFlag 参数
     * @return 返回
     */
    @ApiOperation("查询GSCP接口")
    String queryGscp(Long companyId, String gscpFlag, String redisFlag);

    /**
     * 还原招标计划需求池已创建供应商推荐单的状态
     * @param recommvendorProjectDtoList
     */
    void rollbackPlanPool(List<RecommvendorProjectDto> recommvendorProjectDtoList);

    /**
     * 还原招标计划需求池已创建标书的状态
     * @param projectList
     */
    void rollbackPlanPoolForBid(List<ExtSouProject> projectList);

    /**
     * 更新-寻源需求包名成功的供应商
     * @param projectId
     * @return
     */
    Long vendorUpdateAsSouReq(Long projectId);

    /**
     * 更新-标前交流反馈成功的供应商
     * @param projectId
     * @return
     */
    Long vendorUpdateAsPreBid(Long projectId);
}
