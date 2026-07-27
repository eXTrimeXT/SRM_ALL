package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.init.ApiSouInitProjectInfoSwaggerVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.init.ApiSouItemSwaggerVO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 立项详情信息 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouInitDetailVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "立项详情信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouInitDetailSwaggerVO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private ApiSouInitProjectInfoSwaggerVO projectInfo;
    @ApiModelProperty("项目需求")
    private List<ApiSouItemSwaggerVO> requireInfo;
    @ApiModelProperty("邀请供应商")
    private List<ApiSouVendorVO> vendorInfo;

}
