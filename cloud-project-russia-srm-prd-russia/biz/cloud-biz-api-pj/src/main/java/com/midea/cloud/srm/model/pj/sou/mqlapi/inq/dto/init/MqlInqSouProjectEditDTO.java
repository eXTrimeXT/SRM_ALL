package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.dto.init;

import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlInqSouProjectEditDTO extends SouProject {

    @ApiModelProperty("简易询价拓展数据")
    private InqSouProject inqSouProject;

}
