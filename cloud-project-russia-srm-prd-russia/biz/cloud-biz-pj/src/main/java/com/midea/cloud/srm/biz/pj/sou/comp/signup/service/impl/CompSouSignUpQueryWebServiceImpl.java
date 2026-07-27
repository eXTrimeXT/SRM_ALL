package com.midea.cloud.srm.biz.pj.sou.comp.signup.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.biz.pj.sou.comp.signup.service.CompSouSignUpQueryWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.signup.service.SouSignUpQueryService;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpVendorVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 竞价 - 报名信息查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouSignUpQueryWebServiceImpl implements CompSouSignUpQueryWebService {

    @Autowired
    private SouSignUpQueryService souSignUpQueryService;

    /**
     * 查询供应商报名信息
     */
    @Override
    public PageInfo<ApiSouSignUpQueryVO> listVendorSignUp(ApiSouSignUpQueryDTO queryParam) {
        // 1: 查询数据
        List<ApiSouSignUpQueryVO> voList = souSignUpQueryService.listVendorSignUp(queryParam, SouTypeEnum.comp.name());
        PageInfo<ApiSouSignUpQueryVO> pageInfo = new PageInfo(voList);
        // 2: 数据转化
        voList = SouObjectXUtil.convertTargetObj(voList, new TypeReference<List<ApiSouSignUpQueryVO>>() {
        });
        pageInfo.setList(voList);
        return pageInfo;
    }

    /**
     * 查询供应商报名附件信息
     */
    @Override
    public ApiSouSignUpVendorVO getVendorSignUpDetail(long projectId, long vendorId) {
        // 1: 查询数据
        ApiSouSignUpVendorVO souVO = souSignUpQueryService.getVendorSignUpDetail(projectId, vendorId, SouTypeEnum.comp.name());
        // 2: 数据转化
        return SouObjectXUtil.convertTargetObj(souVO, ApiSouSignUpVendorVO.class);
    }

    @Override
    public List<SouFile> getSignOuter(long projectId) {
        return souSignUpQueryService.getSignOuter(projectId);
    }

}
