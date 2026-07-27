package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.signup;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MQL - 报名列表信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouSignUpQueryVO extends SouVendor {

    @SuppressWarnings("rawtypes")
    public static List<MqlSouSignUpQueryVO> convertMqlVO(List<SouVendor> vendorList) {
        if (vendorList.isEmpty()) { return Collections.emptyList(); }
        List<MqlSouSignUpQueryVO> voList;
        if (vendorList instanceof Page) {
            voList = new Page<>();
            ((Page)voList).setTotal(((Page)vendorList).getTotal());
            ((Page)voList).setPageNum(((Page)vendorList).getPageNum());
            ((Page)voList).setPageSize(((Page)vendorList).getPageSize());
        } else {
            voList = new ArrayList<>(vendorList.size());
        }
        vendorList.forEach(vendor -> voList.add(SouObjectXUtil.convertTargetObj(vendor, MqlSouSignUpQueryVO.class)));
        return voList;
    }

}
