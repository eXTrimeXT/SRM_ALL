package com.midea.cloud.srm.model.pj.sapcreatesupview.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author fubiao
 */
@Data
public class SapCreateSupViewListDto {
/**    	传SAP列表 */
    @JSONField(name ="LIST")
    private List<SapCreateSupViewDto> LIST	;
}
