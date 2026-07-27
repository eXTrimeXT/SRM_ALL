package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorAuthEditDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源openAPI - 执行追加供应商
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/24
 */
@Data
@ApiModel(description = "执行追加供应商")
@EqualsAndHashCode(callSuper = true)
public class ApiSouVendorAddDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("新增供应商的报价权限")
    private List<ApiSouVendorAuthEditDTO> authList;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
    }

}
