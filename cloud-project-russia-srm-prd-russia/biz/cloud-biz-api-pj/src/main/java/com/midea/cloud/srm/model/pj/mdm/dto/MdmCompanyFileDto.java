package com.midea.cloud.srm.model.pj.mdm.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
public class MdmCompanyFileDto {
/** 路径 */
    private String 	path	;
    /** 名称 */
    private String 	fileName	;
}