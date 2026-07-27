package com.midea.cloud.srm.model.pj.enums;

import com.alibaba.excel.util.StringUtils;

/**
 * @author huangbf3
 */
public enum FeignEnum {
    /**
     * 协同
     */
    SC("com.midea.cloud.srm.feign.supcooperate.SupcooperateClient", "com.midea.cloud.srm.feign.pj.cooperate.CooperateBpmClient"),
    /**
     * 供应商
     */
    SUP("com.midea.cloud.srm.feign.supplier.SupplierClient", "com.midea.cloud.srm.feign.pj.supplier.SupplierBpmClient"),
    /**
     * 合同
     */
    CON("com.midea.cloud.srm.feign.contract.ContractClient", "com.midea.cloud.srm.feign.pj.contract.ContractBpmClient"),
    /**
     * 招标
     */
    SOU("com.midea.cloud.srm.feign.sou.SouClient", "com.midea.cloud.srm.feign.pj.sou.SouBpmClient"),
    /**
     * 二开
     */
    PJ("com.midea.cloud.srm.feign.pj.ProjectFlowClient", "com.midea.cloud.srm.feign.pj.pj.PjBpmClient")
    ;

    private String code;
    private String bpmFeign;

    FeignEnum(String code, String bpmFeign) {
        this.code = code;
        this.bpmFeign = bpmFeign;
    }

    public String getCode() {
        return code;
    }

    public String getBpmFeign() {
        return bpmFeign;
    }

    public static FeignEnum getBpmFeignByCode(String code) {
        if(StringUtils.equals(SC.getCode(),code)){
            return SC;
        }else if(StringUtils.equals(SUP.getCode(),code)){
            return SUP;
        }else if(StringUtils.equals(CON.getCode(),code)){
            return CON;
        }else if(StringUtils.equals(SOU.getCode(),code)){
            return SOU;
        }else if(StringUtils.equals(PJ.getCode(),code)){
            return PJ;
        }
        return null;
    }
}
