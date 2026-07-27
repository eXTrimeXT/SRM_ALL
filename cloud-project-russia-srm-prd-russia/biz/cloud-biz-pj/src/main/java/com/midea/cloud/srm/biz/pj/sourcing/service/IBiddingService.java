package com.midea.cloud.srm.biz.pj.sourcing.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.biz.pj.sourcing.dto.BiddingQueryDTO;
import com.midea.cloud.srm.model.pj.sou.bidding.entity.Bidding;

/**
 * <pre>
 *  竞价管理 服务类
 * </pre>
 *
 * @author yipeng@meiCloud.com
 * @version 1.00.00
 */
public interface IBiddingService extends BaseService<Bidding> {
    /**
     * 备注
     * @param biddingQueryDTO
     * @return
     */
    PageInfo<Bidding> listPage(BiddingQueryDTO biddingQueryDTO);

}
