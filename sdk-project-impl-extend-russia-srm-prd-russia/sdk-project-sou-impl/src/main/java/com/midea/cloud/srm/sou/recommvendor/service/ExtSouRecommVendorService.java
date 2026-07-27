package com.midea.cloud.srm.sou.recommvendor.service;


import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import io.swagger.annotations.Api;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Api("寻源核心-推荐供应商")
public interface ExtSouRecommVendorService {


    /**
     * 保存推荐供应商
     *
     * @param param
     * @param b
     * @param name
     * @return
     */
    Long editRecommVendor(ApiExtSouRecommVendorInfoDTO param, boolean b, String name);

    /**
     * 查询推荐供应商
     *
     * @param projectId
     * @return
     */
    ApiExtSouRecommVendorInfoDTO getRecommVendorInfo(Long projectId);

    /**
     * 根据供应供应商单号查询已审批的推荐供应商
     *
     * @param recommVendorNo
     * @return
     */
    ApiExtSouRecommVendorInfoDTO getRecommVendorInfoByNo(String recommVendorNo);

    /**
     * 备注
     * @param recommVendorNo 参数
     * @return 返回
     */
    List<ExtSouVendor> getRecommVendorInfoByProjectId(Long recommVendorNo);

    /**
     * 备注
     * @param projectId 参数
     * @return 返回
     */
    String getApplicantNo(Long projectId);

    /**
     * 备注
     * @param appNo 参数
     * @return 返回
     */
    Long getApplicantByAppNo(String appNo);

}
