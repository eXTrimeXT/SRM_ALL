package com.midea.cloud.srm.model.contract.enums;


import com.midea.cloud.meiql.api.function.SFunction;
import com.midea.cloud.srm.model.cm.perform.entity.PerAcceptance;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlan;
import lombok.Getter;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Getter
public enum ContractAcceptanceHeadBPM {

    /**
     * 合同序号
     */
    HTXH("HTXH","BO_EU_HTZZ","head",PerPlan::getContractNo,"合同序号",PerPlan.class),
    /**
     * 里程碑模板编号
     */
    MBBH("MBBH","BO_EU_HTZZ","head",PerPlan::getProcessNum,"里程碑模板编号",PerPlan.class),
    /**
     * 里程碑模板名称
     */
    MBMC("MBMC","BO_EU_HTZZ","head",PerPlan::getTemplateName,"里程碑模板名称",PerPlan.class),
    /**
     * 业务实体
     */
    YWST("YWST","BO_EU_HTZZ","head",PerPlan::getBuName,"业务实体",PerPlan.class),
    /**
     * 供应商名称
     */
    GYSMC("GYSMC","BO_EU_HTZZ","head",PerPlan::getVendorName,"供应商名称",PerPlan.class),
    /**
     * 合同验收单号
     */
    HTYSH("HTYSH","BO_EU_HTZZ","head",PerAcceptance::getPerAcceptanceNo,"合同验收单号",PerAcceptance.class),
    /**
     * 合同总金额
     */
    HTZJE("HTZJE","BO_EU_HTZZ","head",PerPlan::getIncludeTaxAmount,"合同总金额",PerPlan.class),
    /**
     * 币种
     */
    BZ("BZ","BO_EU_HTZZ","head",PerPlan::getCurrencyName,"币种",PerPlan.class),
    /**
     * 创建人
     */
    CJR("CJR","BO_EU_HTZZ","head",PerAcceptance::getCreatedFullName,"创建人",PerAcceptance.class),
    /**
     * 创建时间
     */
    CJSJ("CJSJ","BO_EU_HTZZ","head",PerAcceptance::getCreationDate,false,null,false,true,"创建时间",PerAcceptance.class,"creationDate"),
    /**
     * 交付说明
     */
    JFSM("JFSM","BO_EU_HTZZ","head",PerAcceptance::getDeliveryExplain,"交付说明",PerAcceptance.class),
    /*LYGCPJ	履约过程评价 启用 */
    ;


    private String bpmFieldName;

    private String tableName;

    private String fieldName;

    private String tableType;

    private SFunction function;

    private Boolean isDict;

    private String dictCode;

    private Boolean isYesOrNo;

    private Boolean isDate;

    private String fieldDesc;

    private Class type;

    <T> ContractAcceptanceHeadBPM(String bpmFieldName, String tableName, String tableType, SFunction<T, ?> function, String fieldDesc, Class<T> tClass){
        this.bpmFieldName = bpmFieldName;
        this.tableName = tableName;
        this.tableType = tableType;
        this.function = function;
        this.isDict = false;
        this.dictCode = "";
        this.isYesOrNo = false;
        this.isDate = false;
        this.fieldDesc = fieldDesc;
        this.fieldName = "";
        this.type = tClass;
    }

    <T> ContractAcceptanceHeadBPM(String bpmFieldName, String tableName, String tableType, SFunction<T, ?> function, Boolean isDict, String dictCode, Boolean isYesOrNo, Boolean isDate, String fieldDesc, Class<T> tClass, String fieldName) {
        this.bpmFieldName = bpmFieldName;
        this.tableName = tableName;
        this.tableType = tableType;
        this.function = function;
        this.isDict = isDict;
        this.dictCode = dictCode;
        this.isYesOrNo = isYesOrNo;
        this.isDate = isDate;
        this.fieldDesc = fieldDesc;
        this.fieldName = fieldName;
        this.type = tClass;
    }




    }
