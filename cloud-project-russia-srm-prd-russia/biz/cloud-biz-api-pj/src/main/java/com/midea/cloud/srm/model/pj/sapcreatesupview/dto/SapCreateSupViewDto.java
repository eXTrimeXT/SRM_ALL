package com.midea.cloud.srm.model.pj.sapcreatesupview.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author fubiao
 */
@Data
public class SapCreateSupViewDto {
    /**    	供应商编码 */
    @JSONField(name ="LIFNR")
    private String 	LIFNR	;
    /**    	公司代码 */
    @JSONField(name ="BUKRS")
    private String 	BUKRS	;
    /**    统驭科目 */
    @JSONField(name ="AKONT")
    private String 	AKONT	;
    /**    	排序码 */
    @JSONField(name ="ZUAWA")
    private String 	ZUAWA	;
    /**    	采购组织 */
    @JSONField(name ="EKORG")
    private String 	EKORG	;
    /**    	账户组 */
    @JSONField(name ="KTOKK")
    private String 	KTOKK	;
    /**    	公司名称*/
    @JSONField(name ="NAME1")
    private String 	NAME1	;
    /**    	公司简称 */
    @JSONField(name ="SORT1")
    private String 	SORT1	;
    /**    	地址 */
    @JSONField(name ="STREET")
    private String 	STREET	;
    /**    	城市 */
    @JSONField(name ="CITY1")
    private String 	CITY1	;
    /**    	国家 */
    @JSONField(name ="COUNTRY")
    private String 	COUNTRY	;
    /**    	地区 */
    @JSONField(name ="REGION")
    private String 	REGION	;
    /**    	订单货币 */
    @JSONField(name ="WAERS")
    private String 	WAERS	;
    /**    	付款条件 */
    @JSONField(name ="ZTERM")
    private String 	ZTERM	;
    /**    	订单货币 */
    @JSONField(name ="WEBRE")
    private String 	WEBRE	;
}
