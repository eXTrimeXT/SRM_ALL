package com.midea.cloud.srm.sou.bid.quicksearchs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.sou.bid.quicksearchs.service.VendorQuickSearchService;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: panmq
 * @Date: 2024/04/03/ $
 * @Description: 供应商快查
 */
@Slf4j
@Service
public class VendorQuickSearchServiceImpl implements VendorQuickSearchService {
    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Override
    public PageInfo<ExtSouVendor> vendorQuickSearchForAnswer(Map<String, Object> params) {
        Long projectId = MapUtils.getLong(params, ExtSouBidComponent.fieldName(ExtSouVendor::getProjectId));
        String vendorCode = MapUtils.getString(params, ExtSouBidComponent.fieldName(ExtSouVendor::getVendorCode));
        String vendorName = MapUtils.getString(params, ExtSouBidComponent.fieldName(ExtSouVendor::getVendorName));

        if(ObjectUtils.allNotNull(projectId)) {
            List<ExtSouVendor> vendorList = vendorService.listVendorInfoAsShieldVendorName(projectService.getById(projectId));
            vendorList = vendorList.stream().filter(o -> {
                if(StringUtils.isNotBlank(vendorCode)) {
                    if(!o.getVendorCode().contains(vendorCode)) {
                        return false;
                    }
                }
                if(StringUtils.isNotBlank(vendorName)) {
                    if(!o.getVendorName().contains(vendorName)) {
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());
            return PageUtil.pagingByFullData(MapUtils.getInteger(params, ExtSouBidComponent.fieldName(ExtSouVendor::getPageNum), SrmConstant.NUM_ONE), MapUtils.getInteger(params, ExtSouBidComponent.fieldName(ExtSouVendor::getPageSize), SrmConstant.NUM_FIFTEEN), vendorList);
        } else {
            PageUtil.startPage(MapUtils.getInteger(params, ExtSouBidComponent.fieldName(ExtSouVendor::getPageNum), SrmConstant.NUM_ONE), MapUtils.getInteger(params, ExtSouBidComponent.fieldName(ExtSouVendor::getPageSize), SrmConstant.NUM_FIFTEEN));
            LambdaQueryWrapper<ExtSouVendor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StringUtils.isNotBlank(vendorCode), ExtSouVendor::getVendorCode, vendorCode);
            queryWrapper.eq(StringUtils.isNotBlank(vendorName), ExtSouVendor::getVendorName, vendorName);
            queryWrapper.groupBy(ExtSouVendor::getVendorId);

            List<ExtSouVendor> vendorList = vendorService.list(queryWrapper);
            return new PageInfo<>(vendorList);
        }
    }
}
