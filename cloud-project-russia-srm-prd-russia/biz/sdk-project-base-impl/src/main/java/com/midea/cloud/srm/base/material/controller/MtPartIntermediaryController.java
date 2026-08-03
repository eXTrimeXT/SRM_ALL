package com.midea.cloud.srm.base.material.controller;

import com.midea.cloud.srm.base.material.service.MtPartIntermediaryService;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/external/mtPartIntermediary")
public class MtPartIntermediaryController {
    @Autowired
    private MtPartIntermediaryService mtPartIntermediaryService;

    @PostMapping("/pull")
    public void pullData(@RequestBody MaterialParam materialParam) {
        mtPartIntermediaryService.pullData(materialParam);
    }
}
