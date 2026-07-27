package com.midea.cloud.srm.biz.pj.supplier.change;

import com.midea.cloud.srm.model.supplier.change.dto.ChangeInfoDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiewx16
 * @date 2023/4/20
 * @apiNote
 */
@Slf4j
@RestController
@RequestMapping("/change/infoChange")
public class InfoChangeController {
    /**
     * 供应商发起-提交公司信息变更数据
     *
     * @param changeInfo
     */
    @ApiOperation(value = "供应商发起-提交公司信息变更数据", notes = "供应商发起-提交公司信息变更数据")
    @PostMapping("/test")
    public void vendorSubmitted(@RequestBody ChangeInfoDTO changeInfo) {
        log.info("test");
    }
}
