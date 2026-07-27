package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 寻源OpenAPI - 追加供应商记录信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/24
 */
@Data
@ApiModel(description = "追加供应商记录信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouVendorRecordDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("新增供应商信息")
    private List<ApiSouVendorDTO> vendorList;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(vendorList)) {
            throw new IllegalArgumentException("缺少新增供应商信息");
        }
    }

}
