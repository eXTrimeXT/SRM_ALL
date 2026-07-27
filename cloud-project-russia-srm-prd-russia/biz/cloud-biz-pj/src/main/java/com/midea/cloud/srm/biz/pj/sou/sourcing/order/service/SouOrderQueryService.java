package com.midea.cloud.srm.biz.pj.sou.sourcing.order.service;

import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemQuoteTempDownloadDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderResultQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouOrderSignUpInfoVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 寻源 - 供应商报价 - 查询服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/20
 */
public interface SouOrderQueryService {

    /**
     * 查询供应商报价单列表信息
     * @param queryParam 查询条件
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<ApiSouOrderQueryVO> listOrders(ApiSouOrderQueryDTO queryParam, String souType);

    /**
     * 查看项目信息
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId 供应商ID
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouInitProjectInfoVO getProjectInfo(long projectId, long vendorId, String souType);

    /**
     * 查看项目需求
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId 供应商ID
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<ApiSouItemVO> getRequireInfo(long projectId, long vendorId, String souType);

    /**
     * 查看报名信息
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId 供应商ID
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouOrderSignUpInfoVO getSignUpInfo(long projectId, long vendorId, String souType);

    /**
     * 供应商报价结果查询
     * @param queryParam 查询条件
     * @param isBuyer true-采购商/false-供应商
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<ApiSouOrderItemVO> listOrderResult(ApiSouOrderResultQueryDTO queryParam, boolean isBuyer, String souType);

    /**
     * 查看供应商报价详情
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId 供应商ID
     * @param round 轮次
     * @param isBuyer true-采购商/false-供应商
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouOrderDetailVO getSouOrderInfo(long projectId, long vendorId, @Nullable Integer round, boolean isBuyer, String souType);

    /**
     * 物料需求维度报价模板导出excel文件
     * @param param
     * @param isBuyer
     */
    void downloadOrderItemQuoteTempExcel(ApiSouOrderItemQuoteTempDownloadDTO param, boolean isBuyer);

    /**
     * 查询供应商的料费分离报价数据
     * PS: 本质上可以直接通过 {@link SouQuoteTempForVendorController#getTempOrderInfo} 接口来查询即可，
     *     但是考虑到那个接口比较通用，难以预防供应商端获取其他供应商的报价信息，因此需要在这里单独做一次前置的校验处理
     * @param tempId {@link SouQuoteTemp#getTempId}
     * @param businessId 业务ID
     * @param isBuyer true-采购商端/false-供应商端
     * @param vendorId 供应商ID(isBuyer=false时必填)
     * @param searchLatestData true-如果供应商当前轮次没有报价，则自动寻找供应商上一轮的报价
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return 报价模板数据
     */
    SouQuoteTempDataVO getQuoteTempOrderInfo(long tempId, String businessId, boolean isBuyer, @Nullable Long vendorId, boolean searchLatestData, String souType);

}
