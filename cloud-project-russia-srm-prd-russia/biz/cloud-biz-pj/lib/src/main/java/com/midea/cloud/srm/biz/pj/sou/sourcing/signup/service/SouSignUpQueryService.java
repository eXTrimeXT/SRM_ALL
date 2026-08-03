package com.midea.cloud.srm.biz.pj.sou.sourcing.signup.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpVendorVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;

import java.util.List;

/**
 * 项目式询价 - 报名查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/23
 */
public interface SouSignUpQueryService {

    /**
     * 查询供应商报名信息
     * @param queryParam 查询条件
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<ApiSouSignUpQueryVO> listVendorSignUp(ApiSouSignUpQueryDTO queryParam, String souType);

    /**
     * 查询供应商报名详情
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId 供应商ID
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApiSouSignUpVendorVO getVendorSignUpDetail(long projectId, long vendorId, String souType);

    /**
     * 备注
     * @param projectId
     * @return
     */
    List<SouFile> getSignOuter(long projectId);

}
