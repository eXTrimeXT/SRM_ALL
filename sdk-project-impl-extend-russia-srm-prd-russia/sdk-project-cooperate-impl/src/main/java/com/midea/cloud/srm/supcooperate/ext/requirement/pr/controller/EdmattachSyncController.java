package com.midea.cloud.srm.supcooperate.ext.requirement.pr.controller;

import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.EdmattachSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/5
 */
@RestController
@Slf4j
@RequestMapping("/edm/attach")
public class EdmattachSyncController extends BaseController {

    @Autowired
    private EdmattachSyncService edmattachSyncService;

    /**
     * 同步EDM附件信息
     * @param param
     */
    @PostMapping("/syncAttach")
    public void syncAttach(@RequestBody Map<String, Object> param) {
        edmattachSyncService.syncAttach(param);
    }
}
