package com.midea.cloud.srm.biz.pj.registrationverification;



/**
 * @author huangbf3
 */
public interface RegistrationVerificationService {

    /**
     * 供应商注册验证码验证接口
     * @param verifyType
     * @param email
     * @param phone
     */
     void  sendVerifyCode(String verifyType, String email  ,String phone);

}