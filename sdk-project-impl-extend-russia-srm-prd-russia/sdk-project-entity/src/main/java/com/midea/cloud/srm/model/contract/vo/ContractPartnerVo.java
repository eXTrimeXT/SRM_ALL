package com.midea.cloud.srm.model.contract.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/19
 */
@TableName("scc_contract_partner")
@ApiModel(
        description = "合同伙伴"
)
@Data
public class ContractPartnerVo {

    @ApiModelProperty("伙伴名称")
    @TableField("PARTNER_NAME")
    private String partnerName;

    @ApiModelProperty("伙伴类型:甲方/乙方")
    @TableField("PARTNER_TYPE")
    private String partnerType;

    @ApiModelProperty("签署状态")
    @TableField("EXT_STAMP_STATUS")
    private String extStampStatus;

    @ApiModelProperty("工号")
    @TableField("EXT_EMPLOYEE_NUMBER")
    private String extEmployeeNumber;
}
