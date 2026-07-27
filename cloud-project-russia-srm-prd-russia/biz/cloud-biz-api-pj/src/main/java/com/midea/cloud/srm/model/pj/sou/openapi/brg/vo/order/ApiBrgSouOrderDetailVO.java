package com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.order;

import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init.ApiBrgSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.order.ApiBrgSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderFileVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 项目式询价openAPI - 报价详情
 * PS: 参考 {@link ApiSouOrderDetailVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouOrderDetailVO extends BaseObjectX {

    @ApiModelProperty("寻源立项信息")
    private ApiBrgSouInitDetailVO initInfo;
    @ApiModelProperty("指定轮次报价单")
    private SouOrder order;
    @ApiModelProperty("物料需求")
    private List<ApiBrgSouOrderItemVO> itemList;
    @ApiModelProperty("报价附件")
    private List<ApiSouOrderFileVO> orderFileList;

}
