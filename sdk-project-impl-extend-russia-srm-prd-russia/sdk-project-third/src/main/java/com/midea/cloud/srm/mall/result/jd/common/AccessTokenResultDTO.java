package com.midea.cloud.srm.mall.result.jd.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/2/28 14:28
 *  修改内容:
 * </pre>
 */
@Data
@NoArgsConstructor
public class AccessTokenResultDTO extends JDBaseResult implements CommonResultDTO {

    private AccessToken result;

    @Data
    public static class AccessToken {
        @ApiModelProperty("业务id")
        @JsonProperty("uid")
        private String uid;

        @ApiModelProperty("访问令牌，用于业务接口调用。有效期24小时")
        @JsonProperty("access_token")
        private String access_token;

        @ApiModelProperty("当access_token过期时，用于刷新access_token")
        @JsonProperty("refresh_token")
        private String refresh_token;

        @ApiModelProperty("当前时间，时间戳格式：1551663377887")
        @JsonProperty("time")
        private Long time;

        @ApiModelProperty("access_token的有效期，单位：秒，有效期24小时")
        @JsonProperty("expires_in")
        private Integer expires_in;

        @ApiModelProperty("refresh_token的过期时间，毫秒级别,时间戳")
        @JsonProperty("refresh_token_expires")
        private Long refresh_token_expires;
    }
}
