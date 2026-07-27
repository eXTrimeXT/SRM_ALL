package com.midea.cloud.srm.biz.pj.sou.sourcing.select.service;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectItemQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectResultVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouSelectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouPlaceOnFileDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouOrderReportVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSelectPlaceOnFile;

import java.util.List;

/**
 * 寻源核心 - 评选服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/08
 */
public interface SouSelectQueryService {

    /**
     * 评选信息列表查询
     * @param queryParam
     * @param souType
     * @return
     */
    List<ApiSouSelectQueryVO> listEvaluations(ApiSouSelectQueryDTO queryParam, String souType);

    /**
     * 获取报价报表信息
     * @param projectId
     * @param souType
     * @return
     */
    ApiSouOrderReportVO generatePriceReport(long projectId, String souType);


    /**
     * 备注
     * @param queryParam
     * @param souType
     * @return
     */
    List<ApiSouSelectItemQueryVO> listItemEvaluations(ApiSouSelectQueryDTO queryParam, String souType);

    /**
     * 备注
     * @param queryParam
     * @param name
     * @return
     */
    List<ApiSouSelectResultVO> listOrderResult(ApiSouSelectQueryDTO queryParam, String name);

    /**
     * 备注
     * @param queryParam
     * @param name
     * @return
     */
    List<SouSelectPlaceOnFile> listPlaceOnFile(ApiSouSelectQueryDTO queryParam, String name);

    /**
     * 备注
     * @param queryParam
     * @param name
     * @return
     */
    List<ApiSouSelectResultVO> listWinNotice(ApiSouSelectQueryDTO queryParam, String name);
}
