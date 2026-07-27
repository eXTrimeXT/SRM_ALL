package com.midea.cloud.srm.model.sou.enums;

 /**
 * 备注
  * @author huangbf3
 */
public enum DictCodeEnum {
     /**
      * 评分配置打分项
      */
    SOU_SCORE_CONFIG_ITEM("SOU_SCORE_CONFIG_ITEM", "评分配置打分项"),
    /**
     * 供应商推荐启用接口配置
     **/
    RECOMMVENOR_SERVICE("RECOMMVENOR_SERVICE", "供应商推荐启用接口配置"),
     /**
      * 供应商风险管理-风险等级
      **/
     RISK_LEVEL("RISK_LEVEL", "供应商风险管理-风险等级"),
     /**
      * 供应商风险管理-风险类型
      **/
     RISK_TYPE("RISK_TYPE", "供应商风险管理-风险类型"),

     /**
      * 定标申请特殊招标唯一单位不一致提醒钉钉账号维护字典
      */
     CA_SPECIAL_REMINDERS("CA_SPECIAL_REMINDERS", "定标申请特殊招标唯一单位不一致提醒钉钉账号维护字典"),

     CONTRACT_SOURCE_TYPE("CONTRACT_SOURCE_TYPE", "合同来源类型"),

     CONTRACT_FORM2("CONTRACT_FORM2", "签署方式"),

     SOU_BID_YEAR_MARGIN("SOU_BID_YEAR_MARGIN", "年度保证金品类");

    private String code;
    private String name;

    DictCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
