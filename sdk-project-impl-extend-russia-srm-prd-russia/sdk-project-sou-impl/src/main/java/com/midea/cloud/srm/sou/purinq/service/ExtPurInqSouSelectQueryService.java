package com.midea.cloud.srm.sou.purinq.service;

import com.midea.cloud.srm.model.extapi.sou.purinq.dto.select.ExtPurInqSouSelectQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.select.ExtPurInqSouSelectOrderItemVendorVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.select.ExtPurInqSouSelectQueryVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.select.ExtPurInqSouSelectionManagementVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author 100014337
 */
public interface ExtPurInqSouSelectQueryService {

    /**
     * 查询询比价管理界面信息
     * @param projectId
     * @return
     */
    ExtPurInqSouSelectionManagementVO getInqSelectManagementInfo(long projectId);

    /**
     * 评选列表信息查询
     * @param queryParam
     * @return
     */
    ExtPurInqSouSelectQueryVO queryItemSelectInfo(ExtPurInqSouSelectQueryDTO queryParam);

    /**
     * 查询评选物料轮次的供应商报价明细
     * @param souItemId
     * @param round
     * @return
     */
    List<ExtPurInqSouSelectOrderItemVendorVO> queryOrderItemVendors(long souItemId, int round);

    /**
     * 评选列表信息导出
     * @param queryParam
     * @param response
     * @throws IOException
     */
    void downLoadExcelForItemSelectInfo(ExtPurInqSouSelectQueryDTO queryParam, HttpServletResponse response) throws IOException;

}
