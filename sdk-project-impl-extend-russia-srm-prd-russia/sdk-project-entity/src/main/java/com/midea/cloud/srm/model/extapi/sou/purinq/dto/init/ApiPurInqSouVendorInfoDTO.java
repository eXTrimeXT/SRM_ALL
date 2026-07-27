package com.midea.cloud.srm.model.extapi.sou.purinq.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiPurInqSouVendorInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    protected Long projectId;
    @ApiModelProperty("供应商信息")
    protected List<ApiPurInqSouVendorDTO> vendorList;
    /** @see SouTypeEnum */
    @SuppressWarnings({"AlibabaBooleanPropertyShouldNotStartWithIs", "AlibabaPojoMustUsePrimitiveField"})
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
