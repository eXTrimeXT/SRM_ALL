package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.order;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouProject;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouVendorBond;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums.AuctSouVendorBondStatus;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderQueryVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 竞价MQL - 报价单列表查询结果
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouOrderQueryVO extends MqlSouOrderQueryVO {

    /** @see AuctSouProject#getBondEndTime */
    @ApiModelProperty("保证金提交截止时间")
    private Date bondEndTime;

    /** @see AuctSouProcessConfig#getBondManagement */
    @ApiModelProperty("是否有保证金节点")
    private Enable hasBondNode;

    /** @see AuctSouVendorBond#getBondStatus */
    @ApiModelProperty("保证金缴纳状态")
    private AuctSouVendorBondStatus bondStatus;

}
