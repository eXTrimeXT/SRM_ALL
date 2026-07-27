package com.midea.cloud.srm.model.pj.supplier.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author fubiao
 */
@Data
public class BlackCompanyDto {
/**    	失信列表请求传参 */
    /**    否	string			页码 */
    @JSONField(name ="page")
    private String page;
    /**    否	string			每页数量 */
    @JSONField(name ="size")
    private String size;
    /**    否	string			起始日期（更新时间，时间格式，例：2022-01-01 10:10:10） */
    @JSONField(name ="startUpdateTime")
    private String startUpdateTime;
    /**    否	string			截止日期（更新时间，时间格式，例：2024-01-01 10:10:10） */
    @JSONField(name ="endUpdateTime")
    private String endUpdateTime;


}
