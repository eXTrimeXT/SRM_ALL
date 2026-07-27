package com.midea.cloud.srm.sou.inq.ext.plugin.query.init;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouProjectQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendor;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.init.ApiInqSouItemVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPJInqSouVendorDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouOrderMapper;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouProjectDAO;
import com.midea.cloud.srm.sou.inq.spi.init.InqSouInitQueryHandler;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouInitQueryHandler extends InqSouInitQueryHandler {

    @Autowired
    private ExtPJInqSouVendorDAO extPjInqSouVendorDao;
    @Autowired
    private ExtPjInqSouOrderMapper extPjInqSouOrderMapper;
    @Autowired
    private InqSouProjectDAO inqSouProjectDAO;

    @Override
    @ApiOperation("寻源分页查询")
    public List<SouProject> doHandlerForPageProjects(ApiSouProjectQueryDTO queryParam, String souType) {
        ExtInqSouProjectQueryDTO inqQueryParam = SouObjectXUtil.convertTargetObj(queryParam, ExtInqSouProjectQueryDTO.class);
        // 1: 查询数据
        if (inqQueryParam.getPageNum() != null && inqQueryParam.getPageSize() != null) {
            PageMethod.startPage(inqQueryParam.getPageNum(), inqQueryParam.getPageSize());
        }
        List<SouProject> projectList = extPjInqSouOrderMapper.querySouProjects(inqQueryParam);
        if (!projectList.isEmpty()) {
            Map<Long/* projectId */, InqSouProject> inqProjectMap = inqSouProjectDAO.listByIds(projectList.stream().map(SouProject::getProjectId).collect(Collectors.toSet()))
                    .stream().collect(Collectors.toMap(InqSouProject::getProjectId, Function.identity()));
            projectList.forEach(project -> SouObjectXUtil.mergeProperties(inqProjectMap.get(project.getProjectId()), project));
        }
        return projectList;
    }

    @Override
    @ApiOperation("查询寻源物料需求的后置处理")
    public List<ApiSouItemVO> doHandlerAfterListRequires(long projectId, String souType, List<ApiSouItemVO> voList) {
        if (CollectionUtils.isEmpty(voList)) { return voList; }
        List<ApiInqSouItemVO> inqVOList = SouObjectXUtil.convertList(voList, ApiInqSouItemVO.class);
        inqVOList.removeIf(e -> Enable.Y.equals(e.getHasClose()));

        return SouObjectXUtil.convertList(inqVOList, ApiSouItemVO.class);
    }

    @Override
    @ApiOperation("查询寻源邀请供应商的后置处理")
    public List<ApiSouVendorVO> doHandlerAfterListVendors(long projectId, String souType, List<ApiSouVendorVO> voList) {
        if (CollectionUtils.isNotEmpty(voList)) {
            Set<Long> souVendorIds = voList.stream().map(ApiSouVendorVO::getSouVendorId).collect(Collectors.toSet());
            if (!souVendorIds.isEmpty()) {
                Map<Long/* souVendorId */, ExtPJInqSouVendor> pjInqVendorMap = extPjInqSouVendorDao.listByIds(souVendorIds)
                        .stream().collect(Collectors.toMap(ExtPJInqSouVendor::getSouVendorId, Function.identity()));
                voList.forEach(vo -> SouObjectXUtil.mergeProperties(pjInqVendorMap.get(vo.getSouVendorId()), vo));
            }
        }

        return voList;
    }

    @Override
    @ApiOperation("查询指定邀请供应商的后置处理")
    public ApiSouVendorVO doHandlerAfterGetVendor(long projectId, long vendorId, String souType, ApiSouVendorVO vo) {
        if (vo != null) {
            ExtPJInqSouVendor pjInqVendor = extPjInqSouVendorDao.getById(vo.getSouVendorId());
            SouObjectXUtil.mergeProperties(pjInqVendor, vo);
        }
        return vo;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
