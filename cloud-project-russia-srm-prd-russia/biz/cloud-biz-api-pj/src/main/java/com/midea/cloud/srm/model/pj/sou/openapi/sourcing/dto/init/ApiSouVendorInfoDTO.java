package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 寻源openAPI - 邀请供应商
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/30
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouVendorInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    protected Long projectId;
    @ApiModelProperty("供应商信息")
    protected List<ApiSouVendorDTO> vendorList;
    /** @see SouTypeEnum */
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(vendorList)) {
            throw new IllegalArgumentException("缺少vendorList数据");
        }
    }

}
