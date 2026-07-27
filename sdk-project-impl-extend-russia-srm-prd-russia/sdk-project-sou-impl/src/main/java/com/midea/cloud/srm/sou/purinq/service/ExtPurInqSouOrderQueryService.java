package com.midea.cloud.srm.sou.purinq.service;

import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ExtPurInqOrderItemHisQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderDetailVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderItemVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ExtPurInqOrderItemHisQueryVO;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface ExtPurInqSouOrderQueryService {

    /**
     * 获取上一轮报价
     * @param projectId
     * @param vendorId
     * @return
     */
    List<ApiPurInqSouOrderItemVO> getLastOrderItems(long projectId, long vendorId);

    /**
     * 查询指定轮次报价信息
     * @param projectId
     * @param vendorId
     * @param round
     * @return
     */
    ApiPurInqSouOrderDetailVO getSouOrderInfo(long projectId, long vendorId, @Nullable Integer round);

    /**
     * 供应商历史报价列表查询
     * @param queryParam
     * @return
     */
    List<ExtPurInqOrderItemHisQueryVO> listVendorOrderHis(ExtPurInqOrderItemHisQueryDTO queryParam);

    /**
     * 下载集采报价须知附件
     * @throws IOException
     */
    void getPurOrderNoticeFile() throws IOException;

}
