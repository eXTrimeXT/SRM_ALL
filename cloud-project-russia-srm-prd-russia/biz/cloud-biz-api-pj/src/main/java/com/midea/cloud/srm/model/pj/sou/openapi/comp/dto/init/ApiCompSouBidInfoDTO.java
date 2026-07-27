package com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 竞价openAPI - 项目信息保存
 * PS: 参考 {@link ApiSouProjectInfoDTO}，变量名保持一致
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@ApiModel("竞价立项保存信息")
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouBidInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单基本信息")
    private ApiSouProjectEditDTO project;

    @ApiModelProperty("物料信息")
    private ApiSouItemEditDTO requireInfo;

    @ApiModelProperty("供应商信息")
    private ApiSouVendorEditDTO vendorInfo;

    @ApiModelProperty("内部查看附件")
    private List<ApiSouFileEditDTO> innerFileList;
    @ApiModelProperty("外部查看附件")
    private List<ApiSouFileEditDTO> outerFileList;


}
