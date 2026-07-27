package com.midea.cloud.srm.model.pj.contract;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@TableName("scc_pj_contract_seal")
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "合同印章维护")
public class ContractSeal extends BaseEntity {

    @TableId
    @ApiModelProperty("主键id")
    private Long contractSealId;

    @ApiModelProperty("签章单位名称")
    private String signCompanyName;

    @ApiModelProperty("印章名称")
    private String sealName;

    @ApiModelProperty("印章ID")
    private String sealId;

}
