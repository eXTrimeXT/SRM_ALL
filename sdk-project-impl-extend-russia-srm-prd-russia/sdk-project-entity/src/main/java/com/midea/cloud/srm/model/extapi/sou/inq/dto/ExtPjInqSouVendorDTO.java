package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendor;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPjInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPjInqSouVendorDTO extends SouVendor {

    /** @see ExtPJInqSouVendor#getSourceFromType */
    @ApiModelProperty("数据来源")
    private ExtPjInqSouVendorSourceFromTypeEnum sourceFromType;

    /** @see ExtPJInqSouVendor#getNewVendorTag */
    @ApiModelProperty("是否新供应商(该字段用于发起新一轮场景)")
    private Enable newVendorTag;

}
