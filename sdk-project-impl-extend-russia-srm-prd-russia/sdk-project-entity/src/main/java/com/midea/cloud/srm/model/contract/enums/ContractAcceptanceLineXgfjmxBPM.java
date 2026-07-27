package com.midea.cloud.srm.model.contract.enums;


import com.midea.cloud.meiql.api.function.SFunction;
import com.midea.cloud.srm.model.cm.perform.entity.PerAcceptanceAtt;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlanMilestone;
import lombok.Getter;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Getter
public enum ContractAcceptanceLineXgfjmxBPM {

    /**
     * 创建人
     */
    SCR("SCR","BO_EU_XGFJMX","line", PerAcceptanceAtt::getCreatedFullName,"创建人",PerAcceptanceAtt.class),
    /**
     * 上传时间
     */
    SCSJ("SCSJ","BO_EU_XGFJMX","line", PerAcceptanceAtt::getCreationDate,false,"",false,true, false, PerAcceptanceAtt.class, "creationDate", "上传时间"),
    /**
     * 附件上传
     */
    FJSC("FJSC","BO_EU_XGFJMX","line", null,false,"",false,true, true, PerPlanMilestone.class, null, "附件上传"),
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

    private Boolean isFile;

    private String fieldDesc;

    <T> ContractAcceptanceLineXgfjmxBPM(String bpmFieldName, String tableName, String tableType, SFunction<T, ?> function, String fieldDesc, Class<T> tClass){
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
        this.isFile = false;
    }

    <T> ContractAcceptanceLineXgfjmxBPM(String bpmFieldName, String tableName, String tableType, SFunction<T, ?> function, Boolean isDict, String dictCode, Boolean isYesOrNo, Boolean isDate, Boolean isFile, Class<T> tClass, String fieldName, String fieldDesc) {
        this.bpmFieldName = bpmFieldName;
        this.tableName = tableName;
        this.tableType = tableType;
        this.function = function;
        this.isDict = isDict;
        this.dictCode = dictCode;
        this.isYesOrNo = isYesOrNo;
        this.isDate = isDate;
        this.isFile = isFile;
        this.fieldDesc = fieldDesc;
        this.fieldName = fieldName;
    }




    }
