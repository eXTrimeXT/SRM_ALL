package com.midea.cloud.srm.biz.pj.sou.comp.select.service;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiCompSouOrderReportVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiCompSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectItemQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectResultVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouSelectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouPlaceOnFileDTO;

import java.util.List;

/**
 * 项目式询价 - 评选查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/25
 */
public interface CompSouSelectQueryWebService {

    /**
     * 评选信息列表查询
     * @param queryParam
     * @return
     */
    List<ApiCompSouSelectQueryVO> listEvaluations(ApiSouSelectQueryDTO queryParam);

    /**
     * 获取报价报表信息
     * @param projectId
     * @return
     */
    ApiCompSouOrderReportVO generatePriceReport(long projectId);

    /**
     * 备注
     * @param queryParam
     * @return
     */
    List<ApiSouSelectItemQueryVO> listItemEvaluations(ApiSouSelectQueryDTO queryParam);

    /**
     * 备注
     * @param queryParam
     * @return
     */
    List<ApiSouSelectResultVO> listOrderResult(ApiSouSelectQueryDTO queryParam);

    /**
     * 备注
     * @param queryParam
     * @return
     */
    List<SouPlaceOnFileDTO> listPlaceOnFile(ApiSouSelectQueryDTO queryParam);

    /**
     * 备注
     * @param queryParam
     * @return
     */
    List<ApiSouSelectResultVO> listWinNotice(ApiSouSelectQueryDTO queryParam);
}
