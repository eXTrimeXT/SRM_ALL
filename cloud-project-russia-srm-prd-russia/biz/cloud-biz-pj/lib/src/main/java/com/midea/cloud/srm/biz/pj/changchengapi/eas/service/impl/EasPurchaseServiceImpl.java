package com.midea.cloud.srm.biz.pj.changchengapi.eas.service.impl;


import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.dto.LoginResultDto;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasPurchaseService;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasService;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPHeaderElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
@Slf4j
@Component
public class EasPurchaseServiceImpl implements EasPurchaseService {

    @Autowired
    private EasService easService;

    @Value("${eas.namespace}")
    private String namespace;

    @Value("${eas.deliveryNoteAddress:http://10.255.52.96:6894/ormrpc/services/WSSyncPurOrderBillFacade}")
    private String deliveryNoteAddress;

    @Value("${eas.invoiceNoticeAddress:http://10.255.52.96:6894/ormrpc/services/WSWSPaybillWebFacade}")
    private String invoiceNoticeAddress;

    @Value("${eas.actualStockAddress:http://10.255.52.96:6894/ormrpc/services/WSWSInventory}")
    private String actualStockAddress;

    private static final String FALSE = "false";

    private static final String SUCCESS = "success";

    @Override
    public void pushDeliveryNote(Object data) throws Exception {
        Call call = getEasServiceCall(deliveryNoteAddress, "syncPurOrderBill", "syncPurOrderBillReturn");

        String param = JSONUtil.toJsonStr(data);
        log.info("pushDeliveryNote, param:{}", param);
        String result = (String) call.invoke(new Object[]{param});
        log.info("pushDeliveryNote result:{}", result);

        JSONArray jsonArray = JSONUtil.parseArray(result);
        jsonArray.forEach(e -> {
            log.info(e.getClass().toString());
            Map map = (Map) e;
            if (FALSE.equals(map.get(SUCCESS))) {
                throw new BaseException("推送EAS送货单失败：" + map.get("message") + ",请联系发货单对应订单的采购员");
            }
        });
    }

    @Override
    public void pushInvoiceNotice(Object data) throws Exception {
        Call call = getEasServiceCall(invoiceNoticeAddress, "sendData", "sendDataReturn");

        String param = JSONUtil.toJsonStr(data);
        log.info("pushInvoiceNotice param:{}", param);
        String result = (String) call.invoke(new Object[]{param});
        log.info("pushInvoiceNotice result:{}", result);

        JSONObject json = JSONUtil.parseObj(result);
        if (FALSE.equals(json.getStr(SUCCESS))) {
            throw new BaseException("推送EAS对账单失败：" + json.getStr("message"));
        }
    }

    @Override
    public Object getActualStock(Object data) throws Exception {
        Call call = getEasServiceCall(actualStockAddress, "queryData", "queryDataReturn");

        String param = JSONUtil.toJsonStr(data);
        log.info("getActualStock param:{}", param);
        String result = (String) call.invoke(new Object[]{param});
        log.info("getActualStock result:{}", result);

        JSONObject json = JSONUtil.parseObj(result);
        if (FALSE.equals(json.getStr(SUCCESS))) {
            throw new BaseException("查询实时库存失败：" + json.getStr("message"));
        }
        return json.getJSONArray("data");
    }

    public Call getEasServiceCall(String address, String operationName, String returnQName) throws Exception {
        log.info("EasServiceCall address:{} ,operationName：{} ,returnQName：{}", address, operationName, returnQName);

        LoginResultDto loginResultDto = easService.getLoginResultDto();
        Call call = loginResultDto.getCall();
        call.setOperationName(operationName);
        call.setTargetEndpointAddress(address);
        call.setReturnQName(new QName("", returnQName));
        //调用业务接口
        call.setTimeout(60000);
        call.setMaintainSession(true);
        //设置登录返回的session在soap头 "http://login.webservice.bos.kingdee.com"是固定的
        SOAPHeaderElement header = new SOAPHeaderElement(namespace, "SessionId", loginResultDto.getWsContext().getSessionId());
        call.addHeader(header);
        return call;
    }

}
