package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech;

import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechProgressDetailQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechProgressQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechProgressReviewQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 寻源openAPI - 技术标查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Service
public class ApiSouTechQueryHandler implements ISouSpiBean {

    @ApiOperation("查询技术评标进度后的额外处理")
    public ApiSouTechProgressQueryVO doHandlerAfterQueryTechProgress(ApiSouTechProgressQueryDTO queryParam, String souType, ApiSouTechProgressQueryVO vo) {
        return vo;
    }

    @ApiOperation("查询供应商技术标信息后的额外处理")
    public ApiSouTechVendorOrderVO doHandlerAfterQueryVendorTechOrder(long orderId, String souType, ApiSouTechVendorOrderVO vo) {
        return vo;
    }

    @ApiOperation("查询供应商技术评分信息后的额外处理")
    public List<ApiSouTechProgressGroupVO> doHandlerAfterQueryTechProgressInfo(long projectId, long vendorId, String souType, List<ApiSouTechProgressGroupVO> voList) {
        return voList;
    }

    @ApiOperation("查询评委的技术评分详情后的额外处理")
    public ApiSouTechProgressGroupDetailVO doHandlerAfterQueryTechProgressInfoDetails(ApiSouTechProgressDetailQueryDTO queryParam, String souType,
                                                                                      ApiSouTechProgressGroupDetailVO vo) {
        return vo;
    }

    @ApiOperation("查询需要技术评分的寻源单信息后的额外处理")
    public List<ApiSouTechProgressReviewVO> doHandlerAfterQueryTechProgressReview(ApiSouTechProgressReviewQueryDTO queryParam, String souType, List<ApiSouTechProgressReviewVO> voList) {
        return voList;
    }

    @ApiOperation("查询询价单技术评分详情后的额外处理")
    public ApiSouTechProgressReviewDetailVO doHandlerAfterQueryTechProgressReviewDetail(long projectId, long userId, String souType, ApiSouTechProgressReviewDetailVO vo) {
        return vo;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
