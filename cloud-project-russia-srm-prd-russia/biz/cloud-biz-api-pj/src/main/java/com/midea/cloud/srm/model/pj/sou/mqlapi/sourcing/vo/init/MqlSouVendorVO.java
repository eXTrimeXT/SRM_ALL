package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init.MqlSouVendorDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorAuth;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 寻源MQL
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/02/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouVendorVO extends MqlSouVendorDTO {

    public static List<MqlSouVendorVO> convertMqlVO(List<SouVendor> vendorList,
                                                    List<SouVendorAuth> authList) {
        if (vendorList.isEmpty()) { return Collections.emptyList(); }
        List<MqlSouVendorVO> voList = new ArrayList<>(vendorList.size());

        Map<Long/* vendorId */, List<SouVendorAuth>> authMap = authList.stream()
                .collect(Collectors.groupingBy(SouVendorAuth::getVendorId));

        for (SouVendor vendor : vendorList) {
            MqlSouVendorVO vo = SouObjectXUtil.convertTargetObj(vendor, MqlSouVendorVO.class);
            voList.add(vo);

            vo.setAuthList(authMap.get(vendor.getVendorId()));
        }
        return voList;
    }

}
