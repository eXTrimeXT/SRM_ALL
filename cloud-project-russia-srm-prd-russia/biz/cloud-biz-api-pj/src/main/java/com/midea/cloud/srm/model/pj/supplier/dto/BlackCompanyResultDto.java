package com.midea.cloud.srm.model.pj.supplier.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author fubiao
 */
@Data
public class BlackCompanyResultDto {
    /**    	string			总数 */
    @JSONField(name ="total")
    private String total;
    /**    	string			当前页码 */
    @JSONField(name ="page")
    private String page;
    /**    	string			每页数量 */
    @JSONField(name ="size")
    private String size;
    /**    	string			结果集 */
    @JSONField(name ="rows")
    private List<BlackCompanyListDto> rows;
}
