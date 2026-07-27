package com.midea.cloud.srm.biz.pj.changchengapi.material.controller;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.biz.pj.changchengapi.material.service.IMaterialService;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialParam;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialResultDto;
import com.midea.cloud.srm.model.pj.changchengapi.yangguan.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 非生产物料接口
 * @author huangbf3
 */
@Slf4j
@RestController
@RequestMapping("/external/material")
public class MaterialController {
    @Autowired
    private IMaterialService iMaterialService;

    /**
     * 非生产物料接口查询
     */
    @PostMapping("/page")
    public ResultDTO<MaterialResultDto> page(@RequestBody MaterialParam materialParam) {
        return iMaterialService.page(materialParam);
    }
}