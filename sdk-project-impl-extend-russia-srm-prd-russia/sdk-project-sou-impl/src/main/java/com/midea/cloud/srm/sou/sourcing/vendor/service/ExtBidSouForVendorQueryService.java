package com.midea.cloud.srm.sou.sourcing.vendor.service;


import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.*;
import com.midea.cloud.srm.model.sou.sourcing.dto.MarginRecordVo;
import io.swagger.annotations.Api;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Api("寻源核心-供应商接口")
public interface ExtBidSouForVendorQueryService {

    /**
     * 查询退款记录
     * @param projectId
     * @param companyId
     * @return
     */
    List<MarginRecordVo>getSouMarginRecord(Long projectId, Long companyId);
    /**
     * 供应商分页查询接口
     * @param query 参数
     * @param souType 参数
     * @return 返回
     */
    PageInfo<ExtSouOrderDto> getPage(ApiExtSouProjectQueryDTO query, String souType);

    /**
     * 查询报价明细
     * @param orderId 参数
     * @param souType 参数
     * @return 返回
     */
    ApiExtSouOrderDetailDto getOrderDetail(Long orderId, String souType);

    /**
     * 商务投标或者投标
     * @param orderId 参数
     * @param souType 参数
     * @return 返回
     */
    ApiExtSouOrderDetailDto getTenderDetail(Long orderId, String souType);

    /**
     * 查询招标文件
     * @param projectId 参数
     * @param souType 参数
     * @return 返回
     */
    ApiExtSouBidFileDto getBidSouFileList(Long projectId, String souType);

    /**
     * 查询招标保证金
     * @param projectId 参数
     * @param vendorId 参数
     * @param souType 参数
     * @return 返回
     */
    ExtSouMarginDto getMargin(Long projectId, Long vendorId, String souType);

    /**
     * 查询招标结果
     * @param projectId 参数
     * @param souType 参数
     * @return 返回
     */
    ApiExtSouNoticeDto getBidNoticeDetail(Long projectId, String souType);

    /**
     * 查询招标电子签章数据
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    ApiExtSignDto getSign(ApiExtSouSignEditDto param, String souType);

    /**
     * 推送招标电子签章数据
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    String pushSgin(ApiExtSouSignEditDto param, String souType);
}
