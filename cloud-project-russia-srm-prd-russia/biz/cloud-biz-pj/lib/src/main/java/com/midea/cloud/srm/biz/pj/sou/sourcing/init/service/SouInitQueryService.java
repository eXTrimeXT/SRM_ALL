package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 寻源 - 立项 - 信息查询服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/18
 */
public interface SouInitQueryService {

    /**
     * 查询寻源单集合
     * @param queryParam 查询条件
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<SouProject> listProjects(ApiSouProjectQueryDTO queryParam, String souType);

    /**
     * 查询寻源基本信息
     * @param projectId {@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouInitProjectInfoVO getProject(long projectId, String souType);

    /**
     * 查询寻源物料需求信息
     * @param projectId {@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<ApiSouItemVO> listRequires(long projectId, String souType);

    /**
     * 查询邀请供应商列表信息
     * @param projectId {@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<ApiSouVendorVO> listVendors(long projectId, String souType);

    /**
     * 查询指定供应商信息
     * @param projectId {@link SouProject#getProjectId}
     * @param vendorId 供应商ID
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    @Nullable
    ApiSouVendorVO getVendor(long projectId, long vendorId, String souType);

    /**x
     * 查询立项的所有信息
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouInitDetailVO getSouInitInfo(long projectId, String souType);

}
