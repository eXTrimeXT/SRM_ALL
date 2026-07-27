package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.order;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProject;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 招投标MQL - 供应商端列表查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouOrderQueryVO extends MqlSouOrderQueryVO {

    /** @see BidSouProject#getBondEndTime */
    @ApiModelProperty("保证金提交截止时间")
    private Date bondEndTime;

    /** @see BidSouProcessConfig#getBondManagement */
    @ApiModelProperty("是否有保证金节点")
    private Enable hasBondNode;

    @SuppressWarnings("rawtypes")
    public static List<MqlBidSouOrderQueryVO> convertBidVO(List<MqlSouOrderQueryVO> souVOList) {
        if (souVOList.isEmpty()) { return Collections.emptyList(); }
        List<MqlBidSouOrderQueryVO> bidVOList;
        if (souVOList instanceof Page) {
            bidVOList = new Page<>();
            ((Page)bidVOList).setTotal(((Page)souVOList).getTotal());
            ((Page)bidVOList).setPageSize(((Page)souVOList).getPageSize());
            ((Page)bidVOList).setPageNum(((Page)souVOList).getPageNum());
        } else {
            bidVOList = new ArrayList<>(souVOList.size());
        }
        souVOList.forEach(vo -> bidVOList.add(SouObjectXUtil.convertTargetObj(vo, MqlBidSouOrderQueryVO.class)));
        return bidVOList;
    }

}
