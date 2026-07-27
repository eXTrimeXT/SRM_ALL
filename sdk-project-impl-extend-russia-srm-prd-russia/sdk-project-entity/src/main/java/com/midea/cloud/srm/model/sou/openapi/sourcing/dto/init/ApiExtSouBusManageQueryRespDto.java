package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("商务管理查询接口整合")
public class ApiExtSouBusManageQueryRespDto extends BaseObjectX {


    @ApiModelProperty("投标详情")
    private List<ExtSouOrderDto> orderList;

    @ApiModelProperty("谈判资料")
    private List<ExtSouFile> talkFileList;

    @ApiModelProperty("开标人员记录表")
    private List<ExtNpmSouOpenBidRecordDto> openUserList;
}
