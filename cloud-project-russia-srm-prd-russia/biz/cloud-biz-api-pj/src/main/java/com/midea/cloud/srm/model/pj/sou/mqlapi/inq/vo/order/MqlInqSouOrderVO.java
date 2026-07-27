package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.vo.order;

import com.midea.cloud.srm.model.pj.sou.mqlapi.inq.vo.order.MqlInqSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderFileVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlInqSouOrderVO extends SouOrder {

    @ApiModelProperty("物料需求")
    private List<MqlInqSouOrderItemVO> orderItemList;
    @ApiModelProperty("报价附件")
    private List<MqlSouOrderFileVO> orderFileList;

}