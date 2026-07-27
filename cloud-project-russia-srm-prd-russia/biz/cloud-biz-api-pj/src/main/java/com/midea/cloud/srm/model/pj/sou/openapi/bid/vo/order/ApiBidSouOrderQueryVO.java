package com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.order;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 招投标openAPI - 供应商端列表查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouOrderQueryVO extends ApiSouOrderQueryVO {

    /** @see BidSouProject#getBondEndTime */
    @ApiModelProperty("保证金提交截止时间")
    private Date bondEndTime;

    /** @see BidSouProcessConfig#getBondManagement */
    @ApiModelProperty("是否有保证金节点")
    private Enable hasBondNode;

    @SuppressWarnings("rawtypes")
    public static List<ApiBidSouOrderQueryVO> convertBidVO(List<ApiSouOrderQueryVO> souVOList) {
        if (souVOList.isEmpty()) { return Collections.emptyList(); }
        List<ApiBidSouOrderQueryVO> bidVOList;
        if (souVOList instanceof Page) {
            bidVOList = new Page<>();
            ((Page)bidVOList).setTotal(((Page)souVOList).getTotal());
            ((Page)bidVOList).setPageSize(((Page)souVOList).getPageSize());
            ((Page)bidVOList).setPageNum(((Page)souVOList).getPageNum());
        } else {
            bidVOList = new ArrayList<>(souVOList.size());
        }
        souVOList.forEach(vo -> bidVOList.add(SouObjectXUtil.convertTargetObj(vo, ApiBidSouOrderQueryVO.class)));
        return bidVOList;
    }

}
