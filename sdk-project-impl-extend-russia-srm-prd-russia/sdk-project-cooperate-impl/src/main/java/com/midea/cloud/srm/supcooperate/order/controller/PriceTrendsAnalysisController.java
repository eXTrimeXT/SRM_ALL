package com.midea.cloud.srm.supcooperate.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.designplans.dto.PullQueryDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;
import com.midea.cloud.srm.model.supcooperate.entity.SccScOrderPriceTrends;
import com.midea.cloud.srm.model.supcooperate.entity.SccScOrderPriceTrendsLine;
import com.midea.cloud.srm.supcooperate.order.service.SccScOrderPriceTrendsLineService;
import com.midea.cloud.srm.supcooperate.order.service.SccScOrderPriceTrendsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Api(value = "PriceTrendsAnalysisController", tags = {"订单分析"})
@Slf4j
@RestController
@RequestMapping("/sc/price/trends/analysis")
public class PriceTrendsAnalysisController {

    @Resource
    private SccScOrderPriceTrendsService orderPriceTrendsService;

    @Resource
    private SccScOrderPriceTrendsLineService orderPriceTrendsLineService;

    @ApiOperation(value = "订单分析", notes = "订单分析", httpMethod = "POST")
    @PostMapping("/getOrderPriceTrendsList")
    public PageInfo<SccScOrderPriceTrends> getOrderPriceTrendsList(@RequestBody SccScOrderPriceTrends orderPriceTrends) {
        PageHelper.startPage(orderPriceTrends.getPageNum(), orderPriceTrends.getPageSize());
        LambdaQueryWrapper<SccScOrderPriceTrends> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(orderPriceTrends.getAreaCode()), SccScOrderPriceTrends::getAreaCode, orderPriceTrends.getAreaCode());
        qw.like(StringUtils.isNotBlank(orderPriceTrends.getMaterialCode()), SccScOrderPriceTrends::getMaterialCode, orderPriceTrends.getMaterialCode());
        qw.like(StringUtils.isNotBlank(orderPriceTrends.getMaterialName()), SccScOrderPriceTrends::getMaterialName, orderPriceTrends.getMaterialName());
        qw.like(StringUtils.isNotBlank(orderPriceTrends.getMaterialDescribe()), SccScOrderPriceTrends::getMaterialDescribe, orderPriceTrends.getMaterialDescribe());
        qw.like(StringUtils.isNotBlank(orderPriceTrends.getBrand()), SccScOrderPriceTrends::getBrand, orderPriceTrends.getBrand());
        List<SccScOrderPriceTrends> list = orderPriceTrendsService.list(qw);
        return new PageInfo<>(list);
    }

    @ApiOperation(value = "订单分析", notes = "订单分析", httpMethod = "POST")
    @PostMapping("/getOrderPriceTrendsLineList")
    public PageInfo<SccScOrderPriceTrendsLine> getOrderPriceTrendsList(@RequestBody SccScOrderPriceTrendsLine orderPriceTrendsLine) {
        PageHelper.startPage(orderPriceTrendsLine.getPageNum(), orderPriceTrendsLine.getPageSize());
        QueryWrapper<SccScOrderPriceTrendsLine> qw = new QueryWrapper<>();
        qw.like(StringUtils.isNotBlank(orderPriceTrendsLine.getAreaCode()), "AREA_CODE", orderPriceTrendsLine.getAreaCode());
        qw.like(StringUtils.isNotBlank(orderPriceTrendsLine.getMaterialCode()), "MATERIAL_CODE", orderPriceTrendsLine.getMaterialCode());
        qw.like(StringUtils.isNotBlank(orderPriceTrendsLine.getMaterialName()), "MATERIAL_NAME", orderPriceTrendsLine.getMaterialName());
        qw.like(StringUtils.isNotBlank(orderPriceTrendsLine.getMaterialDescribe()), "MATERIAL_DESCRIBE", orderPriceTrendsLine.getMaterialDescribe());
        qw.like(StringUtils.isNotBlank(orderPriceTrendsLine.getBrand()), "BRAND", orderPriceTrendsLine.getBrand());
        qw.eq(StringUtils.isNotBlank(orderPriceTrendsLine.getYm()), "DATE_FORMAT(CREATION_DATE, '%Y-%m')", orderPriceTrendsLine.getYm());
        qw.like(StringUtils.isNotBlank(orderPriceTrendsLine.getOrgName()), "ORG_NAME", orderPriceTrendsLine.getOrgName());
        qw.like(StringUtils.isNotBlank(orderPriceTrendsLine.getSupName()), "SUP_NAME", orderPriceTrendsLine.getSupName());
        List<SccScOrderPriceTrendsLine> list = orderPriceTrendsLineService.list(qw);
        return new PageInfo<>(list);
    }
}
