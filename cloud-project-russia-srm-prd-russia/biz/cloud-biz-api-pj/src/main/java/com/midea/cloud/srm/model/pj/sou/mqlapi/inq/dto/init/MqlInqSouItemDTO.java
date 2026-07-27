package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.dto.init;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init.MqlSouItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlInqSouItemDTO extends MqlSouItemDTO {

    @ApiModelProperty("简易询价拓展数据")
    private InqSouItem inqSouItem;

    @ApiModelProperty("技术附件")
    private List<SceneFile> itemFiles;

}
