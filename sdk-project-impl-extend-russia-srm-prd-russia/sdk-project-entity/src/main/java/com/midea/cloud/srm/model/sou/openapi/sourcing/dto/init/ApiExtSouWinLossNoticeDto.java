package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeInternalDTO;
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
@ApiModel("中/落标通知查询返回结果")
public class ApiExtSouWinLossNoticeDto extends BaseObjectX {

    @ApiModelProperty("查看中/落标通知")
    private List<BidNoticeDetailDTO> noticeDetailList;

    @ApiModelProperty("查看内部通知")
    private List<BidNoticeInternalDTO> noticeInternalList;
}
