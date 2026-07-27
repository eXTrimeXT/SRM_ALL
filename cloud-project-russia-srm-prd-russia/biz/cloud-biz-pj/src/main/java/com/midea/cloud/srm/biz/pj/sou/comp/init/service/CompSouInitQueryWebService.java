package com.midea.cloud.srm.biz.pj.sou.comp.init.service;


import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouInitProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;

import java.util.List;

/**
 * 竞价 - 立项查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
public interface CompSouInitQueryWebService {

    /**
     * 列表查询
     * @param queryParam 参数
     * @return
     */
    List<ApiCompSouProjectVO> listProject(ApiSouProjectQueryDTO queryParam);

    /**
     * 查询项目信息
     * @param projectId id
     * @return
     */
    ApiCompSouInitProjectVO getProjectInfo(long projectId);

    /**
     * 查询物料需求
     * @param projectId id
     * @return
     */
    List<ApiCompSouItemVO> getRequireInfo(long projectId);

    /**
     * 查询邀请供应商信息
     * @param projectId id
     * @return
     */
    List<ApiSouVendorVO> getInviteSupplier(long projectId);

}
