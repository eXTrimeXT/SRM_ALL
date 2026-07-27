package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("查看招标结果")
public class ApiExtSouNoticeDto extends BaseObjectX {

    @ApiModelProperty("是否存在中标")
    private String isWin;

    @ApiModelProperty("是否发送")
    private String isSend;

    @ApiModelProperty("中标通知书列表")
    private List<BidNoticeDetailDTO> winNoticeList;

    @ApiModelProperty("中/落标通知书")
    private BidNoticeDetailDTO winOrLossNotice;

    @ApiModelProperty("落标通知书列表")
    private List<BidNoticeDetailDTO> lossNoticeList;


    public static ApiExtSouNoticeDto buildNoticeResult(List<BidNoticeDetailDTO> noticeDetailDTOList) {
        ApiExtSouNoticeDto noticeDto = new ApiExtSouNoticeDto();
        noticeDto.setIsSend(Enable.N.name());
        if(CollectionUtils.isNotEmpty(noticeDetailDTOList)) {
            Map<String, List<BidNoticeDetailDTO>> group = noticeDetailDTOList.stream().collect(Collectors.groupingBy(BidNoticeDetailDTO::getIsWin));
            noticeDto.setWinNoticeList(group.getOrDefault(Enable.Y.name(), new ArrayList<>()));
            noticeDto.setLossNoticeList(group.getOrDefault(Enable.N.name(), new ArrayList<>()));
            noticeDto.setIsSend(Enable.Y.name());
            if(group.containsKey(Enable.Y.name())) {
                noticeDto.setIsWin(Enable.Y.name());
                noticeDto.setWinOrLossNotice(JSON.parseObject(JSON.toJSONString(noticeDto.getWinNoticeList().get(0)), BidNoticeDetailDTO.class));
            } else {
                noticeDto.setWinOrLossNotice(JSON.parseObject(JSON.toJSONString(noticeDetailDTOList.get(0)), BidNoticeDetailDTO.class));
            }
        }
        return noticeDto;
    }
}
