package com.midea.cloud.srm.sou.designplans.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.sou.designplans.dto.ScheduleDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChPaaAdjust;
import com.midea.cloud.srm.model.sou.designplans.enums.DesignPlanEnums;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.constants.SouConstant;
import com.midea.cloud.srm.sou.designplans.mapper.PaaAdjustMapper;
import com.midea.cloud.srm.sou.designplans.service.LedgerService;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceLineMapper;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ex_liuxy46
 */
@Api(value = "ScheduleController", tags = {"集采进度"})
@Slf4j
@RestController
@RequestMapping("/design/plan/schedule")
public class ScheduleController {

    @Resource
    private LedgerService ledgerService;

    @Resource
    private ExtFixPriceLineMapper fixPriceLineMapper;

    @Resource
    private PaaAdjustMapper paaAdjustMapper;

    @ApiOperation(value = "获取集采进度列表", notes = "获取集采进度列表")
    @PostMapping("/dp/schedule/getSchedulePageList")
    public PageInfo<ScheduleDto> getChLedgerPageList(@RequestBody ScheduleDto schedule) {
        PageUtil.startPage(schedule.getPageNum(), schedule.getPageSize());
        List<ScheduleDto> list =ledgerService.getScheduleList(schedule);
        for (ScheduleDto e : list) {
            //主项目
            e.setMainPlanStatus(DesignPlanEnums.getDesignPlanEnumsName(e.getMainPlanStatus()));
            if (SouConstant.DRAFT.equals(e.getMainPlanStatus())) {
                e.setMainIsXbj("否");
            } else {
                e.setMainIsXbj("是");
            }
            //todo 定厂定价申请待确认
            LambdaQueryWrapper<ExtFixPriceLine> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ExtFixPriceLine::getItemCode, e.getProjectName());
            fixPriceLineMapper.selectCount(null);
            //询比价调价
            LambdaQueryWrapper<SccSouChPaaAdjust> paaAdjustQueryWrapper = new LambdaQueryWrapper<>();
            paaAdjustQueryWrapper.eq(SccSouChPaaAdjust::getJcId, e.getLedgerId());
            List<SccSouChPaaAdjust> adList = paaAdjustMapper.selectList(paaAdjustQueryWrapper);
            Map<String, Long> map = adList.stream().filter(item -> StringUtils.isNoneBlank(item.getAdjustType())).collect(Collectors.groupingBy(SccSouChPaaAdjust::getAdjustType, Collectors.counting()));
            if(MapUtil.isNotEmpty(map)){
                if (ObjectUtil.isNotEmpty(map.get(NumConstant.ONE_STR)) && map.get(NumConstant.ONE_STR) > 0) {
                    e.setXIsXbj("是");
                    e.setXIsSq("是");
                    e.setXNum(map.get(NumConstant.ONE_STR));
                } else {
                    e.setXIsXbj("否");
                    e.setXIsSq("否");
                    e.setXNum(0L);
                }
                //市场行情调价
                if (ObjectUtil.isNotEmpty(map.get(NumConstant.TWO_STR)) && map.get(NumConstant.TWO_STR)> 0) {
                    e.setSIsSq("是");
                    e.setSNum(map.get(NumConstant.TWO_STR));
                } else {
                    e.setSIsSq("否");
                    e.setSNum(0L);
                }
            }
        }
        return new PageInfo<>(list);
    }

    public static void main(String[] args) {
        log.info(DesignPlanEnums.getDesignPlanEnumsName("REJECT"));

    }
}
