package com.midea.cloud.srm.sou.purinq.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ExtPurInqSouVendorDelDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ExtPurInqSouVendorQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouVendorDel;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouInitEventService;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouInitQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: for srm 长城 - 询比价 - 立项
 *
 * @author srm
 * @date 2024-05-18
 */
@RestController
@RequestMapping("/npm/buyer/ext_pur_inq/init")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtWebPurInqSouInitForBuyerController {

    @Autowired
    private ExtPurInqSouInitQueryService extPurInqSouInitQueryService;
    @Autowired
    private ExtPurInqSouInitEventService extPurInqSouInitEventService;

    @ApiOperation("删除新增供应商(仅限立项阶段-逻辑删)")
    @PostMapping("/removeVendor")
    public void removeVendor(@RequestBody ExtPurInqSouVendorDelDTO param) {
        extPurInqSouInitEventService.removeVendor(param);
    }

    @ApiOperation("查看被删除的邀请供应商")
    @PostMapping("/queryVendorDel")
    public PageInfo<ExtPurInqSouVendorDel> queryVendorDel(@RequestBody ExtPurInqSouVendorQueryDTO queryParam) {
        return new PageInfo<>(extPurInqSouInitQueryService.queryVendorDel(queryParam));
    }

}
