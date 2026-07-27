package com.midea.cloud.srm.sou.inq.ext.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqOrderItemHisQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouItemQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouOrderQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouProjectQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouItemQueryVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 备注
 * @author huangbf3
 */
public interface ExtPjInqSouOrderMapper extends BaseMapper<ExtPjInqSouOrder> {
    /**
     * 备注
     * @param queryParam 参数
     * @return 返回
     */
    List<SouOrderItem> listVendorOrderHis(ExtInqOrderItemHisQueryDTO queryParam);

    /**
     * 立项列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<SouProject> querySouProjects(ExtInqSouProjectQueryDTO queryParam);

    /**
     * 供应商报价列表查询
     * @param queryParam 参数
     * @param souType 参数
     * @return 返回
     */
    List<ApiSouOrderQueryVO> listOrders(@Param("queryParam") ExtInqSouOrderQueryDTO queryParam, @Param("souType") String souType);

    /**
     * 询比价物料明细报表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtInqSouItemQueryVO> querySouItemsOrderCount(Map<String, Object> queryParam);

    /**
     * 询比价物料明细报表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtInqSouItemQueryVO> querySouItems(ExtInqSouItemQueryDTO queryParam);

}
