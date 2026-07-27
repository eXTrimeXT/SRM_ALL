package com.midea.cloud.srm.model.contract.enums;


import com.midea.cloud.meiql.api.function.SFunction;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlanMilestone;
import lombok.Getter;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Getter
public enum ContractAcceptanceLineLcbmxBPM {


    /**
     * 节点负责人
     */
    JDFZR("JDFZR","BO_EU_LCBMX","line", PerPlanMilestone::getNodePersonName,"节点负责人", PerPlanMilestone.class),
    /**
     * 计划开始时间
     */
    LCBMC("LCBMC","BO_EU_LCBMX","line", PerPlanMilestone::getPlanStartDate,false,"MILESTONE_SCHEDULE",false,true,"计划开始时间", PerPlanMilestone.class,"planStartDate"),
    /**
     * 计划开始时间
     */
    JHKS("JHKS","BO_EU_LCBMX","line", PerPlanMilestone::getPlanStartDate,false,"",false,true,"计划开始时间", PerPlanMilestone.class,"planStartDate"),
    /**
     * 计划结束时间
     */
    JHJS("JHJS","BO_EU_LCBMX","line", PerPlanMilestone::getPlanEndDate,false,"",false,true,"计划结束时间", PerPlanMilestone.class,"planEndDate"),
    /**
     * 计划结束时间
     */
    SJJS("SJJS","BO_EU_LCBMX","line", PerPlanMilestone::getPracticallyEndDate,false,"",false,true,"计划结束时间", PerPlanMilestone.class,"practicallyEndDate"),
    /**
     * line
     */
    BZ("BZ","BO_EU_LCBMX","line",PerPlanMilestone::getRemarks,"line",PerPlanMilestone.class);;




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

    <T> ContractAcceptanceLineLcbmxBPM(String bpmFieldName, String tableName, String tableType, SFunction<T, ?> function, String fieldDesc, Class<T> tClass){
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

    <T> ContractAcceptanceLineLcbmxBPM(String bpmFieldName, String tableName, String tableType, SFunction<T, ?> function, Boolean isDict, String dictCode, Boolean isYesOrNo, Boolean isDate, String fieldDesc, Class<T> tClass, String fieldName) {
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
