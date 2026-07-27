package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouFileEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSignUpFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 供应商报名详情
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouSignUpVendorVO extends SouVendor {

    @ApiModelProperty("公司信息")
    private CompanyInfo companyInfo;
    @ApiModelProperty("供应商报名附件")
    private List<ApiSouSignUpFileVO> signUpFileList;
    @ApiModelProperty("保证金信息")
    private CompSouProject compSouProject;
    @ApiModelProperty("保证金附件")
    private List<ApiSouFileEditDTO> bondFileList;

    public static ApiSouSignUpVendorVO convertApiVO(@Nullable SouVendor vendor,
                                                    CompSouProject compSouProject,
                                                    List<SouFile> souFileList,
                                                    List<SouFile> bondFileList,
                                                    List<SouSignUpFile> signUpFileList,
                                                    CompanyInfo companyInfo) {
        ApiSouSignUpVendorVO vo;
        {
            if (vendor != null) {
                vo = SouObjectXUtil.convertTargetObj(vendor, ApiSouSignUpVendorVO.class);
            } else {
                vo = new ApiSouSignUpVendorVO();
                vo.setVendorId(companyInfo.getCompanyId());
                vo.setVendorCode(companyInfo.getCompanyCode());
                vo.setVendorName(companyInfo.getCompanyName());
            }
        }
        List<ApiSouFileEditDTO> apiSouFileEditDtos = SouObjectXUtil.convertList(bondFileList, ApiSouFileEditDTO.class);
        List<ApiSouSignUpFileVO> apiSouSignUpFileVos = SouObjectXUtil.convertList(signUpFileList, ApiSouSignUpFileVO.class);
        vo.setCompSouProject(compSouProject);
        vo.setCompanyInfo(companyInfo);
        vo.setSignUpFileList(apiSouSignUpFileVos);
        vo.setBondFileList(apiSouFileEditDtos);
        return vo;
    }

}
