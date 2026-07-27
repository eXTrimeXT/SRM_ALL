package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderItem;
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
@ApiModel("招标电子签章签署")
public class ApiExtSouSignEditDto extends BaseObjectX {

    public static final String TYPE_BID_TECH = "BID_TECH";

    public static final String TYPE_BID_BUSINESS = "BID_BUSINESS";

    @ApiModelProperty("投标单据ID")
    private Long orderId;

    @ApiModelProperty("签章类型")
    private String orderType;

    @ApiModelProperty("签署文件列表")
    private List<ExtSouOrderFile> signFileList;
}
