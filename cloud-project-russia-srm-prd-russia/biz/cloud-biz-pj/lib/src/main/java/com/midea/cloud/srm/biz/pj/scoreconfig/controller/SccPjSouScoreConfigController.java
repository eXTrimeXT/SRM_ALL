package com.midea.cloud.srm.biz.pj.scoreconfig.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.biz.pj.scoreconfig.service.ISccPjSouScoreConfigService;
import com.midea.cloud.srm.biz.pj.scoreconfigdetail.service.ISccPjSouScoreConfigDetailService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.scoreconfig.dto.SccPjSouScoreConfigDto;
import com.midea.cloud.srm.model.pj.scoreconfig.entity.SccPjSouScoreConfig;
import com.midea.cloud.srm.model.pj.scoreconfigdetails.entity.SccPjSouScoreConfigDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
/**
 * @description Ѱ
 * @author panmq
 * @date 2023-09-21
 */
@RestController
@Slf4j
@RequestMapping("/sou/scoreConfig")
public class SccPjSouScoreConfigController extends BaseController {

   @Autowired
   private ISccPjSouScoreConfigService iSccPjSouScoreConfigService;

   @Autowired
   private ISccPjSouScoreConfigDetailService iSccPjSouScoreConfigDetailService;

    /**
     * 分页查询
     * @param sccPjSouScoreConfig
     * @return
     */
    @PostMapping("/queryPage")
    public PageInfo<SccPjSouScoreConfig> queryPage(@RequestBody SccPjSouScoreConfig sccPjSouScoreConfig) {
        return iSccPjSouScoreConfigService.queryPage(sccPjSouScoreConfig);
    }

    /**
     * 保存数据
     * @param sccPjSouScoreConfigDto
     * @return
     */
    @PostMapping("/saveScoreConfig")
    public SccPjSouScoreConfigDto saveScoreConfig(@RequestBody SccPjSouScoreConfigDto sccPjSouScoreConfigDto) {
        return iSccPjSouScoreConfigService.saveScoreConfig(sccPjSouScoreConfigDto);
    }

    /**
     * 查询
     * @param scoreConfigId
     */
    @GetMapping("/queryScoreConfig")
    public SccPjSouScoreConfigDto queryScoreConfig(@RequestParam(value = "scoreConfigId")  Long scoreConfigId) {
        return iSccPjSouScoreConfigService.queryScoreConfig(scoreConfigId);
    }

    /**
     * 删除评分项
     * @param configDetailIdList
     */
    @PostMapping("/delScoreConfigDetailBatch")
    public void delScoreConfigDetailBatch(@RequestBody List<Long> configDetailIdList) {
        iSccPjSouScoreConfigDetailService.delScoreConfigDetail(configDetailIdList);
    }

    /**
     * 删除评分项
     * @param configDetailId
     */
    @GetMapping("/delScoreConfigDetail")
    public void delScoreConfigDetail(@RequestParam(value = "configDetailId") Long configDetailId) {
        iSccPjSouScoreConfigDetailService.delScoreConfigDetail(Arrays.asList(configDetailId));
    }

    /**
     * 失效
     * @param scoreConfigIdList
     */
    @PostMapping("/invalidScoreConfigBatch")
    public void invalidScoreConfigBatch(@RequestBody List<Long> scoreConfigIdList) {
        iSccPjSouScoreConfigService.invalidScoreConfig(scoreConfigIdList);
    }

    /**
     * 失效
     * @param scoreConfigId
     */
    @GetMapping("/invalidScoreConfig")
    public void invalidScoreConfig(@RequestParam(value = "scoreConfigId")  Long scoreConfigId) {
        iSccPjSouScoreConfigService.invalidScoreConfig(Arrays.asList(scoreConfigId));
    }

    /**
     * 删除
     * @param scoreConfigIdList
     */
    @PostMapping("/delScoreConfigBatch")
    public void delScoreConfigBatch(@RequestBody List<Long> scoreConfigIdList) {
        iSccPjSouScoreConfigService.delScoreConfig(scoreConfigIdList);
    }

    /**
     * 删除
     * @param scoreConfigId
     */
    @GetMapping("/delScoreConfig")
    public void delScoreConfig(@RequestParam(value = "scoreConfigId")  Long scoreConfigId) {
        iSccPjSouScoreConfigService.delScoreConfig(Arrays.asList(scoreConfigId));
    }

    /**
     * 查询生效的评分模板配置
     * @return
     */
    @GetMapping("/listValidScoreConfig")
    @ApiModelProperty("查询生效的评分模板配置")
    public List<SccPjSouScoreConfig> listValidScoreConfig() {
        return iSccPjSouScoreConfigService.listValidScoreConfig();
    }

    /**
     * 查询评分项
     * @param scoreConfigId
     * @return
     */
    @GetMapping("/listDetail")
    public List<SccPjSouScoreConfigDetail> listDetail(@RequestParam(value = "scoreConfigId") Long scoreConfigId) {
        return iSccPjSouScoreConfigDetailService.listDetail(scoreConfigId);
    }

}

