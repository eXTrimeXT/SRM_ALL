package com.midea.cloud.srm.sou.sourcing.spi.init.editexperts;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRisk;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("随机抽取专家组PO类")
public class ExtSouRandomExtractExpertPO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private ExtSouProject souProject;

    @ApiModelProperty("抽取记录")
    private List<ExtSouExpertRecord> expertRecordList;

    /**
     * 抽取风险
     */
    private List<ExtSouExpertRisk> expertRiskList;

    @ApiModelProperty("抽取专家")
    private List<ExtSouGroup> groupList;
}
