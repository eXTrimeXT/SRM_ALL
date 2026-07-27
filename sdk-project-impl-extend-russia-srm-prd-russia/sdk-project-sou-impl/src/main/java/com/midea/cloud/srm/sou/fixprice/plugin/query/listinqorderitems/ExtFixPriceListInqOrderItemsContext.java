package com.midea.cloud.srm.sou.fixprice.plugin.query.listinqorderitems;

import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceInqOrderItemsQueryDTO;
import com.midea.cloud.srm.model.sou.fixprice.vo.ExtFixPriceInqOrderItemsQueryVO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtFixPriceListInqOrderItemsContext extends BaseObjectX {

    @ApiModelProperty("查询条件")
    private ExtFixPriceInqOrderItemsQueryDTO queryParam;

    @ApiModelProperty("查询结果")
    private List<ExtFixPriceInqOrderItemsQueryVO> resultList;

    public ExtFixPriceListInqOrderItemsContext(ExtFixPriceInqOrderItemsQueryDTO queryParam) {
        this.queryParam = queryParam;
    }

}
