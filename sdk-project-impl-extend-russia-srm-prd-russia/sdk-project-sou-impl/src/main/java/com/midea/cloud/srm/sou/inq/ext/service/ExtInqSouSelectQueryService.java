package com.midea.cloud.srm.sou.inq.ext.service;

import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouSelectQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouSelectOrderItemVendorVO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouSelectQueryVO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouSelectionManagementVO;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtInqSouSelectQueryService {

    /**
     * 查询询比价管理界面信息
     * @param projectId 参数
     * @return 返回
     */
    ExtInqSouSelectionManagementVO getInqSelectManagementInfo(long projectId);

    /**
     * 评选列表信息查询
     * @param queryParam 参数
     * @return 返回
     */
    ExtInqSouSelectQueryVO queryItemSelectInfo(ExtInqSouSelectQueryDTO queryParam);

    /**
     * 查询评选物料轮次的供应商报价明细
     * @param souItemId 参数
     * @param round 参数
     * @return 返回
     */
    List<ExtInqSouSelectOrderItemVendorVO> queryOrderItemVendors(long souItemId, int round);

    /**
     * 评选列表信息导出
     * @param queryParam 参数
     * @param response
     * @throws IOException 报错
     */
    void downLoadExcelForItemSelectInfo(ExtInqSouSelectQueryDTO queryParam, HttpServletResponse response) throws IOException;

}
