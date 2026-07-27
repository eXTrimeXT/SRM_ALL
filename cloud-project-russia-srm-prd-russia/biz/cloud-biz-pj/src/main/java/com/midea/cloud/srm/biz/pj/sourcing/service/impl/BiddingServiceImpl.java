package com.midea.cloud.srm.biz.pj.sourcing.service.impl;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.biz.pj.sourcing.dto.BiddingQueryDTO;
import com.midea.cloud.srm.biz.pj.sourcing.mapper.BiddingMapper;
import com.midea.cloud.srm.biz.pj.sourcing.service.IBiddingService;
import com.midea.cloud.srm.model.pj.sou.bidding.entity.Bidding;
import com.midea.cloud.srm.model.pm.pr.requirement.dto.RequirementHeadQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 *  竞价管理 服务实现类
 * </pre>
 *
 * @author yipeng@meiCloud.com
 * @version 1.00.00
 */
@Service
public class BiddingServiceImpl extends BaseServiceImpl<BiddingMapper, Bidding> implements IBiddingService {

    @Autowired
    private QlService qlService;


    @Override
    public PageInfo<Bidding> listPage(BiddingQueryDTO biddingQueryDTO) {
        QlQueryWrapper qw = QlWrappers.query(Bidding.class, "bid");

        Page<Bidding> page = qlService.
                queryPageByWrapper(qw, (long) biddingQueryDTO.getPageNum(), (long) biddingQueryDTO.getPageSize(), Bidding.class);

        PageInfo<Bidding> pageResult = new PageInfo<>();
        pageResult.setList(page.getRecords());
        pageResult.setPageNum(page.getPageNum());
        pageResult.setPageSize(page.getPageSize());
        pageResult.setTotal(page.getTotal());
        pageResult.setPages(page.getPageCount());
        return pageResult;
    }

}
