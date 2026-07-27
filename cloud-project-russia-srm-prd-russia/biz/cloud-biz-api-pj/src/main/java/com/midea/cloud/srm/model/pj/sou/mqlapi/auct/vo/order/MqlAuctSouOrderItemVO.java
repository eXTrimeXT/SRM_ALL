package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.order;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.order.MqlAuctSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.init.MqlAuctSouItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价MQL -
 * PS: 参考 {@link MqlSouOrderItemVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouOrderItemVO extends MqlAuctSouOrderItemDTO {

    @ApiModelProperty("物料需求")
    private MqlAuctSouItemVO souItem;

    /**
     * 竞价实时排名
     * PS: 该字段不存储到数据库表中，因为当业务是指定供应商发起多轮(有效价格)时，存储在数据库的排名字段是不准确的
     */
    @ApiModelProperty("竞价实时排名")
    private Integer auctRanking;

}
