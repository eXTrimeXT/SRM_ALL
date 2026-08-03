package com.midea.cloud.srm.biz.pj.sou.comp.signup.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpVendorVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;

import java.util.List;

/**
 * 项目式询价 - 报名信息查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/23
 */
public interface CompSouSignUpQueryWebService {

    /**
     * 查询供应商报名信息
     * @param queryParam
     * @return
     */
    PageInfo<ApiSouSignUpQueryVO> listVendorSignUp(ApiSouSignUpQueryDTO queryParam);

    /**
     * 查询供应商报名详情
     * @param projectId
     * @param vendorId
     * @return
     */
    ApiSouSignUpVendorVO getVendorSignUpDetail(long projectId, long vendorId);

    /**
     * 备注
     * @param projectId
     * @return
     */
    List<SouFile> getSignOuter(long projectId);

}
