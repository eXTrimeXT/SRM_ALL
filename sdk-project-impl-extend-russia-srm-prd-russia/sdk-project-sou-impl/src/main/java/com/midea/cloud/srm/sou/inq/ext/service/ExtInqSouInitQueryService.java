package com.midea.cloud.srm.sou.inq.ext.service;

import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouItemQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendorDel;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouItemQueryVO;
import com.midea.cloud.srm.model.supplier.vendororgcategory.vo.AiRecommendCompanyInfoVO;

import java.util.List;

/**
 * 长城 - 询比价 - 立项 - 查询服务
 * @author huangbf3
 */
public interface ExtInqSouInitQueryService {

    /**
     * 供应商智能推荐
     * @param projectId 参数
     * @return 返回
     */
    List<AiRecommendCompanyInfoVO> getVendorAiRecommend(long projectId);

    /**
     * 查看历史最低价供应商
     * @param projectId 参数
     * @return 返回
     */
    List<AiRecommendCompanyInfoVO> getHistoryMinPriceVendors(long projectId);

    /**
     * 查看被删除的邀请供应商
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtPJInqSouVendorDel> queryVendorDel(ExtPjInqSouVendorQueryDTO queryParam);

    /**
     * 询比价物料明细报表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtInqSouItemQueryVO> querySouItems(ExtInqSouItemQueryDTO queryParam);

}
