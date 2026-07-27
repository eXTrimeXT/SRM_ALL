package com.midea.cloud.srm.biz.pj.sunhonesty.service;

import com.midea.cloud.srm.model.pj.siss.dto.SunHonestyReturnDto;
import com.midea.cloud.srm.model.pj.siss.dto.SunHonestySupDto;
import com.midea.cloud.srm.model.pj.sunhonesty.dto.SunHonestyExam;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**对接阳光诚信自助平台相关
 * @author GW00311146
 */
public interface SunHonestyService {
    /**
     * 推送用户信息
     *
     * @param sunHonestySupDtos 供应商账号
     * @return sunHonestySupDtos
     */
    @ApiOperation(value = "推送用户信息")
    List<SunHonestyReturnDto> pushCompanyUser(List<SunHonestySupDto> sunHonestySupDtos);

    /**
     * 重定向 阳光诚信
     * @param u
     * @throws Exception
     */
    @ApiOperation(value = "获取登录url")
    String  getUrlForSunHonestySys(User u) throws Exception;
    /**
     * 校验是否需要考试
     * @throws Exception 异常
     */
    SunHonestyExam checkExam() throws Exception;

}
