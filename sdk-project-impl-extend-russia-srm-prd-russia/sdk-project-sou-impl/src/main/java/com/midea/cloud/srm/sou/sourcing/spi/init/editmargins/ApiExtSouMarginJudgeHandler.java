package com.midea.cloud.srm.sou.sourcing.spi.init.editmargins;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.sou.enums.MarginHanderModeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidMarginStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouMarginJudgeHandler implements ISouSpiBean {

    @Autowired
    private IExtSouMarginService marginService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public void judgeMarginCanNotNeesPayBeforeHandler(ExtSouMargin param, ExtSouMargin margin, String souType) {

//        if(SouBidMarginStatusEnum.NOT_PAY.getCode().equals(margin.getMarginStatus())) {
//            throw new BaseException("待确认状态时才允许进行操作！");
//        }

        AssertUtils.isTrue(StringUtils.isNotBlank(param.getHanderMode()), "处理方式不允许为空");

        if(Arrays.asList(MarginHanderModeEnum.CAN_NOTPAY.getCode(), MarginHanderModeEnum.ERROR_PAY.getCode()).contains(param.getHanderMode())) {
            AssertUtils.isTrue(StringUtils.isNotBlank(param.getCauseDesc()), "原因说明不允许为空");
        }

        margin.setHanderMode(param.getHanderMode());
        margin.setCauseDesc(param.getCauseDesc());
        if(MarginHanderModeEnum.OFF_LINE.getCode().equals(param.getHanderMode())) {
            margin.setMarginStatus(SouBidMarginStatusEnum.PAY.getCode());
        } else if(MarginHanderModeEnum.ERROR_PAY.getCode().equals(param.getHanderMode())) {
            margin.setMarginStatus(SouBidMarginStatusEnum.FAIL_PAY.getCode());
        }
    }
}
