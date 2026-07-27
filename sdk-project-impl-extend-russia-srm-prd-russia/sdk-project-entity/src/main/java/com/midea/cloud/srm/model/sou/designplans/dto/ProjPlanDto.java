package com.midea.cloud.srm.model.sou.designplans.dto;

import com.midea.cloud.srm.model.sou.designplans.entity.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ex_liuxy46
 */
@Data
public class ProjPlanDto implements Serializable {

    private Long designId;

    /**
     * 基础信息
     */
    private SccSouChDemandProgramme demandProgramme;

    /**
     * 工作日程
     */
    private List<SccSouChDemandWork> workList;

    /**
     * 招标策略及目标设定-汽柴油
     */
    private List<SccSouChDemandStrategy> strategyList;

    /**
     * 招标策略及目标设定-其他
     */
    private List<SccSouChDemandOther> otherList;

    /**
     * 招标策略及目标设定-招标策略及目标设定
     */
    private List<SccSouChDemandSetting> settingList;

    /**
     * 招标策略及目标设定-使用单位金额分析
     */
    List<SccSouChDemandAnalysis> unitList;

    /**
     * 招标策略及目标设定-按物流品类分析
     */
    List<SccSouChDemandAnalysis> categoryList;
    /**
     * 招标策略及目标设定-供应商金额分析
     */
    List<SccSouChDemandAnalysis> supplyList;
}
