package com.midea.cloud.srm.sou.earnestmoney.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositRefundStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.dto.EarnestMoneyDto;
import com.midea.cloud.srm.sou.earnestmoney.service.EarnestMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/27
 */
@RequestMapping("earnestMoney")
@RestController
public class EarnestMoneyController {
    @Autowired
    EarnestMoneyService earnestMoneyService;
    @PostMapping("/list")
    public PageInfo<EarnestMoneyDto> list(@RequestBody Map<String,Object> query){
        List<EarnestMoneyDto> list =earnestMoneyService.list(query);

        String beInvoiced = "BE_INVOICED";

        for(EarnestMoneyDto earnestMoneyDto:list){
            /**
             * 根据字典转换"是否开票"状态
             */
            if(earnestMoneyDto.getIStatus()!=null) {
                if (beInvoiced.equals(earnestMoneyDto.getIStatus())) {
                    earnestMoneyDto.setIStatus("否");
                } else {
                    earnestMoneyDto.setIStatus("是");
                }
            }
            else{
                earnestMoneyDto.setIStatus("否");
            }
            /**
             * 根据字典转换意向金退款状态DEPOSIT_REFUND_STATUS为是否退款和是否退款成功
             */
            if(earnestMoneyDto.getDepositRefundStatus() == null) {
                continue;
            }
            if(IntDepositRefundStatusEnum.REFUNDED.getCode().equals(earnestMoneyDto.getDepositRefundStatus()) ||
                    IntDepositRefundStatusEnum.REFUND_FAILED.getCode().equals(earnestMoneyDto.getDepositRefundStatus())
                || IntDepositRefundStatusEnum.REFUNDING.getCode().equals(earnestMoneyDto.getDepositRefundStatus())){
                earnestMoneyDto.setRefund("Y");
                earnestMoneyDto.setRefundSuccess("N");
                if(IntDepositRefundStatusEnum.REFUNDED.getCode().equals(earnestMoneyDto.getDepositRefundStatus())) {
                    earnestMoneyDto.setRefundSuccess("Y");
                }

            }
            else{
                earnestMoneyDto.setRefund("N");
                earnestMoneyDto.setRefundSuccess("N");
            }
        }
        return new PageInfo<>(list);
    }
}
