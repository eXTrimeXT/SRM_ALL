package com.midea.cloud.srm.perf.orderscoreman.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.perf.orderscoreman.entity.PjOrderScoreManScoringV1;
import com.midea.cloud.srm.perf.orderscoreman.service.IPjOrderScoreManScoringV1Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 订单化绩效评分 前端控制器</pre>
 * @author huangbf3
 * <pre>
 */
@Api(value = "PjOrderScoreManScoringV1Controller", tags = {"订单化绩效评分"})
@RestController
@RequestMapping("/pj/perf/score-man-scoring-v1")
@Slf4j
public class PjOrderScoreManScoringV1Controller extends BaseController {

    @Autowired
    private IPjOrderScoreManScoringV1Service pjOrderScoreManScoringV1Service;

    @ApiOperation(value = "分页查询评分人绩效评分信息", notes = "分页查询评分人绩效评分信息", httpMethod = "POST")
    @PostMapping("/listScoreManScoringPage")
    public PageInfo<PjOrderScoreManScoringV1> listScoreManScoringPage(@RequestBody PjOrderScoreManScoringV1 scoreManScoringV1) {
        PageUtil.startPage(scoreManScoringV1.getPageNum(), scoreManScoringV1.getPageSize());
        return new PageInfo<PjOrderScoreManScoringV1>(pjOrderScoreManScoringV1Service.listScoreManScoringPage(scoreManScoringV1));
    }

    @ApiOperation(value = "保存评分人绩效评分集合（评分人绩效评分提交）", notes = "保存评分人绩效评分集合（评分人绩效评分提交）", httpMethod = "POST")
    @PostMapping("/saveScoreManScoring")
    public String saveScoreManScoring(@RequestBody List<PjOrderScoreManScoringV1> scoreManScoringV1List){
        // 先更新评分人绩效评分 scc_perf_score_man_scoring_v1 表
        return pjOrderScoreManScoringV1Service.saveScoreManScoring(scoreManScoringV1List);
    }

    /**
     * 自定义导入文件
     * @param file
     */
    @ApiOperation(value = "自定义导入文件", notes = "自定义导入文件")
    @RequestMapping("/importScoreManScoringV1Excel")
    public Map<String, Object> importScoreManScoringV1Excel(@RequestParam("file") MultipartFile file, Fileupload fileupload) throws Exception {
        return pjOrderScoreManScoringV1Service.importScoreManScoringV1Excel(file, fileupload);
    }

}
