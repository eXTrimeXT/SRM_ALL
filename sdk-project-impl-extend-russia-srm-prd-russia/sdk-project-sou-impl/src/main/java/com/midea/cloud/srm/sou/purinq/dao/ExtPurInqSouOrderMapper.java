package com.midea.cloud.srm.sou.purinq.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqOrderItemHisQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ApiPurInqSouOrderQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ExtPurInqOrderItemHisQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrder;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface ExtPurInqSouOrderMapper extends BaseMapper<ExtPurInqSouOrder> {

    /**
     * listVendorOrderHis
     * @param queryParam
     * @return
     */
    List<SouOrderItem> listVendorOrderHis(ExtPurInqOrderItemHisQueryDTO queryParam);

    /**
     * 供应商报价列表查询
     * @param queryParam
     * @param souType
     * @return
     */
    List<ApiSouOrderQueryVO> listOrders(@Param("queryParam") ApiPurInqSouOrderQueryDTO queryParam, @Param("souType") String souType);

}
