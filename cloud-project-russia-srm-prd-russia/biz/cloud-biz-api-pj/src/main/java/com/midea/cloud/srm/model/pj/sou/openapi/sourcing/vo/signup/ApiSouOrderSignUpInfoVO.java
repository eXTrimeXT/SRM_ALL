package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup;

import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSignUpFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangwk12@meicloud.com
 * @since 2022/11/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderSignUpInfoVO extends BaseObjectX {

    @ApiModelProperty("供应商信息")
    private SouVendor vendor;

    @ApiModelProperty("报名附件信息")
    private List<ApiSouSignUpFileVO> signUpFileList;

    public static ApiSouOrderSignUpInfoVO convertApiVO(SouVendor vendor, List<SouFile> outerFileList,
                                                       List<SouSignUpFile> signUpFileList) {
        ApiSouOrderSignUpInfoVO vo = new ApiSouOrderSignUpInfoVO();
        vo.setVendor(vendor);
        vo.setSignUpFileList(new ArrayList<>(signUpFileList.size()));
        {
            Map<Long/* souFileId */, SouFile> souFileMap = outerFileList.stream()
                    .collect(Collectors.toMap(SouFile::getSouFileId, Function.identity()));
            signUpFileList.forEach(signUpFile -> {
                ApiSouSignUpFileVO f = SouObjectXUtil.convertTargetObj(signUpFile, ApiSouSignUpFileVO.class);
                vo.getSignUpFileList().add(f);

                if (signUpFile.getSouFileId() != null) {
                    SouFile souFile = souFileMap.get(signUpFile.getSouFileId());
                    SouObjectXUtil.mergeProperties(souFile, f);
                }
            });
        }
        return vo;
    }

}
