package com.midea.cloud.srm.mall.result.jd.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JDBaseResult {

    @JsonProperty("success")
    private boolean success;
    @JsonProperty("resultMessage")
    private String resultMessage;
    @JsonProperty("resultCode")
    private String resultCode;

}
