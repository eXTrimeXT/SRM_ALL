package com.midea.cloud.gateway.pj.dto;

import lombok.Data;

/**
 * <pre>
 *  登录DTO
 * @author huangbf3
 * </pre>
 */
@Data
public class UserLoginDTO {
    private String username;
    private String password;
    private String redirectUri;
}
