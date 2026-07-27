package com.midea.cloud.srm.sou.sourcing.spi.init.queryinvitesuppliers;

import com.midea.cloud.srm.model.sou.approve.entity.SouApproveUser;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.approve.service.ISouApproveUserService;
import com.midea.cloud.srm.sou.recommvendor.service.ExtSouRecommVendorService;
import com.midea.cloud.srm.sou.sourcing.fixstatus.service.SouFixedProjectStatusService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class ApiExtSouInitInviteSupplierQueryHandler implements ISouSpiBean {

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ExtSouRecommVendorService souRecommVendorService;

    @ApiOperation("邀请供应商查询的前置处理")
    public void doHandlerBeforeGetInviteSupplier(Long projectId, String souType) {

    }

    @ApiOperation("邀请供应商查询的后置处理")
    public List<ExtSouVendor> doHandlerAfterGetInviteSupplier(Long projectId, String souType, ApiExtSouProjectInfoDTO projectInfoDTO, List<ExtSouVendor> souVendorList) {
        ExtSouProject souProject = projectInfoDTO.getProject();
        List<ExtSouVendor> extSouVendorList = souVendorList;
        if (Arrays.asList(SouBiddingProStatusEnum.DRAW_UP.getCode()).contains(souProject.getProjectStatus()) && StringUtils.isNotBlank(souProject.getExtRecommendNo())) {
            Map<Long, ExtSouVendor> vendorMap = souVendorList.stream().collect(Collectors.toMap(v -> v.getVendorId(), Function.identity(), (k1, k2) -> k2));
            ApiExtSouRecommVendorInfoDTO vendorInfoDTO = souRecommVendorService.getRecommVendorInfoByNo(souProject.getExtRecommendNo());
            if (CollectionUtils.isNotEmpty(vendorInfoDTO.getSouVendor())) {
                for (ExtSouVendor extSouVendor : vendorInfoDTO.getSouVendor()) {
                    if (!vendorMap.containsKey(extSouVendor.getVendorId())) {
                        extSouVendorList.add(extSouVendor);
                    }
                }
            }
        }
        return extSouVendorList;
    }


    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
