package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.control;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.control.MqlSouControlOrderVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init.MqlSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * MQL - 投标控制查询
 *
 * @author ex_nongtb@partner.midea.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouControlVO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private MqlSouProjectVO project;
    @ApiModelProperty("当前轮次信息")
    private SouRound currentRound;
    @ApiModelProperty("供应商报价信息")
    private List<MqlSouControlOrderVO> orderInfos;

    public static MqlSouControlVO convertMqlVO(MqlSouProjectVO project,
                                               SouRound currentRound,
                                               List<SouVendor> vendorList,
                                               List<SouOrder> orderList,
                                               List<SouFileConfig> fileConfigList,
                                               List<SouOrderFile> orderFileList) {
        MqlSouControlVO vo = new MqlSouControlVO();
        vo.setProject(project);
        vo.setCurrentRound(currentRound);
        vo.setOrderInfos(MqlSouControlOrderVO.convertMqlVO(project, currentRound, vendorList, orderList, fileConfigList, orderFileList));
        return vo;
    }

}
