package com.midea.cloud.srm.sou.securitydeposit.controoler;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.sou.enums.MarginHanderModeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidMarginStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositRefundStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.dto.SecurityDepositDto;
import com.midea.cloud.srm.sou.securitydeposit.service.SecurityDepositService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/24
 */
@RequestMapping("securityDeposit")
@RestController
@Slf4j
public class SecurityDepositController {
    @Autowired
    SecurityDepositService securityDepositService;
    @PostMapping("/list")
    public PageInfo<SecurityDepositDto> securityDepositListPage(
            @RequestBody Map<String,Object> query) {
        List<SecurityDepositDto> list=new ArrayList<>();
        try{
             list = securityDepositService.list(query);
             }catch(Exception e)
        {
          log.error("securityDepositListPage Exception", e);
          throw new BaseException(e.getMessage());
          }

        for(SecurityDepositDto securityDepositDto:list){
            /**
             * 根据英文字典转换为中文
             */
            if(securityDepositDto.getMarginStatus()!=null&& SouBidMarginStatusEnum.PAY.getCode().equals(securityDepositDto.getMarginStatus())){
                securityDepositDto.setMarginStatus("是");
            }
            else{
                securityDepositDto.setMarginStatus("否");
            }

            /**
             * 银行有值，是否收款直接设为是
             */
            if(securityDepositDto!=null&& StringUtils.isNotBlank(securityDepositDto.getPayBank())){
                securityDepositDto.setMarginStatus("是");
            }

            /**
             * 处理方式为线下，是否收款赋值为是
             */
            if(securityDepositDto!=null&&securityDepositDto.getHanderMode()!=null&& MarginHanderModeEnum.OFF_LINE.getCode().equals(securityDepositDto.getHanderMode())){
                securityDepositDto.setMarginStatus("是");
            }

            /**
            NOT_REFUNDED 未退款
            REFUNDED 已退款
            REFUND_FAILED 退款失败
            REFUNDING 退款中*/
            if(securityDepositDto.getRefundStatus()==null){
                securityDepositDto.setRefund("N");
                securityDepositDto.setRefundSuccess("N");
                continue;
            }
            if(IntDepositRefundStatusEnum.REFUNDED.getCode().equals(securityDepositDto.getRefundStatus()))
            {
                securityDepositDto.setRefund("Y");
                securityDepositDto.setRefundSuccess("Y");
            }
            else if(IntDepositRefundStatusEnum.NOT_REFUNDED.getCode().equals(securityDepositDto.getRefundStatus()))
            {
                securityDepositDto.setRefund("N");
                securityDepositDto.setRefundSuccess("N");
            }
            else {
                securityDepositDto.setRefund("Y");
                securityDepositDto.setRefundSuccess("N");
            }
        }
        return new PageInfo<>(list);
    }
}
