package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.sou.common.ExtSouBidLecterUtils;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouVendorMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class IExtSouVendorServiceImpl extends ServiceImpl<ExtSouVendorMapper, ExtSouVendor> implements IExtSouVendorService {
    @Override
    public List<ExtSouVendor> listVendorInfoAsShieldVendorName(ExtSouProject souProject) {
        List<ExtSouVendor> vendorList = this.lambdaQuery().eq(ExtSouVendor::getProjectId, souProject.getProjectId())
                .orderByAsc(ExtSouVendor::getSouVendorId).list();
        if(YesOrNo.YES.getValue().equals(souProject.getExtHideKeyInfo())) {
            Boolean isBidPricipal = ObjectUtils.defaultIfNull(AppUserUtil.getUserName(), "").equals(souProject.getCreatedBy());
            shieldVendor(isBidPricipal, vendorList);
        }
        return vendorList;
    }

    /**
     * 屏蔽供应商名字
     * @param isBidPricipal
     * @param vendorList
     */
    private void shieldVendor(Boolean isBidPricipal, List<ExtSouVendor> vendorList) {
        final Integer[] index = {0};
        vendorList.stream().forEach(vendor -> {
            Integer curentIndex = index[0]++;
            if(isBidPricipal) {
                vendor.setVendorName(StringUtils.joinWith("", ExtSouBidLecterUtils.shieldVendorName(curentIndex), SrmConstant.PARENTHESES_LEFT, vendor.getVendorName(), SrmConstant.PARENTHESES_RIGHT));
                vendor.setVendorCode(StringUtils.joinWith("", ExtSouBidLecterUtils.shieldVendorName(curentIndex), SrmConstant.PARENTHESES_LEFT, vendor.getVendorCode(), SrmConstant.PARENTHESES_RIGHT));
            } else {
                vendor.setVendorName(ExtSouBidLecterUtils.shieldVendorName(curentIndex));
                vendor.setVendorCode(ExtSouBidLecterUtils.shieldVendorName(curentIndex));
            }
        });
    }

    @Override
    public Map<String, ExtSouVendor> listVendorInfoAsShieldVendorNameBatch(List<ExtSouProject> souProjectList) {
        Map<String, ExtSouVendor> vendorMap = new HashMap<>(16);
        if(CollectionUtils.isEmpty(souProjectList)) {
            return vendorMap;
        }
        List<ExtSouVendor> vendorList = this.lambdaQuery().in(ExtSouVendor::getProjectId, souProjectList.stream().map(ExtSouProject::getProjectId).collect(Collectors.toList()))
                .orderByAsc(ExtSouVendor::getSouVendorId).list();
        if(CollectionUtils.isEmpty(vendorList)) {
            return vendorMap;
        }

        Map<Long, ExtSouProject> projectMap = souProjectList.stream().collect(Collectors.toMap(k -> k.getProjectId(), Function.identity(), (k1, k2) -> k2));

        Map<Long, List<ExtSouVendor>> vendorGroup = vendorList.stream().collect(Collectors.groupingBy(ExtSouVendor::getProjectId));

        String userName = ObjectUtils.defaultIfNull(AppUserUtil.getUserName(), "");

        for(Long projectId : vendorGroup.keySet()) {
            List<ExtSouVendor> vendorSubList = vendorGroup.get(projectId);
            ExtSouProject souProject = projectMap.get(projectId);
            Boolean isBidPricipal = userName.equals(souProject.getCreatedBy());
            shieldVendor(isBidPricipal, vendorSubList);
            vendorSubList.stream().forEach(vendor -> {
                String vendorKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, vendor.getVendorId());
                vendorMap.put(vendorKey, vendor);
            });
        }

        return vendorMap;
    }
}
