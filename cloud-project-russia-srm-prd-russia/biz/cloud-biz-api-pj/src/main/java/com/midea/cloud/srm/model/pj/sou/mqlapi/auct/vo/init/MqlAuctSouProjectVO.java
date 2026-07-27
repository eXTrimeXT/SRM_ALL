package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.init.MqlAuctSouProjectDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.process.MqlSouProcessNodeVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 竞价MQL - 立项信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouProjectVO extends MqlAuctSouProjectDTO {

    @ApiModelProperty("流程节点")
    private List<MqlSouProcessNodeVO> processNodeList;

}
