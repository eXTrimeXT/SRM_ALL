package com.midea.cloud.srm.model.contract.dto;

import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 100014336 ganyh19
 */
@Data
public class ContractHeadExt extends ContractHead {

    /**
     * 合同正文
     */
    private String extContentFinal;

    /**
     * 合同
     */
    private String extPricePoolFlag;

    /**
     * bpm发起人账号
     */
    private String startBpmUsername;

    /**
     * bpm发起人名称
     */
    private String startBpmNickname;

    /**
     * 招标负责人ID
     */
    private Long extInviteHeadId;

    /**
     * 招标负责人名称
     */
    private String extInviteHeadName;

    /**
     * 招标负责人账号
     */
    private String extInviteHeadAccount;

    @ApiModelProperty("印章ID")
    private Long sealId;

    @ApiModelProperty("是否自动签章")
    private Integer autoSign;
}
