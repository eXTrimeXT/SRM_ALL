package com.midea.cloud.srm.biz.pj.sou.sourcing.order.service;

import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderCancelDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderWithdrawDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 寻源 - 供应商报价 - 业务事件服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/05
 */
public interface SouOrderEventService {

    /**
     * 暂存/提交报价
     * @param param 报价信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void editOrder(ApiSouOrderDTO param, String souType);

    /**
     * 备注
     * @param param
     */
    void initOrder(ApiSouOrderDTO param);

    /**
     * 撤回报价
     * @param param 撤回报价信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void withdrawOrder(ApiSouOrderWithdrawDTO param, String souType);

    /**
     *
     * 作废报价
     * @param param 参数
     * @param souType 类型
     */
    void cancelOrder(ApiSouOrderCancelDTO param, String souType);

    /**
     * 物料维度报价模板导入
     * @param projectId
     * @param vendorId
     * @param souItemId
     * @param round
     * @param isBuyer
     * @param souType
     * @param file
     */
    void importOrderItemQuoteTempExcel(long projectId, long vendorId, long souItemId, @Nullable Integer round, boolean isBuyer, String souType,
                                       MultipartFile file);

    /**
     * 报价模板数据计算
     * @param tempId {@link SouQuoteTemp#getTempId}
     * @param businessId 业务ID
     * @param tempData 模板数据
     * @param isBuyer true-采购商端/false-供应商端
     * @param vendorId 供应商ID(isBuyer=false时必填)
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    SouQuoteTempDataDetailVO computeQuoteTempData(long tempId, String businessId, Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> tempData,
                                                  boolean isBuyer, @Nullable Long vendorId, String souType);

}
