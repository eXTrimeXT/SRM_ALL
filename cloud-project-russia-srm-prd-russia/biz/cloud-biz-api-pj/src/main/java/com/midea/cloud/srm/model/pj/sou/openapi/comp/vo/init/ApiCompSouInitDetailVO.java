package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouInitProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 竞价openAPI - 立项详情信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouInitDetailVO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private ApiCompSouInitProjectVO projectInfo;
    @ApiModelProperty("项目需求")
    private List<ApiCompSouItemVO> requireInfo;
    @ApiModelProperty("邀请供应商")
    private List<ApiSouVendorVO> vendorInfo;

    public void doVendorView(@Nullable Long vendorId) {
        projectInfo.doVendorView();
        if (vendorId == null) {
            vendorInfo = null;
        } else {
            this.vendorInfo = this.vendorInfo.stream().filter(v -> v.getVendorId().equals(vendorId)).collect(Collectors.toList());
        }
    }

}
