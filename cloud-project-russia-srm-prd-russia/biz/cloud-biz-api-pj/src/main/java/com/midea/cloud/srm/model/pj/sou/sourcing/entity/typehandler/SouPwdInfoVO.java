package com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 寻源 - 密码开标信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/02
 */
@Data
public class SouPwdInfoVO {

    @ApiModelProperty("开标密码")
    private String openPwd;

    @ApiModelProperty("密码生成时间")
    private Date generateTime;

    public static Map<String/* openBidType */, SouPwdInfoVO> convertVO(Map<String/* openBidType */, String/* pwd */> pwdMap) {
        if (pwdMap == null || pwdMap.isEmpty()) { return Collections.emptyMap(); }
        Map<String/* openBidType */, SouPwdInfoVO> voMap = new HashMap<>(pwdMap.size());
        Date now = new Date();
        pwdMap.forEach((openBidType, pwd) -> {
            SouPwdInfoVO vo = new SouPwdInfoVO();
            vo.setOpenPwd(pwd);
            vo.setGenerateTime(now);

            voMap.put(openBidType, vo);
        });
        return voMap;
    }

}
