package com.midea.cloud.srm.sou.inq.ext.service;

import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqOrderItemHisQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqOrderItemHisQueryVO;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.order.ApiInqSouOrderDetailVO;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.order.ApiInqSouOrderItemVO;
import org.springframework.lang.Nullable;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtInqSouOrderQueryService {

    /**
     * 获取上一轮报价
     * @param projectId 参数
     * @param vendorId 参数
     * @return 返回
     */
    List<ApiInqSouOrderItemVO> getLastOrderItems(long projectId, long vendorId);

    /**
     * 查询指定轮次报价信息
     * @param projectId 参数
     * @param vendorId 参数
     * @param round 参数
     * @return 返回
     */
    ApiInqSouOrderDetailVO getSouOrderInfo(long projectId, long vendorId, @Nullable Integer round);

    /**
     * 供应商历史报价列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtInqOrderItemHisQueryVO> listVendorOrderHis(ExtInqOrderItemHisQueryDTO queryParam);

}
