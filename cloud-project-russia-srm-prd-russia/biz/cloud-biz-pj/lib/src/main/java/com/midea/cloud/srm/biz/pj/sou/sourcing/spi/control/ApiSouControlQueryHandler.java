package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control;

import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.ApiSouItemRecordQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouControlVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouItemRecordLatestVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouItemRecordVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 寻源openAPI - 流程控制查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/01
 */
@Service
public class ApiSouControlQueryHandler implements ISouSpiBean {

    @ApiOperation("查询报价管理信息后的额外处理")
    public ApiSouControlVO doHandlerAfterGetControlInfo(long projectId, String souType, ApiSouControlVO vo) {
        return vo;
    }

    @ApiOperation("查询报价单详情后的额外处理")
    public ApiSouOrderDetailVO doHandlerAfterGetVendorOrderInfo(long orderId, String souType, ApiSouOrderDetailVO vo) {
        return vo;
    }

    @ApiOperation("查询物料需求变更记录后的额外处理")
    public List<ApiSouItemRecordVO> doHandlerAfterListSouItemRecords(ApiSouItemRecordQueryDTO queryParam, String souType, List<ApiSouItemRecordVO> voList) {
        return voList;
    }

    @ApiOperation("查询最新的物料变更记录后的额外处理")
    public ApiSouItemRecordLatestVO doHandlerAfterGetLatestItemRecord(long projectId, String souType, ApiSouItemRecordLatestVO vo) {
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
