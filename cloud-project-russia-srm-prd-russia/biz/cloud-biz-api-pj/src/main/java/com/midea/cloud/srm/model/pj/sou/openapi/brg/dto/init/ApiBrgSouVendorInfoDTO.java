package com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorInfoDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 项目式询价openAPI - 邀请供应商保存
 * PS: 参考 {@link ApiSouVendorInfoDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/22
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouVendorInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("供应商信息")
    private List<ApiSouVendorDTO> vendorList;
    @ApiModelProperty("true-暂存/false-提交")
    private boolean isTempSave = true;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(vendorList)) {
            throw new IllegalArgumentException("缺少vendorList数据");
        }
    }

}
