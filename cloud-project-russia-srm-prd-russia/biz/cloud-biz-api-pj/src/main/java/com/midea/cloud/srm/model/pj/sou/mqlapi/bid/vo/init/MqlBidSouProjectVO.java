package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.bid.dto.init.MqlBidSouProjectDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.inq.dto.init.MqlInqSouProjectDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.process.MqlSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 招投标MQL - 寻源单
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouProjectVO extends MqlBidSouProjectDTO {

    @ApiModelProperty("流程节点")
    private List<MqlSouProcessNodeVO> processNodeList;

}
