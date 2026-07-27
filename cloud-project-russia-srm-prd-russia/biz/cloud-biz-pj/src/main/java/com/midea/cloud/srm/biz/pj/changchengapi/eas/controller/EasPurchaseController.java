package com.midea.cloud.srm.biz.pj.changchengapi.eas.controller;


import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasPurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
@Slf4j
@RestController
@RequestMapping("/easPurchase")
public class EasPurchaseController {

    @Autowired
    private EasPurchaseService easPurchaseService;

    /**
     * 推送EAS送货单
     */
    @PostMapping("/pushDeliveryNote")
    public void pushDeliveryNote(@RequestBody Object data) throws Exception {
        easPurchaseService.pushDeliveryNote(data);
    }

    /**
     * 推送EAS对账单
     */
    @PostMapping("/pushInvoiceNotice")
    public void pushInvoiceNotice(@RequestBody Object data) throws Exception {
        easPurchaseService.pushInvoiceNotice(data);
    }

    /**
     * 查询实时库存
     */
    @PostMapping("/getActualStock")
    public Object getActualStock(@RequestBody Object data) throws Exception {
        return easPurchaseService.getActualStock(data);
    }
}
