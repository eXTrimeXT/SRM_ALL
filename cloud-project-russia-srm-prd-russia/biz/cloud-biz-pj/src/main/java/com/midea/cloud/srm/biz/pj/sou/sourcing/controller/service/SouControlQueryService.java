package com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.ApiSouItemRecordQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouControlVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouItemRecordLatestVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouItemRecordVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;

import java.util.List;

/**
 * 寻源核心 - 业务控制 - 查询服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/27
 */
public interface SouControlQueryService {

    /**
     * 获取报价管理信息
     * @param projectId 寻源单ID{@link SouOrder#getOrderId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouControlVO getControlInfo(long projectId, String souType);

    /**
     * 查询供应商报价详情
     * @param orderId 报价单ID{@link SouOrder#getOrderId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @param isBuyer true-采购商/false-供应商
     * @return
     */
    ApiSouOrderDetailVO getVendorOrderInfo(long orderId, boolean isBuyer, String souType);

    /**
     * 查询物料需求变更记录
     * @param queryParam 查询条件
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<ApiSouItemRecordVO> listSouItemRecords(ApiSouItemRecordQueryDTO queryParam, String souType);

    /**
     * 查询最新的物料变更记录
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouItemRecordLatestVO getLatestItemRecord(long projectId, String souType);

}
