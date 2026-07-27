package com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init;

import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init.ApiBrgSouInitProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init.ApiBrgSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目式询价 - 立项详情信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouInitDetailVO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private ApiBrgSouInitProjectVO projectInfo;
    @ApiModelProperty("项目需求")
    private List<ApiBrgSouItemVO> requireInfo;
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
