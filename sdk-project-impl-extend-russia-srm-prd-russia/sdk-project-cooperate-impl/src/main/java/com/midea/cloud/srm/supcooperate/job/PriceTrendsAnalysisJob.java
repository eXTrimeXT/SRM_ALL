package com.midea.cloud.srm.supcooperate.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.supcooperate.entity.SccScOrderPriceTrends;
import com.midea.cloud.srm.model.supcooperate.entity.SccScOrderPriceTrendsLine;
import com.midea.cloud.srm.supcooperate.ext.order.mapper.ExtOrderTodoMapper;
import com.midea.cloud.srm.supcooperate.order.service.SccScOrderPriceTrendsLineService;
import com.midea.cloud.srm.supcooperate.order.service.SccScOrderPriceTrendsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Job("priceTrendsAnalysisJob")
public class PriceTrendsAnalysisJob implements ExecuteableJob {

    @Resource
    private ExtOrderTodoMapper extOrderTodoMapper;

    @Resource
    private SccScOrderPriceTrendsService orderPriceTrendsService;

    @Resource
    private SccScOrderPriceTrendsLineService orderPriceTrendsLineService;

    @Resource
    private BaseClient baseClient;

    private static final int BATCH = 500;
    private static final int NUM_MONTH = 11;

    /**
     * 价格趋势分析定时任务
     * @param params 参数
     * @return 返回值
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        try {
            orderPriceTrendsService.remove(new LambdaQueryWrapper<SccScOrderPriceTrends>().ne(SccScOrderPriceTrends::getPriceTrendsId, -1));
            orderPriceTrendsLineService.remove(new LambdaQueryWrapper<SccScOrderPriceTrendsLine>().ne(SccScOrderPriceTrendsLine::getPriceTrendsLineId, -1));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            List<SccScOrderPriceTrends> optList = new ArrayList<>();
            Set<String> maSet = new HashSet<>();
            for (int i = 0; i <= NUM_MONTH; i++) {
                LocalDate ld = LocalDate.now().minusMonths(i);
                String paDate = ld.format(formatter);
                log.info("时间" + paDate);
                List<SccScOrderPriceTrendsLine> optLineList = extOrderTodoMapper.getMaterialListByPa(paDate);
                if (CollectionUtils.isNotEmpty(optLineList)) {
                    orderPriceTrendsLineService.saveBatch(optLineList, BATCH);
                    int finalI = i;
                    optLineList.forEach(minItem -> {
                        if (maSet.add(minItem.getMaterialArea())) {
                            SccScOrderPriceTrends pt = new SccScOrderPriceTrends();
                            pt.setMaterialId(minItem.getMaterialId());
                            pt.setMaterialCode(minItem.getMaterialCode());
                            pt.setMaterialName(minItem.getMaterialName());
                            pt.setMaterialDescribe(minItem.getMaterialDescribe());
                            pt.setBrand(minItem.getBrand());
                            pt.setAreaCode(minItem.getAreaCode());
                            pt.setMaterialArea(minItem.getMaterialArea());
                            dealPrice(pt, minItem.getNoTaxPrice(), finalI);
                            optList.add(pt);
                        } else {
                            optList.forEach(c -> {
                                if (minItem.getMaterialArea().equals(c.getMaterialArea())) {
                                    dealPrice(c, minItem.getNoTaxPrice(), finalI);
                                }
                            });
                        }
                    });
                }
            }
            optList.forEach(e -> e.setMinPrice(dealMinPrice(e)));
            orderPriceTrendsService.saveBatch(optList, BATCH);
        } catch (Exception e) {
            log.info("------------" + JSONObject.toJSONString(e));
        }
        return BaseResult.buildSuccess("调用成功");
    }

    private static void dealPrice(SccScOrderPriceTrends pt, BigDecimal minPrice, int num) {
        switch (num) {
            case 0:
                pt.setCurrentMonthPrice(minPrice);
                return;
            case 1:
                pt.setOnePrice(minPrice);
                return;
            case 2:
                pt.setTwoPrice(minPrice);
                return;
            case 3:
                pt.setThreePrice(minPrice);
                return;
            case 4:
                pt.setFourPrice(minPrice);
                return;
            case 5:
                pt.setFivePrice(minPrice);
                return;
            case 6:
                pt.setSixPrice(minPrice);
                return;
            case 7:
                pt.setSevenPrice(minPrice);
                return;
            case 8:
                pt.setEightPrice(minPrice);
                return;
            case 9:
                pt.setNinePrice(minPrice);
                return;
            case 10:
                pt.setTenPrice(minPrice);
                return;
            case 11:
                pt.setElevenPrice(minPrice);
                return;
            default:
        }
    }

    public static String dealArea(List<DictItemDTO> areaList, String areaCode) {
        for (DictItemDTO e : areaList) {
            if (e.getDictItemCode().equals(areaCode)) {
                return e.getDictItemName();
            }
        }
        return "";
    }

    public static BigDecimal dealMinPrice(SccScOrderPriceTrends pt) {
        List<BigDecimal> list = new ArrayList<>();
        list.add(dealBigDecimal(pt.getCurrentMonthPrice()));
        list.add(dealBigDecimal(pt.getOnePrice()));
        list.add(dealBigDecimal(pt.getTwoPrice()));
        list.add(dealBigDecimal(pt.getThreePrice()));
        list.add(dealBigDecimal(pt.getFourPrice()));
        list.add(dealBigDecimal(pt.getFivePrice()));
        list.add(dealBigDecimal(pt.getSixPrice()));
        list.add(dealBigDecimal(pt.getSevenPrice()));
        list.add(dealBigDecimal(pt.getEightPrice()));
        list.add(dealBigDecimal(pt.getNinePrice()));
        list.add(dealBigDecimal(pt.getTenPrice()));
        list.add(dealBigDecimal(pt.getElevenPrice()));
        List<BigDecimal> bdList = list.stream().filter(e -> new BigDecimal(0).compareTo(e) < 0).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(bdList)) {
            boolean bk = bdList.stream().min(BigDecimal::compareTo).isPresent();
            if (bk) {
                BigDecimal min = bdList.stream().min(BigDecimal::compareTo).get();
                log.info("----------------" + min);
                if (new BigDecimal(0).compareTo(min) < 0) {
                    return min;
                }
            }
        }
        return null;
    }

    public static BigDecimal dealBigDecimal(BigDecimal pt) {
        return pt == null ? new BigDecimal(0) : pt;
    }
}
