package com.midea.cloud.srm.model.pj.siss.dto;

import lombok.Data;

/**阳光诚信自主平台推送用户信息返回信息
 * @author GW00311146
 */
@Data
public class SunHonestyReturnDto {
    private String username;

    private String password;

    private String code;

    private String uuid;

    private String id;

    private String nickName;

    private String companyName;

    private String companyType;

    private String sourceType;

    private String ifPartner;

    private String image;

    private String email;

    private String state;

    private String contact;

    private String contactPhone;

    private String contactIdentityCard;

    private Long resultId;

    private String msg;


}
