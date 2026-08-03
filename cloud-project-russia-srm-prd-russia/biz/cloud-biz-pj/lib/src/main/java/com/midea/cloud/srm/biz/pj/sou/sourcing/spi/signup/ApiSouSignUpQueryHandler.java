package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup;

import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpVendorVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 寻源openAPI - 报名查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Service
public class ApiSouSignUpQueryHandler implements ISouSpiBean {

    @ApiOperation("查询供应商报名信息后的额外处理")
    public List<ApiSouSignUpQueryVO> doHandlerAfterListVendorSignUp(ApiSouSignUpQueryDTO queryParam, String souType, List<ApiSouSignUpQueryVO> voList) {
        return voList;
    }

    @ApiOperation("查询供应商报名详情后的额外处理")
    public ApiSouSignUpVendorVO doHandlerAfterGetVendorSignUpDetail(long projectId, long vendorId, String souType, ApiSouSignUpVendorVO vo) {
        return vo;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
