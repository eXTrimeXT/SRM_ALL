package com.midea.cloud.srm.sou.earnestmoney.service.impl;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.srm.model.sou.sourcing.dto.EarnestMoneyDto;
import com.midea.cloud.srm.model.sou.sourcing.dto.SecurityDepositDto;
import com.midea.cloud.srm.model.supcooperate.report.purchase.dto.PurchaseOrderProcessDto;
import com.midea.cloud.srm.sou.earnestmoney.mapper.EarnestMoneyMapper;
import com.midea.cloud.srm.sou.earnestmoney.service.EarnestMoneyService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/27
 */
@Service
public class EarnestMoneyServiceImpl implements EarnestMoneyService {
    @Autowired
    EarnestMoneyMapper earnestMoneyMapper;
    @Override
    public List<EarnestMoneyDto> list(Map<String, Object> query) {
        PageUtil.startPage(MapUtils.getInteger(query, QlQueryFieldWrapper.field(PurchaseOrderProcessDto::getPageNum).getFieldName(), 1),
                MapUtils.getInteger(query, QlQueryFieldWrapper.field(PurchaseOrderProcessDto::getPageSize).getFieldName(), 15));
       List<EarnestMoneyDto>list=earnestMoneyMapper.list(query);
       return list;
    }
}
