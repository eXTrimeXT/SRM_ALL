package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init;

import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 寻源openAPI - 立项查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/28
 */
@Service
public class ApiSouInitQueryHandler implements ISouSpiBean {

    @ApiOperation("寻源分页查询的后置处理")
    public List<SouProject> doHandlerAfterPageProjects(ApiSouProjectQueryDTO queryParam, String souType, List<SouProject> souProjectList) {
        return souProjectList;
    }

    @ApiOperation("查询寻源基本信息的后置处理")
    public ApiSouInitProjectInfoVO doHandlerAfterGetProject(long projectId, String souType, ApiSouInitProjectInfoVO vo) {
        return vo;
    }

    @ApiOperation("查询寻源物料需求的后置处理")
    public List<ApiSouItemVO> doHandlerAfterListRequires(long projectId, String souType, List<ApiSouItemVO> voList) {
        return voList;
    }

    @ApiOperation("查询寻源邀请供应商的后置处理")
    public List<ApiSouVendorVO> doHandlerAfterListVendors(long projectId, String souType, List<ApiSouVendorVO> voList) {
        return voList;
    }

    @ApiOperation("查询指定邀请供应商的后置处理")
    public ApiSouVendorVO doHandlerAfterGetVendor(long projectId, long vendorId, String souType, ApiSouVendorVO vo) {
        return vo;
    }

    @ApiOperation("查询寻源立项所有信息的后置处理")
    public ApiSouInitDetailVO doHandlerAfterGetSouInitInfo(long projectId, String souType, ApiSouInitDetailVO vo) {
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
