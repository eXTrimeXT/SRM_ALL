package com.midea.cloud.srm.biz.pj.sourcepubconfig.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.biz.pj.sourcepubconfig.service.ISccPjSourcePubconfigService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.sourcepubconfig.dto.SccPjSourcePubconfigDto;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author huangbf3
 */
@RestController
@Slf4j
@RequestMapping("/source/pubconfig")
public class SccPjSourcePubconfigController extends BaseController {

    @Autowired
    private ISccPjSourcePubconfigService iSccPjSourcePubconfigService;

    /**
     * 保存或提交接口
     * @param sccPjSourcePubconfigDto
     * @return
     */
    @PostMapping("/savePubconfig")
    public SccPjSourcePubconfig savePubconfig(@RequestBody SccPjSourcePubconfigDto sccPjSourcePubconfigDto) {
        return iSccPjSourcePubconfigService.savePubconfig(sccPjSourcePubconfigDto.getSourcePubconfig(), sccPjSourcePubconfigDto.getType());
    }

    /**
     * 批量删除接口
     * @param pubconfigIdList
     */
    @PostMapping("/delPubconfig")
    public void delPubconfig(@RequestBody List<Long> pubconfigIdList) {
        iSccPjSourcePubconfigService.delPubconfig(pubconfigIdList);
    }

    /**
     * 单个删除接口
     * @param pubconfigId
     */
    @GetMapping("/delPubconfigSingle")
    public void delPubconfigSingle(@RequestParam("pubconfigId") Long pubconfigId) {
        iSccPjSourcePubconfigService.delPubconfig(Arrays.asList(pubconfigId));
    }

    /**
     * 批量失效接口
     * @param pubconfigIdList
     */
    @PostMapping("/invalidPubconfig")
    public void invalidPubconfig(@RequestBody List<Long> pubconfigIdList) {
        iSccPjSourcePubconfigService.invalidPubconfig(pubconfigIdList);
    }

    /**
     * 单个失效接口
     * @param pubconfigId
     */
    @GetMapping("/invalidPubconfigSingle")
    public void invalidPubconfigSingle(@RequestParam("pubconfigId") Long pubconfigId) {
        iSccPjSourcePubconfigService.invalidPubconfig(Arrays.asList(pubconfigId));
    }

    /**
     * 批量生效接口
     * @param pubconfigIdList
     */
    @PostMapping("/validPubconfig")
    public void validPubconfig(@RequestBody List<Long> pubconfigIdList) {
        iSccPjSourcePubconfigService.validPubconfig(pubconfigIdList);
    }

    /**
     * 单个生效效接口
     * @param pubconfigId
     */
    @GetMapping("/validPubconfigSingle")
    public void validPubconfigSingle(@RequestParam("pubconfigId") Long pubconfigId) {
        iSccPjSourcePubconfigService.validPubconfig(Arrays.asList(pubconfigId));
    }

    /**
     * 分页查询接口
     * @param sourcePubconfig
     */
    @PostMapping("/queryPage")
    public PageInfo<SccPjSourcePubconfig> queryPage(@RequestBody SccPjSourcePubconfig sourcePubconfig) {
        return iSccPjSourcePubconfigService.queryPage(sourcePubconfig);
    }

    /**
     * 查询详情
     * @param pubconfigId
     */
    @GetMapping("/queryPubconfig")
    public SccPjSourcePubconfig queryPubconfig(@RequestParam("pubconfigId") Long pubconfigId) {
        return iSccPjSourcePubconfigService.queryPubconfig(pubconfigId);
    }
}
