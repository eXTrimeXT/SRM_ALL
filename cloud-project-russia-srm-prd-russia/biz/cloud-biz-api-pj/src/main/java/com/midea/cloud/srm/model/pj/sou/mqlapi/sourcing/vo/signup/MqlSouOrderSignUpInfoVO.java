package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.signup;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.signup.MqlSouSignUpFileVO;
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
 * @since 2023/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderSignUpInfoVO extends BaseObjectX {

    @ApiModelProperty("供应商信息")
    private SouVendor vendor;

    @ApiModelProperty("报名附件信息")
    private List<MqlSouSignUpFileVO> signUpFileList;

    public static MqlSouOrderSignUpInfoVO convertMqlVO(SouVendor vendor, List<SouFile> outerFileList, List<SouSignUpFile> signUpFileList) {
        MqlSouOrderSignUpInfoVO vo = new MqlSouOrderSignUpInfoVO();
        vo.setVendor(vendor);
        vo.setSignUpFileList(new ArrayList<>(signUpFileList.size())); {
            Map<Long/* souFileId */, SouFile> souFileMap = outerFileList.stream()
                    .collect(Collectors.toMap(SouFile::getSouFileId, Function.identity()));
            signUpFileList.forEach(signUpFile -> {
                MqlSouSignUpFileVO f = SouObjectXUtil.convertTargetObj(signUpFile, MqlSouSignUpFileVO.class);
                vo.getSignUpFileList().add(f);

                if (signUpFile.getSouFileId() != null) {
                    f.setSouFile(souFileMap.get(signUpFile.getSouFileId()));
                }
            });
        }
        return vo;
    }

}
