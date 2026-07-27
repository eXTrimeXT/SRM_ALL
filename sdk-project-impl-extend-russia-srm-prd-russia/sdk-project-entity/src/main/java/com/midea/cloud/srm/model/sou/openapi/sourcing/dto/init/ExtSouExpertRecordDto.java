package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("抽取历史")
public class ExtSouExpertRecordDto extends ExtSouExpertRecord {

    @ApiModelProperty("移除招标工作组原因")
    private String extRemoveReason;

    @ApiModelProperty("专家等级")
    private String extExpertLevel;

    public String getExtExpertLevel() {
        return super.getExpertLevel();
    }
}
