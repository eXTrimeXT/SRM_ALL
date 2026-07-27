package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.vo.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.inq.dto.init.MqlInqSouProjectDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.process.MqlSouProcessNodeVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlInqSouProjectVO extends MqlInqSouProjectDTO {

    @ApiModelProperty("流程节点")
    private List<MqlSouProcessNodeVO> processNodeList;

}
