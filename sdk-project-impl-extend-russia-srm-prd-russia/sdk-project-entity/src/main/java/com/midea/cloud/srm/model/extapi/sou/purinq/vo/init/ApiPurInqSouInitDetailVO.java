package com.midea.cloud.srm.model.extapi.sou.purinq.vo.init;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiPurInqSouInitDetailVO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private ApiPurInqSouInitProjectInfoVO projectInfo;
    @ApiModelProperty("项目需求")
    private List<ApiPurInqSouItemVO> requireInfo;
    @ApiModelProperty("邀请供应商")
    private List<ApiPurInqSouVendorVO> vendorInfo;

}
