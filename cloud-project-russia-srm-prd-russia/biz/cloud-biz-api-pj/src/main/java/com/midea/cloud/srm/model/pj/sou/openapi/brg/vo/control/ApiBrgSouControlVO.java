package com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.control;

import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init.ApiBrgSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouControlOrderVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouControlVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 项目式询价openAPI - 报价管理信息
 * PS: 参考 {@link ApiSouControlVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouControlVO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private ApiBrgSouProjectVO project;
    @ApiModelProperty("当前轮次信息")
    private SouRound currentRound;
    @ApiModelProperty("供应商报价信息")
    private List<ApiSouControlOrderVO> orderInfos;

}
