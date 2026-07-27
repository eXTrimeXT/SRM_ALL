package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("寻源核心-流程配置")
@TableName(value = "scc_sou_process_config")
public class ExtSouProcessConfig extends SouProcessConfig{

    @ApiModelProperty("编制定标结果")
    private Enable extBidReuslt;

    @ApiModelProperty("中/落标通知")
    private Enable extBidWinOrLoss;

    @ApiModelProperty("归档")
    private Enable extBidArchive;

    @ApiModelProperty("保证金管理")
    private Enable extBondManagement;

}
