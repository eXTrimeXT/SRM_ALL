package com.midea.cloud.srm.sou.inq.ext.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqOrderItemHisQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqOrderItemHisQueryVO;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouOrderQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 长城 - 询比价 - 报价
 * @author huangbf3
 */
@RestController
@RequestMapping("/npm/buyer/inq/order")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtWebInqSouOrderForBuyerController {

    @Autowired
    private ExtInqSouOrderQueryService extInqSouOrderQueryService;

    @ApiOperation("供应商历史报价列表查询")
    @PostMapping("/listVendorOrderHis")
    public PageInfo<ExtInqOrderItemHisQueryVO> listVendorOrderHis(@RequestBody ExtInqOrderItemHisQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsBuyer();
        queryParam.setForBuyer(true);

        return new PageInfo<>(extInqSouOrderQueryService.listVendorOrderHis(queryParam));
    }

}
