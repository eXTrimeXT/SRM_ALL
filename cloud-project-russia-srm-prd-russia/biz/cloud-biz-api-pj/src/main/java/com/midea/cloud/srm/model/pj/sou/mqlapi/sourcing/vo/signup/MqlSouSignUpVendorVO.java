package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.signup;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.signup.MqlSouSignUpFileVO;
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
 * MQL - 供应商报名详情
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouSignUpVendorVO extends SouVendor {

    @ApiModelProperty("公司信息")
    private CompanyInfo companyInfo;
    @ApiModelProperty("供应商报名附件")
    private List<com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.signup.MqlSouSignUpFileVO> signUpFileList;

    public static MqlSouSignUpVendorVO convertMqlVO(@Nullable SouVendor vendor,
                                                    List<SouFile> souFileList,
                                                    List<SouSignUpFile> signUpFileList,
                                                    CompanyInfo companyInfo) {
        MqlSouSignUpVendorVO vo; {
            if (vendor != null) {
                vo = SouObjectXUtil.convertTargetObj(vendor, MqlSouSignUpVendorVO.class);
            } else {
                vo = new MqlSouSignUpVendorVO();
                vo.setVendorId(companyInfo.getCompanyId());
                vo.setVendorCode(companyInfo.getCompanyCode());
                vo.setVendorName(companyInfo.getCompanyName());
            }
        }
        vo.setCompanyInfo(companyInfo);
        vo.setSignUpFileList(new ArrayList<>(souFileList.size() + signUpFileList.size())); {
            Map<Long/* souFileId */, SouSignUpFile> signUpFileMap = signUpFileList.stream()
                    .filter(e -> e.getSouFileId() != null)
                    .collect(Collectors.toMap(SouSignUpFile::getSouFileId, Function.identity()));
            souFileList.sort(Comparator.comparing(SouFile::getSortIndex));
            for (SouFile souFile : souFileList) {
                com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.signup.MqlSouSignUpFileVO fileVO = SouObjectXUtil.convertTargetObj(souFile, com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.signup.MqlSouSignUpFileVO.class);
                vo.getSignUpFileList().add(fileVO);

                SouSignUpFile signUpFile = signUpFileMap.get(souFile.getSouFileId());
                if (signUpFile != null) {
                    SouObjectXUtil.mergeProperties(signUpFile, fileVO);
                }
            }
            for (SouSignUpFile signUpFile : signUpFileList) {
                if (signUpFile.getSouFileId() != null) { continue; }
                com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.signup.MqlSouSignUpFileVO fileVO = SouObjectXUtil.convertTargetObj(signUpFile, MqlSouSignUpFileVO.class);
                vo.getSignUpFileList().add(fileVO);
            }
        }
        return vo;
    }

}
