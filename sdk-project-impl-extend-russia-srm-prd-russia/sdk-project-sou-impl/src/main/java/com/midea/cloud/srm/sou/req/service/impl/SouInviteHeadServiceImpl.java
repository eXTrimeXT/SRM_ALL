package com.midea.cloud.srm.sou.req.service.impl;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.sou.enums.ExtCompanyStatusEmun;
import com.midea.cloud.srm.model.sou.enums.ServiceStatusEnum;
import com.midea.cloud.srm.model.sou.req.SouInviteHead;
import com.midea.cloud.srm.sou.req.mapper.SouInviteHeadMapper;
import com.midea.cloud.srm.sou.req.service.SouInviteHeadService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 邀请供应商头表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-13
 */
@Service
public class SouInviteHeadServiceImpl extends BaseServiceImpl<SouInviteHeadMapper, SouInviteHead> implements SouInviteHeadService {

    @Autowired
    private SouInviteHeadMapper inviteHeadMapper;


    @Override
    public PageInfo<SouInviteHead> listPage(SouInviteHead params) {
        /** 分页参数 */
        PageUtil.startPage(params.getPageNum(), params.getPageSize());
        /** 分页查询 */
        List<SouInviteHead> list = inviteHeadMapper.listPage(params);
        //代码拼接品类
        this.concatCategory(params, list);
        PageInfo<SouInviteHead> pageInfo = new PageInfo<>(list);

        return pageInfo;
    }

    /**
     * 代码拼接品类
     *
     * @param params
     * @param pageItemRelation
     */
    private void concatCategory(SouInviteHead params, List<SouInviteHead> pageItemRelation) {
        if (CollectionUtils.isNotEmpty(pageItemRelation)) {
            //构造查询参数
            List<Long> vendorList = pageItemRelation.stream().map(SouInviteHead::getVendorId).collect(Collectors.toList());
            Map<String, Object> map = new HashMap<>(15);
            map.put("isIr", params.getIsIr());
            map.put("vendorList", vendorList);
            map.put("categoryId", params.getCategoryId());

            //获取查询集合
            List<SouInviteHead> categoryList = inviteHeadMapper.listCategoryByVendors(map);

            Map<Long, List<SouInviteHead>> categoryMap = categoryList.stream().collect(Collectors.groupingBy(SouInviteHead::getVendorId));

            //转换成map
            Map<Long, String> resultMap = categoryList.stream()
                    .collect(Collectors.groupingBy(SouInviteHead::getVendorId, Collectors.mapping(SouInviteHead::getCategoryName, Collectors.joining(", "))));
            //遍历结果集，赋值
            pageItemRelation.forEach(souInviteHead -> {
                souInviteHead.setCategoryName(resultMap.get(souInviteHead.getVendorId()));

                AtomicReference<Boolean> categoryStatus = new AtomicReference<>(false);
                /** AND y.PJ_ORG_STATUS='Y' AND y.PJ_CATEGORY_STATUS='Y' AND y.SERVICE_STATUS IN('QUALIFIED', 'VERIFY')*/
                if(!Objects.isNull(params.getCategoryId())) {
                    categoryMap.getOrDefault(souInviteHead.getVendorId(), new ArrayList<>(16)).stream().forEach(category -> {
                        if(YesOrNo.YES.getValue().equals(category.getPjOrgStatus()) && YesOrNo.YES.getValue().equals(category.getPjCategoryStatus()) &&
                                Arrays.asList(ServiceStatusEnum.QUALIFIED.getCode(), ServiceStatusEnum.VERIFY.getCode()).contains(category.getServiceStatus())) {
                            categoryStatus.set(true);
                        }
                    });
                    if(YesOrNo.NO.getValue().equals(souInviteHead.getKeySupervisionFlag()) && YesOrNo.NO.getValue().equals(souInviteHead.getTimelimitflag())) {
                        if(Arrays.asList(ExtCompanyStatusEmun.OFFICIAL_SUPPLIER.name(), ExtCompanyStatusEmun.QUASI_SUPPLIER.name()).contains(souInviteHead.getPjCompanyStatus())) {
                            souInviteHead.setSmartRecomm(categoryStatus.get() ? YesOrNo.YES.getName() : YesOrNo.NO.getName());
                        }
                    }
                }
            });
        }
    }

}
