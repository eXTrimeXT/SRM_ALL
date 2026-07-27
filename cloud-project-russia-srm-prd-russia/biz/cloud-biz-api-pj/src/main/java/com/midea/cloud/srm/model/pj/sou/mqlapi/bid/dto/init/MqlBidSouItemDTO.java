package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.dto.init;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init.MqlSouItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouItemDTO extends MqlSouItemDTO {

    @ApiModelProperty("招投标拓展数据")
    private BidSouItem bidSouItem;

    @ApiModelProperty("技术附件")
    private List<SceneFile> itemFiles;

}
