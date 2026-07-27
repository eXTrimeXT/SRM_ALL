package com.midea.cloud.srm.supcooperate.pjquicksearch.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.perf.pjquicksearch.dto.PerfScoreItemsQueryDto;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItems;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.WarehousingReturnDetail;
import com.midea.cloud.srm.supcooperate.order.service.IWarehousingReturnDetailService;
import com.midea.cloud.srm.supcooperate.pjquicksearch.mapper.PjQuickSearchMapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * @author ex_liuxy46
 */
@Slf4j
@RestController
@RequestMapping("/sc-anon/pjQuickSearch")
public class PjQuickSearchController {

    @Autowired
    private PjQuickSearchMapper pjQuickSearchMapper;

    /**
     * 分页查询全部组织
     */
    @ApiOperation(value = "绩效项目-分页查询入库退货明细的供应商信息", notes = "绩效项目-分页查询入库退货明细的供应商信息")
    @PostMapping("/listPageWarehousingReturnDetail")
    public PageInfo<WarehousingReturnDetail> listPageWarehousingReturnDetail(@RequestBody PerfScoreItemsQueryDto perfScoreItems) {
        PageUtil.startPage(perfScoreItems.getPageNum(), perfScoreItems.getPageSize());
        perfScoreItems.setPerStartMonth(perfScoreItems.getPerStartMonth().withDayOfMonth(1));
        return new PageInfo<WarehousingReturnDetail>(pjQuickSearchMapper.listForPerf(perfScoreItems));
    }
}
