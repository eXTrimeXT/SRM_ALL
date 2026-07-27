package com.midea.cloud.srm.model.pj.sapcreatesupview.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author fubiao
 */
@Data
public class SapResultDto {
    /**    否	string			信息 */
    private String MSG;
    /**    否	string			供应商编码 */
    @JSONField(name ="LIFNR")
    private String LIFNR;
    /**    否	string			标记 */
    @JSONField(name ="FLAG")
    private String FLAG;
}
