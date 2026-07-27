package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ex_liuxy46
 */
@Data
public class EdmPrHeadDto implements Serializable {

    /** 需求类型 */
    private String demandType;
    /** 申请人（工号） */
    private String applyBy;
    /** 申请单类型 */
    private String applicationFormType;
    /** 采购说明 */
    private String ceeaAppointReason;
    /** 外部单号 */
    private String exNo;
    /** 来源 */
    private String source;
    /** 行信息 */
    private List<EdmPrLineDto> lineList;
}
