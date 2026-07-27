package com.midea.cloud.srm.biz.pj.sou.sourcing.tech.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechProgressDetailQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechProgressQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechProgressReviewQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;

import java.util.List;

/**
 * 寻源核心 - 技术标查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/26
 */
public interface SouTechQueryService {

    /**
     * 查询技术报价进度
     * @param queryParam 查询条件
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouTechProgressQueryVO queryTechProgress(ApiSouTechProgressQueryDTO queryParam, String souType);

    /**
     * 查询供应商技术标信息
     * @param orderId 报价单ID{@link SouOrder#getOrderId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouTechVendorOrderVO queryVendorTechOrder(long orderId, String souType);

    /**
     * 查询供应商技术评分信息
     * PS: 针对具体某个供应商，各评分人的评分进度
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId 供应商ID
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<ApiSouTechProgressGroupVO> queryTechProgressInfo(long projectId, long vendorId, String souType);

    /**
     * 查询评委的技术评分详情
     * PS: 针对具体某个供应商，查询某个评委的评分详情
     * @param queryParam 查询条件
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouTechProgressGroupDetailVO queryTechProgressInfoDetails(ApiSouTechProgressDetailQueryDTO queryParam, String souType);

    /**
     * 工作小组成员: 查询需要技术评分的寻源单信息
     * @param queryParam 查询条件
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<ApiSouTechProgressReviewVO> queryTechProgressReview(ApiSouTechProgressReviewQueryDTO queryParam, String souType);

    /**
     * 工作小组成员: 查询询价单技术评分详情
     * PS: 具体需要对哪些供应商进行技术评分
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param userId 当前用户ID
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouTechProgressReviewDetailVO queryTechProgressReviewDetail(long projectId, long userId, String souType);

}
