package com.midea.cloud.srm.biz.pj.sou.comp.init.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.midea.cloud.srm.biz.pj.sou.comp.init.service.CompSouInitQueryWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitQueryService;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouInitProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 竞价 - 立项查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouInitQueryWebServiceImpl implements CompSouInitQueryWebService {

    @Autowired
    private SouInitQueryService souInitQueryService;

    /**
     * 列表查询
     */
    @Override
    public List<ApiCompSouProjectVO> listProject(ApiSouProjectQueryDTO queryParam) {
        // 1: 查询数据
        List<SouProject> projectList = souInitQueryService.listProjects(queryParam, SouTypeEnum.comp.name());
        // 2: 数据转化
        return ApiCompSouProjectVO.convertCompVO(projectList);
    }

    /**
     * 查询项目信息
     */
    @Override
    public ApiCompSouInitProjectVO getProjectInfo(long projectId) {
        // 1: 查询数据
        ApiSouInitProjectInfoVO souVO = souInitQueryService.getProject(projectId, SouTypeEnum.comp.name());
        // 2: 数据转化
        return SouObjectXUtil.convertTargetObj(souVO, ApiCompSouInitProjectVO.class);
    }

    /**
     * 查询物料需求
     */
    @Override
    public List<ApiCompSouItemVO> getRequireInfo(long projectId) {
        // 1: 查询数据
        List<ApiSouItemVO> itemList = souInitQueryService.listRequires(projectId, SouTypeEnum.comp.name());
        // 2: 数据转化
        return SouObjectXUtil.convertTargetObj(itemList, new TypeReference<List<ApiCompSouItemVO>>() {
        });
    }

    /**
     * 查询邀请供应商信息
     */
    @Override
    public List<ApiSouVendorVO> getInviteSupplier(long projectId) {
        // 1: 查询数据
        List<ApiSouVendorVO> vendorList = souInitQueryService.listVendors(projectId, SouTypeEnum.comp.name());
        // 2: 数据转化
        return SouObjectXUtil.convertTargetObj(vendorList, new TypeReference<List<ApiSouVendorVO>>() {
        });
    }

}
