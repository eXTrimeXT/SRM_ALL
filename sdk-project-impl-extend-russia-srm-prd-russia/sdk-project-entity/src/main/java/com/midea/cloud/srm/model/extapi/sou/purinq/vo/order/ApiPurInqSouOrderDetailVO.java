package com.midea.cloud.srm.model.extapi.sou.purinq.vo.order;

import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouInitDetailVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderFileVO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiPurInqSouOrderDetailVO extends BaseObjectX {

    @ApiModelProperty("寻源立项信息")
    private ApiPurInqSouInitDetailVO initInfo;
    @ApiModelProperty("指定轮次报价单")
    private ExtPurInqSouOrderVO order;
    @ApiModelProperty("物料需求")
    private List<ApiPurInqSouOrderItemVO> itemList;
    @ApiModelProperty("报价附件")
    private List<ApiSouOrderFileVO> orderFileList;

}
