package com.midea.cloud.srm.biz.pj.sou.comp.order.service;

import com.midea.cloud.srm.model.base.formula.vo.EssentialFactorVO;
import com.midea.cloud.srm.model.pj.sou.comp.dto.webapi.order.CompSouVendorViewOrderDetailQueryWebDTO;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order.CompSouOrderDetailWebVO;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order.CompSouOrderItemWebVO;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order.CompSouVendorViewOrderDetailsWebVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouInitProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderResultQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouOrderSignUpInfoVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 竞价 - 报价查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
public interface CompSouOrderQueryWebService {

    /**
     * 查询竞价列表
     * @param queryParam 参数
     * @return
     */
    List<ApiCompSouOrderQueryVO> listCompOrders(ApiSouOrderQueryDTO queryParam);

    /**
     * 查看项目信息【项目信息】
     * @param projectId id
     * @param vendorId id
     * @return
     */
    ApiCompSouInitProjectVO getCompProjectInfo(long projectId, long vendorId);

    /**
     * 查看项目信息【项目需求】
     * @param projectId 项目id
     * @param vendorId 供应商id
     * @return
     */
    List<ApiCompSouItemVO> getCompRequireInfo(long projectId, long vendorId);

    /**
     * 查看项目信息【报名信息】
     * @param projectId 项目id
     * @param vendorId 供应商id
     * @return
     */
    ApiSouOrderSignUpInfoVO getCompSignUpInfo(long projectId, long vendorId);

    /**
     * 查看项目信息【投标明细】
     * @param queryParam 请求参数
     * @param vendorId 供应商id
     * @return
     */
    CompSouVendorViewOrderDetailsWebVO getOrderDetails(CompSouVendorViewOrderDetailQueryWebDTO queryParam, long vendorId);

    /**
     * 查看结果
     * @param queryParam 请求参数
     * @param isBuyer 是否采购商
     * @return
     */
    List<CompSouOrderItemWebVO> listOrderResult(ApiSouOrderResultQueryDTO queryParam, boolean isBuyer);

    /**
     *  备注
     * 查询询价单详情(用于报价)
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId 供应商ID
     * @param isBuyer
     * @return
     */
    CompSouOrderDetailWebVO getCompSouOrderInfo(long projectId, long vendorId, boolean isBuyer);

    /**
     * 提供一个单独的查看公式报价的接口
     * PS: 因为有好几个页面需要查看公式报价
     *
     * @param souItemId    物料需求行ID
     * @param orderItemId  报价单行ID(可能为空，此时供应商还未填写阶梯报价)
     * @param currencyCode 基价转换的目标币种
     * @param vendorId 供应商id
     * @param isBuyer 是否采购商
     * @return
     */
    List<EssentialFactorVO> getOrderFormulaPrices(long souItemId, @Nullable Long orderItemId, String currencyCode, long vendorId, boolean isBuyer);

}
