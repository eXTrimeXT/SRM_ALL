package com.midea.cloud.srm.biz.pj.sou.comp.select.service.impl;

import com.midea.cloud.srm.biz.pj.sou.comp.select.service.CompSouSelectQueryWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.service.SouSelectQueryService;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiCompSouOrderReportVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiCompSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectItemQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectResultVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouSelectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouPlaceOnFileDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouOrderReportVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSelectPlaceOnFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目式询价 - 评选查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/25
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouSelectQueryWebServiceImpl implements CompSouSelectQueryWebService {

    @Autowired
    private SouSelectQueryService souSelectQueryService;

    /**
     * 评选信息列表查询
     */
    @Override
    public List<ApiCompSouSelectQueryVO> listEvaluations(ApiSouSelectQueryDTO queryParam) {
        // 1: 查询核心数据
        List<ApiSouSelectQueryVO> souVOList = souSelectQueryService.listEvaluations(queryParam, SouTypeEnum.comp.name());
        // 2: 数据转化
        return SouObjectXUtil.convertList(souVOList, ApiCompSouSelectQueryVO.class);
    }

    /**
     * 获取报价报表信息
     */
    @Override
    public ApiCompSouOrderReportVO generatePriceReport(long projectId) {
        // 1: 查询数据
        ApiSouOrderReportVO souVO = souSelectQueryService.generatePriceReport(projectId, SouTypeEnum.comp.name());
        // 2: 返回数据
        return SouObjectXUtil.convertTargetObj(souVO, ApiCompSouOrderReportVO.class);
    }

    @Override
    public List<ApiSouSelectItemQueryVO> listItemEvaluations(ApiSouSelectQueryDTO queryParam) {
        // 1: 查询核心数据
        List<ApiSouSelectItemQueryVO> souVOList = souSelectQueryService.listItemEvaluations(queryParam, SouTypeEnum.comp.name());
        return souVOList;
    }

    @Override
    public List<ApiSouSelectResultVO> listOrderResult(ApiSouSelectQueryDTO queryParam) {
        // 1: 查询核心数据
        List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listOrderResult(queryParam, SouTypeEnum.comp.name());
        return souVOList;
    }

    @Override
    public List<SouPlaceOnFileDTO> listPlaceOnFile(ApiSouSelectQueryDTO queryParam) {
        // 1: 查询核心数据
        List<SouSelectPlaceOnFile> souVOList = souSelectQueryService.listPlaceOnFile(queryParam, SouTypeEnum.comp.name());
        // 2: 数据转化
        return SouObjectXUtil.convertList(souVOList, SouPlaceOnFileDTO.class);
    }

    @Override
    public List<ApiSouSelectResultVO> listWinNotice(ApiSouSelectQueryDTO queryParam) {
        // 1: 查询核心数据
        List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listWinNotice(queryParam, SouTypeEnum.comp.name());
        return souVOList;
    }
}
