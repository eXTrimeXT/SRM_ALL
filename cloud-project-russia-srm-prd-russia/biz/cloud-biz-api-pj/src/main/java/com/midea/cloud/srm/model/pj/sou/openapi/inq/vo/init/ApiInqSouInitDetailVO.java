package com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init;

import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init.ApiInqSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init.ApiInqSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 简易询价 - 立项信息
 * PS: 参考{@link ApiSouInitDetailVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouInitDetailVO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private ApiInqSouInitProjectInfoVO projectInfo;
    @ApiModelProperty("项目需求")
    private List<ApiInqSouItemVO> requireInfo;
    @ApiModelProperty("邀请供应商")
    private List<ApiSouVendorVO> vendorInfo;

}
