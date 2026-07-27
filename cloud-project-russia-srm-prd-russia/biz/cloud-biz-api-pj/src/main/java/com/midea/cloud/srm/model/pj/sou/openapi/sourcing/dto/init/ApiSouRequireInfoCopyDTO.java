package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouRequireInfoDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 寻源openAPI - 立项物料需求复制
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouRequireInfoCopyDTO extends BaseObjectX {

    @ApiModelProperty("物料需求信息")
    private ApiSouRequireInfoDTO requireInfo;
    /** oldSouItemId newSouItemId */
    @ApiModelProperty("物料需求行ID变更记录<oldSouItemId, newSouItemId>")
    private Map<Long, Long> souItemIdRecords;

}
