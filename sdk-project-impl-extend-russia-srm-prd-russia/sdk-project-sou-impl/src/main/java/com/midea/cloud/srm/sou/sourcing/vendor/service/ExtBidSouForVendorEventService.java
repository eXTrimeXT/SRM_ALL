package com.midea.cloud.srm.sou.sourcing.vendor.service;


import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderDetailDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import io.swagger.annotations.Api;
/**
 * 备注
 * @author huangbf3
 */
@Api("寻源核心-供应商接口")
public interface ExtBidSouForVendorEventService {

    /**
     * 确认投标
     *
     * @param param
     * @param souType
     * @return
     */
    public Long confirmTender(ExtSouOrderDto param, String souType);

    /**
     * 撤回投标
     *
     * @param param
     * @param souType
     * @return
     */
    public Long withdrawTender(ExtSouOrderDto param, String souType);

    /**
     * 缴纳保证金
     *
     * @param param
     * @param souType
     * @return
     */
    public Long editOrderMargin(ExtSouMarginDto param, String souType);

    /**
     * 暂存或提交报价
     *
     * @param extSouOrderDetailDto
     * @param souType
     * @return
     */
    public Long editOrderItem(ApiExtSouOrderDetailDto extSouOrderDetailDto, String souType);

    /**
     * 更新标书下载时间
     * @param projectId
     * @return
     */
    public Long updateBidFileDownloadTime(Long projectId);

}
