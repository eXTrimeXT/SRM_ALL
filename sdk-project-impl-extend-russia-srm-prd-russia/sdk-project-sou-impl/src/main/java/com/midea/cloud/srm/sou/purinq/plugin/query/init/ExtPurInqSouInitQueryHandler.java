package com.midea.cloud.srm.sou.purinq.plugin.query.init;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ApiPurInqSouProjectQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouCurrency;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouVendor;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouInitDetailVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouInitProjectInfoVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouItemVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouVendorVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.sou.purinq.dao.*;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiSouInitQueryHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouInitQueryHandler extends ApiSouInitQueryHandler {

    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;
    @Autowired
    private ExtPurInqSouItemDAO extPurInqSouItemDAO;
    @Autowired
    private ExtPurInqSouCurrencyDAO extPurInqSouCurrencyDAO;
    @Autowired
    private ExtPurInqSouVendorDAO extPurInqSouVendorDAO;
    @Autowired
    private ExtPurInqSouProjectMapper extPurInqSouProjectMapper;

    @Override
    @ApiOperation("寻源分页查询")
    public List<SouProject> doHandlerForPageProjects(ApiSouProjectQueryDTO queryParam, String souType) {
        ApiPurInqSouProjectQueryDTO inqQueryParam = SouObjectXUtil.convertTargetObj(queryParam, ApiPurInqSouProjectQueryDTO.class);
        // 1: 查询数据
        if (inqQueryParam.getPageNum() != null && inqQueryParam.getPageSize() != null) {
            PageMethod.startPage(inqQueryParam.getPageNum(), inqQueryParam.getPageSize());
        }
        return extPurInqSouProjectMapper.listPurInqProjects(inqQueryParam);
    }

    @Override
    @ApiOperation("寻源分页查询的后置处理")
    public List<SouProject> doHandlerAfterPageProjects(ApiSouProjectQueryDTO queryParam, String souType, List<SouProject> souProjectList) {
        if (souProjectList.isEmpty()) { return souProjectList; }
        // 1: 查询额外询价信息
        Map<Long, ExtPurInqSouProject> inqProjectMap = extPurInqSouProjectDAO.listByIds(souProjectList.stream().map(SouProject::getProjectId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPurInqSouProject::getProjectId, Function.identity()));
        souProjectList.forEach(project -> SouObjectXUtil.mergeProperties(inqProjectMap.get(project.getProjectId()), project));

        return souProjectList;
    }

    @Override
    @ApiOperation("查询寻源基本信息的后置处理")
    public ApiSouInitProjectInfoVO doHandlerAfterGetProject(long projectId, String souType, ApiSouInitProjectInfoVO vo) {
        vo = super.doHandlerAfterGetProject(projectId, souType, vo);
        ApiPurInqSouInitProjectInfoVO inqVO = SouObjectXUtil.convertTargetObj(vo, ApiPurInqSouInitProjectInfoVO.class);
        // 1: 查询额外询比价基本信息
        ExtPurInqSouProject inqProject = extPurInqSouProjectDAO.getById(projectId);
        SouObjectXUtil.mergeProperties(inqProject, inqVO);
        // 2: 查询额外可用壁纸
        List<ExtPurInqSouCurrency> inqCurrencyList = extPurInqSouCurrencyDAO.list(ExtPurInqSouCurrency::getProjectId, projectId);
        inqVO.setCurrencyList(inqCurrencyList);

        return SouObjectXUtil.convertTargetObj(inqVO, ApiSouInitProjectInfoVO.class);
    }

    @Override
    @ApiOperation("查询寻源物料需求的后置处理")
    public List<ApiSouItemVO> doHandlerAfterListRequires(long projectId, String souType, List<ApiSouItemVO> voList) {
        voList = super.doHandlerAfterListRequires(projectId, souType, voList);
        if (voList.isEmpty()) { return voList; }
        Map<Long/* souItemId */, ExtPurInqSouItem> inqItemMap = extPurInqSouItemDAO.list(ExtPurInqSouItem::getProjectId, projectId)
                .stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        List<ApiPurInqSouItemVO> inqVOList = SouObjectXUtil.convertList(voList, ApiPurInqSouItemVO.class);
        for (ApiPurInqSouItemVO itemVO : inqVOList) {
            SouObjectXUtil.mergeProperties(inqItemMap.get(itemVO.getSouItemId()), itemVO);
        }

        return SouObjectXUtil.convertList(inqVOList, ApiSouItemVO.class);
    }

    @Override
    @ApiOperation("查询寻源邀请供应商的后置处理")
    public List<ApiSouVendorVO> doHandlerAfterListVendors(long projectId, String souType, List<ApiSouVendorVO> voList) {
        voList = super.doHandlerAfterListVendors(projectId, souType, voList);
        if (voList.isEmpty()) { return voList; }
        Map<Long/* souVendorId */, ExtPurInqSouVendor> inqVendorMap = extPurInqSouVendorDAO.list(ExtPurInqSouVendor::getProjectId, projectId)
                .stream().collect(Collectors.toMap(ExtPurInqSouVendor::getSouVendorId, Function.identity()));
        List<ApiPurInqSouVendorVO> inqVOList = SouObjectXUtil.convertList(voList, ApiPurInqSouVendorVO.class);
        for (ApiPurInqSouVendorVO vendorVO : inqVOList) {
            SouObjectXUtil.mergeProperties(inqVendorMap.get(vendorVO.getSouVendorId()), vendorVO);
        }

        return SouObjectXUtil.convertList(inqVOList, ApiSouVendorVO.class);
    }

    @Override
    @ApiOperation("查询指定邀请供应商的后置处理")
    public ApiSouVendorVO doHandlerAfterGetVendor(long projectId, long vendorId, String souType, ApiSouVendorVO vo) {
        vo = super.doHandlerAfterGetVendor(projectId, vendorId, souType, vo);
        //noinspection ConstantConditions
        if (vo == null) { return vo; }
        ExtPurInqSouVendor inqVendor = extPurInqSouVendorDAO.getById(vo.getSouVendorId());
        SouObjectXUtil.mergeProperties(inqVendor, vo);

        return vo;
    }

    @Override
    @ApiOperation("查询寻源立项所有信息的后置处理")
    public ApiSouInitDetailVO doHandlerAfterGetSouInitInfo(long projectId, String souType, ApiSouInitDetailVO vo) {
        vo = super.doHandlerAfterGetSouInitInfo(projectId, souType, vo);
        ApiPurInqSouInitDetailVO inqVO = SouObjectXUtil.convertTargetObj(vo, ApiPurInqSouInitDetailVO.class);
        // 1: 转化额外基本信息
        ExtPurInqSouProject inqProject = extPurInqSouProjectDAO.getById(projectId);
        SouObjectXUtil.mergeProperties(inqProject, inqVO.getProjectInfo());
        // 2: 转化可用币种
        List<ExtPurInqSouCurrency> currencyList = extPurInqSouCurrencyDAO.lambdaQuery()
                .eq(ExtPurInqSouCurrency::getProjectId, projectId)
                .orderByAsc(ExtPurInqSouCurrency::getSortIndex)
                .list();
        inqVO.getProjectInfo().setCurrencyList(currencyList);
        // 3: 转化物料需求
        Map<Long/* souItemId */, ExtPurInqSouItem> inqItemMap = extPurInqSouItemDAO.list(ExtPurInqSouItem::getProjectId, projectId)
                .stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        for (ApiPurInqSouItemVO itemVO : inqVO.getRequireInfo()) {
            SouObjectXUtil.mergeProperties(inqItemMap.get(itemVO.getSouItemId()), itemVO);
        }
        // 4: 转换供应商信息
        Map<Long/* souVendorId */, ExtPurInqSouVendor> inqVendorMap = extPurInqSouVendorDAO.list(ExtPurInqSouVendor::getProjectId, projectId)
                .stream().collect(Collectors.toMap(ExtPurInqSouVendor::getSouVendorId, Function.identity()));
        for (ApiPurInqSouVendorVO vendorVO : inqVO.getVendorInfo()) {
            SouObjectXUtil.mergeProperties(inqVendorMap.get(vendorVO.getSouVendorId()), vendorVO);
        }

        return SouObjectXUtil.convertTargetObj(inqVO, ApiSouInitDetailVO.class);
    }

    @Override
    public String matchModule() {
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
