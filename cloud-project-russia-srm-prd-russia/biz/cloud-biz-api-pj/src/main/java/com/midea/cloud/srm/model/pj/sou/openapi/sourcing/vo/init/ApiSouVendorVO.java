package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init;

import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorAuth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 邀请供应商
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouVendorVO extends SouVendor {

    @ApiModelProperty("报价权限")
    private List<SouVendorAuth> authList;

    public static List<ApiSouVendorVO> convertApiVO(List<SouVendor> vendorList,
                                                    List<SouVendorAuth> authList) {
        if (vendorList.isEmpty()) { return Collections.emptyList(); }
        List<ApiSouVendorVO> voList = new ArrayList<>(vendorList.size());

        Map<Long/* vendorId */, List<SouVendorAuth>> authMap = authList.stream()
                .collect(Collectors.groupingBy(SouVendorAuth::getVendorId));

        for (SouVendor vendor : vendorList) {
            ApiSouVendorVO vo = SouObjectXUtil.convertTargetObj(vendor, ApiSouVendorVO.class);
            voList.add(vo);

            vo.setAuthList(authMap.get(vendor.getVendorId()));
        }
        return voList;
    }

}
