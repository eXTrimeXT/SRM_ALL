package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.hall;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouCurrency;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItem;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItemHis;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.hall.CompSouHallOrderItemHisVO;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.hall.CompSouHallOrderItemPriceTrendVO;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.hall.CompSouHallOrderItemRankVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 竞价 - 竞价大厅比价信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("竞价大厅比价信息")
public class CompSouHallOrderItemPriceVO extends BaseObjectX {

    @ApiModelProperty("最新实时报价排名")
    private List<CompSouHallOrderItemRankVO> rankList;

    @ApiModelProperty("价格走势")
    private List<CompSouHallOrderItemPriceTrendVO> trendList;

    @ApiModelProperty("报价列表(供应商在指定轮次的历史报价记录)")
    private List<CompSouHallOrderItemHisVO> hisList;

    /**
     * 便捷方法
     */
    public static CompSouHallOrderItemPriceVO convert(SouProject souProject,
                                                      SouItem souItem,
                                                      CompSouItem compSouItem,
                                                      CompSouCurrency currency,
                                                      List<SouVendor> vendorList,
                                                      List<SouOrderItem> souOrderItemList,
                                                      List<CompSouOrderItemHis> orderItemHisList) {
        CompSouHallOrderItemPriceVO vo = new CompSouHallOrderItemPriceVO();

        vo.rankList = CompSouHallOrderItemRankVO.convert(souProject, souItem, compSouItem, souOrderItemList, vendorList, currency);
        vo.trendList = CompSouHallOrderItemPriceTrendVO.convert(orderItemHisList, vendorList);
        vo.hisList = CompSouHallOrderItemHisVO.convert(souItem, orderItemHisList, vendorList, currency);

        return vo;
    }

}
