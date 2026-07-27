package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouControlOrderVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源openAPI - 投标控制查询
 *
 * @author ex_nongtb@partner.midea.com
 * @since 2022/09/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouControlVO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private SouProject project;
    @ApiModelProperty("当前轮次信息")
    private SouRound currentRound;
    @ApiModelProperty("供应商报价信息")
    private List<ApiSouControlOrderVO> orderInfos;

    public static ApiSouControlVO convertApiVO(SouProject project,
                                               SouRound currentRound,
                                               List<SouVendor> vendorList,
                                               List<SouOrder> orderList,
                                               List<SouFileConfig> fileConfigList,
                                               List<SouOrderFile> orderFileList) {
        ApiSouControlVO vo = new ApiSouControlVO();
        vo.setProject(project);
        vo.setCurrentRound(currentRound);
        vo.setOrderInfos(ApiSouControlOrderVO.convertApiVO(project, currentRound, vendorList, orderList, fileConfigList, orderFileList));
        return vo;
    }

}
