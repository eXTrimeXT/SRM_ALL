package com.midea.cloud.srm.model.contract.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/19
 */
@TableName("scc_contract_head")
@ApiModel(
        description = "合同主表"
)
@Data
public class ContractHeadVo {
    @TableField("CREATION_DATE")
    @ApiModelProperty("时间")
    private Date creationDate;
    @TableField("EXT_CONTRACT_HANDLER_NAME")
    @ApiModelProperty("经办人名称")
    private String ExtContractHandlerName;
    @TableField("CONTRACT_NAME")
    @ApiModelProperty("合同名称")
    private String ContractName;
}
