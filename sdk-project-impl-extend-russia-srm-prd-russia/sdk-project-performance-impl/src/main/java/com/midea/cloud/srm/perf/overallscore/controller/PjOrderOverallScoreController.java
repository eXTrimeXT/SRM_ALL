package com.midea.cloud.srm.perf.overallscore.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.perf.overallscore.dto.PjPerfOverallScore;
import com.midea.cloud.srm.model.perf.scoring.PerfOverallScore;
import com.midea.cloud.srm.perf.overallscore.service.IPjPerfOverallScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <pre>
 * 订单化绩效评分 前端控制器</pre>
 * @author huangbf3
 * <pre>
 */
@Api(value = "PjOrderScoreManScoringV1Controller", tags = {"订单化绩效查询"})
@RestController
@RequestMapping("/pj/scoring/perfOverallScore")
@Slf4j
public class PjOrderOverallScoreController extends BaseController {

    @Resource
    private IPjPerfOverallScoreService iPerfOverallScoreService;

    /**
     * 分页查询
     *
     * @param perfOverallScore
     * @return
     */
    @PostMapping("/listPerfOverallScorePage")
    @ApiOperation(value = "分页查询综合绩效得分信息", notes = "分页查询综合绩效得分信息")
    public PageInfo<PjPerfOverallScore> listPage(@RequestBody PjPerfOverallScore perfOverallScore) {
        PageUtil.startPage(perfOverallScore.getPageNum(), perfOverallScore.getPageSize());
        return new PageInfo<PjPerfOverallScore>(iPerfOverallScoreService.listPage(perfOverallScore));
    }

    /**
     * 查询明细
     *
     * @param overallScoreId
     * @return
     * @throws BaseException
     */
    @GetMapping("/findOverallScorelById")
    @ApiOperation(value = "根据绩效指标绩效得分主表ID获取绩效指标绩效得分主表和子表信息", notes = "根据绩效指标绩效得分主表ID获取绩效指标绩效得分主表和子表信息")
    public PerfOverallScore findOverallScorelById(Long overallScoreId) throws BaseException {
        PerfOverallScore overallScore = new PerfOverallScore();
        PerfOverallScore queryOverallScore = new PerfOverallScore();
        queryOverallScore.setOverallScoreId(overallScoreId);
        List<PerfOverallScore> overallScoreList = iPerfOverallScoreService.findOverallScoreAndSonList(queryOverallScore);
        if (CollectionUtils.isNotEmpty(overallScoreList) && null != overallScoreList.get(0)) {
            overallScore = overallScoreList.get(0);
        }
        return overallScore;
    }

}
