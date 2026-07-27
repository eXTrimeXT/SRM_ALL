package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.dto.control;

import com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.init.MqlBidSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.control.MqlSouControlOrderVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.control.MqlSouControlVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 招投标MQL - 报价管理信息
 * PS: 参考 {@link MqlSouControlVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouControlVO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private MqlBidSouProjectVO project;
    @ApiModelProperty("当前轮次信息")
    private SouRound currentRound;
    @ApiModelProperty("供应商报价信息")
    private List<MqlSouControlOrderVO> orderInfos;

}
