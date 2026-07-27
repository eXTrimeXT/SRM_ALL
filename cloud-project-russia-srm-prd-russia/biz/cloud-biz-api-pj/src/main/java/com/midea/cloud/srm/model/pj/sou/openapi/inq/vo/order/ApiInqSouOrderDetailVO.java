package com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.order;

import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init.ApiInqSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.order.ApiInqSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderFileVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 简易询价openAPI - 供应商报价信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouOrderDetailVO extends BaseObjectX {

    @ApiModelProperty("寻源立项信息")
    private ApiInqSouInitDetailVO initInfo;
    @ApiModelProperty("指定轮次报价单")
    private SouOrder order;
    @ApiModelProperty("物料需求")
    private List<ApiInqSouOrderItemVO> itemList;
    @ApiModelProperty("报价附件")
    private List<ApiSouOrderFileVO> orderFileList;

}
