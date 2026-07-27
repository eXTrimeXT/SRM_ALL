package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Getter
@Setter
public class SouProjectDTO {

    private Long projectId;

    private String souNo;

    private Date publishTime;

    private Date signUpStartTime;

    private Date signUpEndTime;

    private Date orderStartTime;

    private Date orderEndTime;

    private String souRules;

    private String publicRules;

    private Integer orderNum;

    private Integer quoteCap;

    private String souName;

    private String souType;

    private Long processConfigId;

    private SouScoreRuleTypeEnum scoreRuleType;

    private Long scoreTemplateId;

    private String scoreTemplateName;

    private String standardCurrency;

    private Integer pricePrecision;

    private SouProjectStatusEnum projectStatus;

    private SouApprovalStatusEnum createApprovalStatus;

    private Enable needEncryptPrice;

    private Integer currentRound;

    private String orderSite;

    private Enable isSyncToPriceLibrary;

    private String generatePriceApprovalType;

    private String cancelReason;

    private String needPwdOperations;

    private Enable allowItemChange;

    private Enable allowNewVendors;

    private Enable allowProxyOrder;

    private Date priceStartTime;

    private Date priceEndTime;

    private Enable techOpen;

    private Date techOpenTime;

    private Date earliestBusinessOpenTime;

    private SouPublishScopeEnum publishScope;

    private SouOrderWayEnum orderWay;

    private SouOrderTypeEnum orderType;

    private Enable allowWithdraw;

    private Enable allowPartPrice;

    private Enable isPriceNotax;

    private Integer inviteCount;

    private Integer orderCount;

    private String linkman;

    private String tel;

    private String email;

    private String remark;

    private String sourceFromType;

    private Long sourceFromId;

    private String sourceFromNo;

    private Long quoteTempId;

    private String quoteTempName;

    private String extProjectNo;

    private Long orgBuId;

    private String orgBuCode;

    private String orgBuNAME;

    private Long companyId;

    private String companyCode;

    private String companyName;

    private Long depId;

    private String depCode;

    private String depName;
}
