package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.editorder;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItemHis;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 寻源核心 - 供应商报价实体承载
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/05
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SouOrderEditPO extends BaseObjectX {

    @ApiModelProperty("报价头信息")
    protected SouOrder souOrder;
    @ApiModelProperty("报价明细信息")
    protected List<SouOrderItem> orderItemList = new ArrayList<>(16);
    @ApiModelProperty("报价附件")
    protected List<SouOrderFile> orderFileList;
    @ApiModelProperty("报价历史信息")
    protected List<SouOrderItemHis> hisPriceList = new ArrayList<>(16);

}