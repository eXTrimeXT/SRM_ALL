package com.midea.cloud.srm.model.pj.orguser.dto;

import lombok.Data;

/**
 * @author ex_liuxy46
 */
@Data
public class UserReturnDto {

    private UserResultDto result;
    private Integer code;
    private String message;
}
