package com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProcessConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ApiExtSouProcessConfigVo extends ExtSouProcessConfig {

    @ApiModelProperty("编制定标结果")
    private Enable bidReuslt;

    @ApiModelProperty("中/落标通知")
    private Enable bidWinOrLoss;

    @ApiModelProperty("归档")
    private Enable bidArchive;

    @ApiModelProperty("保证金管理")
    private Enable bondManagement;

    public void initeExtField() {
        this.bidArchive = getExtBidArchive();
        this.bidReuslt = getExtBidReuslt();
        this.bidWinOrLoss = getExtBidWinOrLoss();
        this.bondManagement = getExtBondManagement();
    }
}
