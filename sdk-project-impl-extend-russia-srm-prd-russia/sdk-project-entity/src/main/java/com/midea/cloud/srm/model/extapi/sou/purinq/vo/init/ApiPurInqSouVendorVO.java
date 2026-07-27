package com.midea.cloud.srm.model.extapi.sou.purinq.vo.init;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouVendor;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiPurInqSouVendorVO extends ApiSouVendorVO {

    /** @see ExtPurInqSouVendor#getSourceFromType */
    @ApiModelProperty("数据来源")
    private ExtPurInqSouVendorSourceFromTypeEnum sourceFromType;

    /** @see ExtPurInqSouVendor#getNewVendorTag */
    @ApiModelProperty("是否新供应商(该字段用于发起新一轮场景)")
    private Enable newVendorTag;

}
